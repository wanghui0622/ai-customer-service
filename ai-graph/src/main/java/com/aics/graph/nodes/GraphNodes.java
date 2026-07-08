package com.aics.graph.nodes;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.AgentRouter;
import com.aics.graph.config.GraphOrchestrationProperties;
import com.aics.graph.context.OrchestrationContext;
import com.aics.graph.state.ChatGraphState;
import com.aics.model.ToolCallRecord;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.LlmClient;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolCatalog;
import com.aics.spi.ToolExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 图节点实现：薄包装现有 SPI，不内嵌业务领域逻辑。
 */
public final class GraphNodes {

    private static final Logger log = LoggerFactory.getLogger(GraphNodes.class);
    private static final Pattern ORDER_PATTERN = Pattern.compile("(ORD[-_]?\\d+|\\d{6,})");
    private static final ObjectMapper JSON = new ObjectMapper();

    private final ChatMemory memory;
    private final KnowledgeRetriever rag;
    private final ToolExecutor tools;
    private final PromptComposer promptComposer;
    private final LlmClient llm;
    private final AgentRouter agentRouter;
    private final ToolCatalog toolCatalog;
    private final GraphOrchestrationProperties graphProperties;

    public GraphNodes(ChatMemory memory,
                      KnowledgeRetriever rag,
                      ToolExecutor tools,
                      PromptComposer promptComposer,
                      LlmClient llm,
                      AgentRouter agentRouter,
                      ToolCatalog toolCatalog,
                      GraphOrchestrationProperties graphProperties) {
        this.memory = memory;
        this.rag = rag;
        this.tools = tools;
        this.promptComposer = promptComposer;
        this.llm = llm;
        this.agentRouter = agentRouter;
        this.toolCatalog = toolCatalog;
        this.graphProperties = graphProperties;
    }

