package com.aics.service.chat;

import com.aics.graph.CustomerServiceGraph;
import com.aics.graph.context.OrchestrationContext;
import com.aics.graph.state.ChatGraphState;
import com.aics.service.chat.dto.ChatTurnTraceResult;
import com.aics.service.config.OrchestrationEngine;
import com.aics.service.config.OrchestrationProperties;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.LlmClient;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolExecutor;
import com.aics.agentrouter.AgentRouter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 系统唯一编排入口：按配置委托线性管道或 LangGraph 图编排。
 */
@Service
public class AiChatService {

    public void chatStream(String sessionId, String message, java.util.function.Consumer<String> onChunk) {
        if (!(llm instanceof com.aics.spi.StreamingLlmClient streaming) || !streaming.supportsStreaming()) {
            onChunk.accept(chat(sessionId, message));
            return;
        }
        String prompt;
        if (orchestrationProperties.getEngine() == OrchestrationEngine.GRAPH) {
            ChatGraphState state = customerServiceGraph.invokeUntilPrompt(
                    sessionId, message, toContext(orchestrationProperties));
            prompt = state.prompt();
            StringBuilder answer = new StringBuilder();
            streaming.stream(prompt, chunk -> {
                answer.append(chunk);
                onChunk.accept(chunk);
            });
            customerServiceGraph.saveAnswer(sessionId, message, answer.toString());
            return;
        }
        prompt = linearChatPipeline.buildPromptOnly(sessionId, message);
        StringBuilder answer = new StringBuilder();
        streaming.stream(prompt, chunk -> {
            answer.append(chunk);
            onChunk.accept(chunk);
        });
        linearChatPipeline.saveAnswer(sessionId, message, answer.toString());
    }

    private final LinearChatPipeline linearChatPipeline;
    private final CustomerServiceGraph customerServiceGraph;
    private final OrchestrationProperties orchestrationProperties;
    private final LlmClient llm;

    public AiChatService(ChatMemory memory,
                         KnowledgeRetriever rag,
                         ToolExecutor tools,
                         PromptComposer promptComposer,
                         LlmClient llm,
                         AgentRouter agentRouter,
                         OrchestrationProperties orchestrationProperties,
                         CustomerServiceGraph customerServiceGraph) {
        this.linearChatPipeline = new LinearChatPipeline(
                memory, rag, tools, promptComposer, llm, agentRouter, orchestrationProperties);
        this.customerServiceGraph = Objects.requireNonNull(customerServiceGraph);
        this.orchestrationProperties = Objects.requireNonNull(orchestrationProperties);
        this.llm = llm;
    }

    public String chat(String sessionId, String message) {
        return chatWithTrace(sessionId, message).answer();
    }

    public ChatTurnTraceResult chatWithTrace(String sessionId, String message) {
        if (orchestrationProperties.getEngine() == OrchestrationEngine.GRAPH) {
            return fromGraphState(customerServiceGraph.invoke(
                    sessionId,
                    message,
                    toContext(orchestrationProperties)));
        }
        return linearChatPipeline.chatWithTrace(sessionId, message);
    }

    public ChatTurnTraceResult resumeWithApproval(String sessionId, String approvalToken, String decision) {
        ChatGraphState state = customerServiceGraph.resume(sessionId, approvalToken, decision);
        return fromGraphState(state);
    }

    private static OrchestrationContext toContext(OrchestrationProperties properties) {
        return new OrchestrationContext(
                properties.isRagEnabled(),
                properties.isToolsEnabled(),
                properties.isAgentRouterLlmEnabled());
    }

    private static ChatTurnTraceResult fromGraphState(ChatGraphState state) {
        return new ChatTurnTraceResult(
                state.answer(),
                state.routerDecision(),
                state.ragUsed(),
                state.toolsUsed(),
                List.copyOf(state.ragContext()),
                state.toolResult(),
                state.prompt(),
                List.copyOf(state.toolCalls()),
                List.copyOf(state.executedNodes()),
                state.graphExecutionId(),
                state.durationMs(),
                state.pendingApproval(),
                state.approvalToken()
        );
    }
}
