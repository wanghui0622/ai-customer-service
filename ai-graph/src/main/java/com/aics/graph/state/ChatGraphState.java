package com.aics.graph.state;

import com.aics.agentrouter.AgentDecision;
import com.aics.graph.context.OrchestrationContext;
import com.aics.model.ToolCallRecord;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LangGraph 共享状态，字段与 {@link com.aics.service.chat.dto.ChatTurnTraceResult} 对齐。
 */
public class ChatGraphState extends AgentState {

    public static final String SESSION_ID = "sessionId";
    public static final String MESSAGE = "message";
    public static final String HISTORY = "history";
    public static final String INTENT = "intent";
    public static final String ROUTER_DECISION = "routerDecision";
    public static final String RAG_USED = "ragUsed";
    public static final String TOOLS_USED = "toolsUsed";
    public static final String RAG_CONTEXT = "ragContext";
    public static final String TOOL_RESULT = "toolResult";
    public static final String TOOL_CALLS = "toolCalls";
    public static final String TOOL_LOOP_COUNT = "toolLoopCount";
    public static final String AGGREGATED_TOOL_RESULT = "aggregatedToolResult";
    public static final String PROMPT = "prompt";
    public static final String ANSWER = "answer";
    public static final String ORCHESTRATION_CONTEXT = "orchestrationContext";
    public static final String EXECUTED_NODES = "executedNodes";
    public static final String GRAPH_EXECUTION_ID = "graphExecutionId";
    public static final String DURATION_MS = "durationMs";
    public static final String PENDING_APPROVAL = "pendingApproval";
    public static final String APPROVAL_TOKEN = "approvalToken";
    public static final String APPROVAL_DECISION = "approvalDecision";
    public static final String NEXT_TOOL_ACTION = "nextToolAction";
    public static final String NEXT_TOOL_NAME = "nextToolName";
    public static final String NEXT_TOOL_INPUT = "nextToolInput";
    public static final String STEP_COUNT = "stepCount";

    public static final Map<String, Channel<?>> SCHEMA = buildSchema();

    public ChatGraphState(Map<String, Object> initData) {
        super(initData);
    }

    private static Map<String, Channel<?>> buildSchema() {
        Map<String, Channel<?>> schema = new HashMap<>();
        schema.put(SESSION_ID, replaceChannel());
        schema.put(MESSAGE, replaceChannel());
        schema.put(HISTORY, replaceChannel());
        schema.put(INTENT, replaceChannel());
        schema.put(ROUTER_DECISION, replaceChannel());
        schema.put(RAG_USED, replaceChannel());
        schema.put(TOOLS_USED, replaceChannel());
        schema.put(RAG_CONTEXT, replaceChannel());
        schema.put(TOOL_RESULT, replaceChannel());
        schema.put(TOOL_CALLS, replaceChannel());
        schema.put(TOOL_LOOP_COUNT, replaceChannel());
        schema.put(AGGREGATED_TOOL_RESULT, replaceChannel());
        schema.put(PROMPT, replaceChannel());
        schema.put(ANSWER, replaceChannel());
        schema.put(ORCHESTRATION_CONTEXT, replaceChannel());
        schema.put(EXECUTED_NODES, Channels.appender(ArrayList::new));
        schema.put(GRAPH_EXECUTION_ID, replaceChannel());
        schema.put(DURATION_MS, replaceChannel());
        schema.put(PENDING_APPROVAL, replaceChannel());
        schema.put(APPROVAL_TOKEN, replaceChannel());
        schema.put(APPROVAL_DECISION, replaceChannel());
        schema.put(NEXT_TOOL_ACTION, replaceChannel());
        schema.put(NEXT_TOOL_NAME, replaceChannel());
        schema.put(NEXT_TOOL_INPUT, replaceChannel());
        schema.put(STEP_COUNT, replaceChannel());
        return Collections.unmodifiableMap(schema);
    }

    private static <T> Channel<T> replaceChannel() {
        return Channels.base((oldValue, newValue) -> newValue);
    }