    public Map<String, Object> loadMemory(ChatGraphState state) {
        String history = memory.loadHistory(state.sessionId());
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.HISTORY, history);
        update.putAll(ChatGraphState.nodeTrace("load_memory"));
        return update;
    }

    public Map<String, Object> route(ChatGraphState state) {
        OrchestrationContext ctx = state.orchestrationContext();
        AgentDecision decision;
        if (graphProperties.isReactEnabled()) {
            decision = heuristicRoute(state.message(), state.history());
        } else if (ctx.agentRouterLlmEnabled()) {
            decision = agentRouter.route(state.message(), state.history());
        } else {
            decision = heuristicRoute(state.message(), state.history());
        }
        String intent = resolveIntent(state.message(), decision);
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.ROUTER_DECISION, decision);
        update.put(ChatGraphState.INTENT, intent);
        update.putAll(ChatGraphState.nodeTrace("route"));
        return update;
    }

    public Map<String, Object> ragRetrieve(ChatGraphState state) {
        OrchestrationContext ctx = state.orchestrationContext();
        AgentDecision decision = state.routerDecision();
        boolean useRag = ctx.ragEnabled() && decision.useRag();
        List<String> context = useRag ? rag.retrieve(state.message()) : Collections.emptyList();
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.RAG_USED, useRag);
        update.put(ChatGraphState.RAG_CONTEXT, List.copyOf(context));
        update.putAll(ChatGraphState.nodeTrace("rag_retrieve"));
        return update;
    }

    public Map<String, Object> toolExecuteLinear(ChatGraphState state) {
        OrchestrationContext ctx = state.orchestrationContext();
        AgentDecision decision = state.routerDecision();
        boolean useTools = ctx.toolsEnabled() && decision.useTools();
        String toolResult = useTools
                ? tools.executeNamed(decision.toolName(), state.message())
                : "";
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.TOOLS_USED, useTools);
        update.put(ChatGraphState.TOOL_RESULT, toolResult);
        update.put(ChatGraphState.AGGREGATED_TOOL_RESULT, toolResult);
        update.putAll(ChatGraphState.nodeTrace("tool_execute"));
        return update;
    }

    public Map<String, Object> toolPlan(ChatGraphState state) {
        OrchestrationContext ctx = state.orchestrationContext();
        if (!ctx.toolsEnabled() || !graphProperties.isReactEnabled()) {
            return Map.of(
                    ChatGraphState.NEXT_TOOL_ACTION, "respond",
                    ChatGraphState.EXECUTED_NODES, List.of("tool_plan")
            );
        }
        if (state.toolLoopCount() >= graphProperties.getMaxToolLoops()) {
            log.warn("tool loop limit reached: {}", graphProperties.getMaxToolLoops());
            return Map.of(
                    ChatGraphState.NEXT_TOOL_ACTION, "respond",
                    ChatGraphState.EXECUTED_NODES, List.of("tool_plan")
            );
        }
        String planPrompt = buildToolPlanPrompt(state);
        String raw = llm.chat(planPrompt);
        ToolPlanDecision plan = parseToolPlan(raw, state);
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.NEXT_TOOL_ACTION, plan.action());
        update.put(ChatGraphState.NEXT_TOOL_NAME, plan.toolName());
        update.put(ChatGraphState.NEXT_TOOL_INPUT, plan.toolInput());
        if ("respond".equals(plan.action()) && state.toolCalls().isEmpty()) {
            AgentDecision routed = heuristicRoute(state.message(), state.history());
            update.put(ChatGraphState.ROUTER_DECISION, routed);
            update.put(ChatGraphState.INTENT, resolveIntent(state.message(), routed));
        }
        update.putAll(ChatGraphState.nodeTrace("tool_plan"));
        return update;
    }

    public Map<String, Object> toolExecuteReact(ChatGraphState state) {
        String toolName = state.nextToolName();
        String toolInput = state.nextToolInput();
        if (toolName.isBlank()) {
            toolInput = state.message();
        }
        String output = tools.executeNamed(toolName, toolInput);
        List<ToolCallRecord> calls = new ArrayList<>(state.toolCalls());
        calls.add(new ToolCallRecord(toolName, toolInput, output, Instant.now()));
        String aggregated = aggregateToolResults(calls);
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.TOOLS_USED, true);
        update.put(ChatGraphState.TOOL_RESULT, output);
        update.put(ChatGraphState.TOOL_CALLS, List.copyOf(calls));
        update.put(ChatGraphState.TOOL_LOOP_COUNT, state.toolLoopCount() + 1);
        update.put(ChatGraphState.AGGREGATED_TOOL_RESULT, aggregated);
        update.put(ChatGraphState.ROUTER_DECISION,
                new AgentDecision(state.ragUsed(), true, toolName, "react-tool"));
        update.putAll(ChatGraphState.nodeTrace("tool_execute"));
        return update;
    }

    public Map<String, Object> humanReview(ChatGraphState state) {
        if (!graphProperties.getApproval().isEnabled()) {
            return ChatGraphState.nodeTrace("human_review");
        }
        String lastTool = state.toolCalls().isEmpty()
                ? state.nextToolName()
                : state.toolCalls().get(state.toolCalls().size() - 1).name();
        if (!isSensitiveTool(lastTool)) {
            return ChatGraphState.nodeTrace("human_review");
        }
        String token = state.approvalToken().isBlank()
                ? UUID.randomUUID().toString()
                : state.approvalToken();
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.PENDING_APPROVAL, state.approvalDecision().isBlank());
        update.put(ChatGraphState.APPROVAL_TOKEN, token);
        update.putAll(ChatGraphState.nodeTrace("human_review"));
        return update;
    }

    public Map<String, Object> buildPrompt(ChatGraphState state) {
        String toolResult = state.aggregatedToolResult();
        if (toolResult.isBlank()) {
            toolResult = state.toolResult();
        }
        String prompt;
        if (!state.toolCalls().isEmpty()) {
            prompt = promptComposer.build(
                    state.history(),
                    state.ragContext(),
                    toolResult,
                    state.message(),
                    state.toolCalls()
            );
        } else {
            prompt = promptComposer.build(
                    state.history(),
                    state.ragContext(),
                    toolResult,
                    state.message()
            );
        }
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.PROMPT, prompt);
        update.putAll(ChatGraphState.nodeTrace("build_prompt"));
        return update;
    }

    public Map<String, Object> llmGenerate(ChatGraphState state) {
        String answer = llm.chat(state.prompt());
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.ANSWER, answer);
        update.putAll(ChatGraphState.nodeTrace("llm_generate"));
        return update;
    }

    public Map<String, Object> saveMemory(ChatGraphState state) {
        memory.saveMessage(state.sessionId(), state.message(), state.answer());
        return ChatGraphState.nodeTrace("save_memory");
    }

    public Map<String, Object> escalateHuman(ChatGraphState state) {
        String answer = "已为您转接人工客服，请稍候。工单队列编号：" + state.sessionId();
        Map<String, Object> update = new HashMap<>();
        update.put(ChatGraphState.ANSWER, answer);
        update.put(ChatGraphState.PROMPT, "");
        update.putAll(ChatGraphState.nodeTrace("escalate_human"));
        return update;
    }

    public String afterToolPlan(ChatGraphState state) {
        return "call_tool".equals(state.nextToolAction()) ? "tool_execute" : "build_prompt";
    }

    public String afterToolExecuteReact(ChatGraphState state) {
        String lastTool = state.toolCalls().isEmpty()
                ? state.nextToolName()
                : state.toolCalls().get(state.toolCalls().size() - 1).name();
        if (graphProperties.getApproval().isEnabled()
                && isSensitiveTool(lastTool)
                && state.approvalDecision().isBlank()) {
            return "human_review";
        }
        if ("call_tool".equals(state.nextToolAction())
                && state.toolLoopCount() < graphProperties.getMaxToolLoops()) {
            return "tool_plan";
        }
        return "build_prompt";
    }

    public String afterHumanReview(ChatGraphState state) {
        if (state.pendingApproval() && state.approvalDecision().isBlank()) {
            return "__interrupt__";
        }
        if ("rejected".equalsIgnoreCase(state.approvalDecision())) {
            return "build_prompt";
        }
        return "tool_plan";
    }

    public String routeByIntent(ChatGraphState state) {
        if (!graphProperties.getSubGraph().isEnabled()) {
            return "consult";
        }
        return switch (state.intent()) {
            case "order" -> "order";
            case "complaint" -> "complaint";
            case "escalate" -> "escalate";
            default -> "consult";
        };
    }

    private static AgentDecision heuristicRoute(String message, String history) {
        String text = (message == null ? "" : message).toLowerCase(Locale.ROOT);
        boolean order = text.contains("订单") || ORDER_PATTERN.matcher(text).find();
        boolean complaint = text.contains("投诉") || text.contains("不满") || text.contains("差评");
        boolean escalate = text.contains("人工") || text.contains("转接");
        boolean question = text.contains("?") || text.contains("？") || text.contains("怎么")
                || text.contains("如何") || text.contains("什么");
        boolean useRag = question || text.length() > 12;
        boolean useTools = order || complaint;
        String toolName = order ? "order_query" : complaint ? "ticket_create" : "";
        String reason = "heuristic-route";
        if (escalate) {
            return new AgentDecision(false, false, "", reason);
        }
        return new AgentDecision(useRag, useTools, toolName, reason);
    }

    private static String resolveIntent(String message, AgentDecision decision) {
        String text = message == null ? "" : message.toLowerCase(Locale.ROOT);
        if (text.contains("人工") || text.contains("转接")) {
            return "escalate";
        }
        if (text.contains("投诉") || text.contains("不满") || "ticket_create".equals(decision.toolName())) {
            return "complaint";
        }
        if (text.contains("订单") || "order_query".equals(decision.toolName())) {
            return "order";
        }
        return "consult";
    }

    private String buildToolPlanPrompt(ChatGraphState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是客服编排助手。根据用户问题决定是否调用工具。\n");
        sb.append("仅输出 JSON：{\"action\":\"call_tool\"|\"respond\",\"toolName\":\"\",\"toolInput\":\"\"}\n\n");
        sb.append("可用工具：\n");
        for (ToolCatalog.ToolDescriptor tool : toolCatalog.listTools()) {
            sb.append("- ").append(tool.name()).append(": ").append(tool.description()).append('\n');
        }
        if (!state.toolCalls().isEmpty()) {
            sb.append("\n已执行工具：\n");
            int i = 1;
            for (ToolCallRecord call : state.toolCalls()) {
                sb.append(i++).append(". ").append(call.name()).append(" => ").append(call.output()).append('\n');
            }
        }
        sb.append("\n历史：\n").append(state.history()).append('\n');
        sb.append("用户：").append(state.message()).append('\n');
        return sb.toString();
    }

    private ToolPlanDecision parseToolPlan(String raw, ChatGraphState state) {
        try {
            JsonNode node = JSON.readTree(extractJson(raw));
            String action = node.path("action").asText("respond");
            String toolName = node.path("toolName").asText("");
            String toolInput = node.path("toolInput").asText(state.message());
            if ("call_tool".equals(action) && toolName.isBlank()) {
                action = "respond";
            }
            return new ToolPlanDecision(action, toolName, toolInput);
        } catch (Exception e) {
            log.debug("failed to parse tool plan, fallback respond: {}", e.getMessage());
            return new ToolPlanDecision("respond", "", state.message());
        }
    }

    private static String extractJson(String raw) {
        if (raw == null) {
            return "{}";
        }
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return raw;
    }

    private static String aggregateToolResults(List<ToolCallRecord> calls) {
        if (calls.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < calls.size(); i++) {
            ToolCallRecord call = calls.get(i);
            sb.append('[').append(i + 1).append("] ").append(call.name()).append(": ")
                    .append(call.output()).append('\n');
        }
        return sb.toString().trim();
    }

    private boolean isSensitiveTool(String toolName) {
        if (toolName == null || toolName.isBlank()) {
            return false;
        }
        String configured = graphProperties.getApproval().getSensitiveTools();
        for (String part : configured.split(",")) {
            if (toolName.equals(part.trim())) {
                return true;
            }
        }
        return false;
    }

    private record ToolPlanDecision(String action, String toolName, String toolInput) {
    }
}
