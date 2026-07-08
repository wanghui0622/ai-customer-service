package com.aics.graph;

import com.aics.agentrouter.AgentRouter;
import com.aics.graph.config.GraphOrchestrationProperties;
import com.aics.graph.context.OrchestrationContext;
import com.aics.graph.nodes.GraphNodes;
import com.aics.graph.state.ChatGraphState;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.LlmClient;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolCatalog;
import com.aics.spi.ToolExecutor;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import com.aics.graph.checkpoint.RedisGraphCheckpointSaver;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * LangGraph4j 客服编排图：线性等价 + ReAct 多步工具 + 子图路由 + interrupt。
 */
@Service
public class CustomerServiceGraph {

    private final GraphNodes nodes;
    private final ChatMemory memory;
    private final GraphOrchestrationProperties graphProperties;
    private final CompiledGraph<ChatGraphState> compiledGraph;
    private final CompiledGraph<ChatGraphState> promptGraph;
    private final BaseCheckpointSaver checkpointSaver;

    public CustomerServiceGraph(ChatMemory memory,
                                KnowledgeRetriever rag,
                                ToolExecutor tools,
                                PromptComposer promptComposer,
                                LlmClient llm,
                                AgentRouter agentRouter,
                                ToolCatalog toolCatalog,
                                GraphOrchestrationProperties graphProperties) throws GraphStateException {
        this.graphProperties = graphProperties;
        this.memory = memory;
        this.nodes = new GraphNodes(
                memory, rag, tools, promptComposer, llm, agentRouter, toolCatalog, graphProperties);
        this.checkpointSaver = createCheckpointSaver(graphProperties);
        this.compiledGraph = buildGraph(false).compile(compileConfig());
        this.promptGraph = buildGraph(true).compile(compileConfig());
    }

    public ChatGraphState invokeUntilPrompt(String sessionId, String message, OrchestrationContext context) {
        String executionId = UUID.randomUUID().toString();
        Map<String, Object> inputs = ChatGraphState.initial(sessionId, message, context, executionId);
        RunnableConfig config = RunnableConfig.builder().threadId(sessionId).build();
        try {
            return promptGraph.invoke(inputs, config)
                    .orElseThrow(() -> new IllegalStateException("prompt graph returned empty state"));
        } catch (Exception e) {
            throw new IllegalStateException("prompt graph invocation failed: " + e.getMessage(), e);
        }
    }

    public void saveAnswer(String sessionId, String message, String answer) {
        memory.saveMessage(sessionId, message, answer);
    }

    public ChatGraphState invoke(String sessionId, String message, OrchestrationContext context) {
        long started = System.currentTimeMillis();
        String executionId = UUID.randomUUID().toString();
        Map<String, Object> inputs = ChatGraphState.initial(sessionId, message, context, executionId);
        RunnableConfig config = RunnableConfig.builder().threadId(sessionId).build();
        try {
            ChatGraphState finalState = compiledGraph.invoke(inputs, config)
                    .orElseThrow(() -> new IllegalStateException("graph returned empty state"));
            return withDuration(finalState, started);
        } catch (Exception e) {
            throw new IllegalStateException("graph invocation failed: " + e.getMessage(), e);
        }
    }

    public ChatGraphState resume(String sessionId, String approvalToken, String decision) {
        long started = System.currentTimeMillis();
        RunnableConfig config = RunnableConfig.builder().threadId(sessionId).build();
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put(ChatGraphState.APPROVAL_TOKEN, approvalToken == null ? "" : approvalToken);
            updates.put(ChatGraphState.APPROVAL_DECISION, decision == null ? "" : decision);
            updates.put(ChatGraphState.PENDING_APPROVAL, false);
            RunnableConfig updated = compiledGraph.updateState(config, updates);
            ChatGraphState finalState = compiledGraph.invoke(Map.of(), updated)
                    .orElseThrow(() -> new IllegalStateException("resume returned empty state"));
            return withDuration(finalState, started);
        } catch (Exception e) {
            throw new IllegalStateException("graph resume failed: " + e.getMessage(), e);
        }
    }

    private StateGraph<ChatGraphState> buildGraph(boolean promptOnly) throws GraphStateException {
        String afterRag = graphProperties.isReactEnabled() ? "tool_plan" : "tool_execute_linear";
        StateGraph<ChatGraphState> graph = new StateGraph<>(ChatGraphState.SCHEMA, ChatGraphState::new)
                .addNode("load_memory", node_async(nodes::loadMemory))
                .addNode("route", node_async(nodes::route))
                .addNode("rag_retrieve", node_async(nodes::ragRetrieve))
                .addNode("tool_execute_linear", node_async(nodes::toolExecuteLinear))
                .addNode("tool_plan", node_async(nodes::toolPlan))
                .addNode("tool_execute", node_async(nodes::toolExecuteReact))
                .addNode("human_review", node_async(nodes::humanReview))
                .addNode("build_prompt", node_async(nodes::buildPrompt))
                .addNode("llm_generate", node_async(nodes::llmGenerate))
                .addNode("save_memory", node_async(nodes::saveMemory))
                .addNode("escalate_human", node_async(nodes::escalateHuman))
                .addEdge(START, "load_memory")
                .addEdge("load_memory", "route")
                .addConditionalEdges("route", edge_async(nodes::routeByIntent), Map.of(
                        "consult", "rag_retrieve",
                        "order", "rag_retrieve",
                        "complaint", "rag_retrieve",
                        "escalate", "escalate_human"
                ))
                .addEdge("escalate_human", "save_memory")
                .addEdge("rag_retrieve", afterRag)
                .addEdge("tool_execute_linear", "build_prompt")
                .addConditionalEdges("tool_plan", edge_async(nodes::afterToolPlan), Map.of(
                        "tool_execute", "tool_execute",
                        "build_prompt", "build_prompt"
                ))
                .addConditionalEdges("tool_execute", edge_async(nodes::afterToolExecuteReact), Map.of(
                        "tool_plan", "tool_plan",
                        "human_review", "human_review",
                        "build_prompt", "build_prompt"
                ))
                .addConditionalEdges("human_review", edge_async(nodes::afterHumanReview), Map.of(
                        "tool_plan", "tool_plan",
                        "build_prompt", "build_prompt",
                        "__interrupt__", END
                ))
                .addEdge("build_prompt", promptOnly ? END : "llm_generate");
        if (!promptOnly) {
            graph = graph
                .addEdge("llm_generate", "save_memory")
                .addEdge("save_memory", END);
        }
        return graph;
    }

    private BaseCheckpointSaver createCheckpointSaver(GraphOrchestrationProperties properties) {
        if ("redis".equalsIgnoreCase(properties.getCheckpoint().getStore())) {
            return new RedisGraphCheckpointSaver();
        }
        return new MemorySaver();
    }

    private CompileConfig compileConfig() {
        CompileConfig.Builder builder = CompileConfig.builder()
                .recursionLimit(graphProperties.getMaxSteps());
        if (graphProperties.getApproval().isEnabled()) {
            builder.checkpointSaver(checkpointSaver)
                    .interruptBefore("human_review");
        }
        return builder.build();
    }

    private static ChatGraphState withDuration(ChatGraphState state, long startedAt) {
        Map<String, Object> update = Map.of(ChatGraphState.DURATION_MS, System.currentTimeMillis() - startedAt);
        return new ChatGraphState(state.mergeWith(update, ChatGraphState.SCHEMA));
    }
}