    public String sessionId() {
        return value(SESSION_ID, "");
    }

    public String message() {
        return value(MESSAGE, "");
    }

    public String history() {
        return value(HISTORY, "");
    }

    public String intent() {
        return value(INTENT, "consult");
    }

    public AgentDecision routerDecision() {
        return value(ROUTER_DECISION, AgentDecision.none());
    }

    public boolean ragUsed() {
        return value(RAG_USED, false);
    }

    public boolean toolsUsed() {
        return value(TOOLS_USED, false);
    }

    @SuppressWarnings("unchecked")
    public List<String> ragContext() {
        return value(RAG_CONTEXT).map(v -> (List<String>) v).orElse(List.of());
    }

    public String toolResult() {
        return value(TOOL_RESULT, "");
    }

    @SuppressWarnings("unchecked")
    public List<ToolCallRecord> toolCalls() {
        return value(TOOL_CALLS).map(v -> (List<ToolCallRecord>) v).orElse(List.of());
    }

    public int toolLoopCount() {
        return value(TOOL_LOOP_COUNT, 0);
    }

    public String aggregatedToolResult() {
        return value(AGGREGATED_TOOL_RESULT, "");
    }

    public String prompt() {
        return value(PROMPT, "");
    }

    public String answer() {
        return value(ANSWER, "");
    }

    public OrchestrationContext orchestrationContext() {
        return value(ORCHESTRATION_CONTEXT, OrchestrationContext.defaults());
    }

    @SuppressWarnings("unchecked")
    public List<String> executedNodes() {
        return value(EXECUTED_NODES).map(v -> (List<String>) v).orElse(List.of());
    }

    public String graphExecutionId() {
        return value(GRAPH_EXECUTION_ID, "");
    }

    public long durationMs() {
        return value(DURATION_MS, 0L);
    }

    public boolean pendingApproval() {
        return value(PENDING_APPROVAL, false);
    }

    public String approvalToken() {
        return value(APPROVAL_TOKEN, "");
    }

    public String approvalDecision() {
        return value(APPROVAL_DECISION, "");
    }

    public String nextToolAction() {
        return value(NEXT_TOOL_ACTION, "");
    }

    public String nextToolName() {
        return value(NEXT_TOOL_NAME, "");
    }

    public String nextToolInput() {
        return value(NEXT_TOOL_INPUT, "");
    }

    public int stepCount() {
        return value(STEP_COUNT, 0);
    }

    public static Map<String, Object> initial(
            String sessionId,
            String message,
            OrchestrationContext context,
            String graphExecutionId) {
        Map<String, Object> init = new HashMap<>();
        init.put(SESSION_ID, sessionId);
        init.put(MESSAGE, message);
        init.put(HISTORY, "");
        init.put(INTENT, "consult");
        init.put(ROUTER_DECISION, AgentDecision.none());
        init.put(RAG_USED, false);
        init.put(TOOLS_USED, false);
        init.put(RAG_CONTEXT, List.<String>of());
        init.put(TOOL_RESULT, "");
        init.put(TOOL_CALLS, List.<ToolCallRecord>of());
        init.put(TOOL_LOOP_COUNT, 0);
        init.put(AGGREGATED_TOOL_RESULT, "");
        init.put(PROMPT, "");
        init.put(ANSWER, "");
        init.put(ORCHESTRATION_CONTEXT, context);
        init.put(GRAPH_EXECUTION_ID, graphExecutionId);
        init.put(DURATION_MS, 0L);
        init.put(PENDING_APPROVAL, false);
        init.put(APPROVAL_TOKEN, "");
        init.put(APPROVAL_DECISION, "");
        init.put(NEXT_TOOL_ACTION, "");
        init.put(NEXT_TOOL_NAME, "");
        init.put(NEXT_TOOL_INPUT, "");
        init.put(STEP_COUNT, 0);
        return init;
    }

    public static Map<String, Object> nodeTrace(String nodeName) {
        return Map.of(EXECUTED_NODES, List.of(nodeName));
    }
}
