package com.aics.reactivechat.dto;

import com.aics.model.ToolCallRecord;
import com.aics.service.chat.dto.ChatTurnTraceResult;

import java.util.Collections;
import java.util.List;

/**
 * POST /api/chat 完整响应：答复 + 编排调试信息。
 */
public record ChatTraceResponse(
        String answer,
        AgentDecisionView agentDecision,
        List<String> ragContext,
        String toolResult,
        String prompt,
        List<ToolCallView> toolCalls,
        List<String> executedNodes,
        String graphExecutionId,
        long durationMs,
        boolean pendingApproval,
        String approvalToken
) {
    public record ToolCallView(String name, String input, String output) {
        public static ToolCallView from(ToolCallRecord record) {
            return new ToolCallView(record.name(), record.input(), record.output());
        }
    }

    public static ChatTraceResponse fromTrace(ChatTurnTraceResult t) {
        var d = t.routerDecision();
        String name = (t.toolsUsed() && d.toolName() != null && !d.toolName().isBlank())
                ? d.toolName()
                : "";
        List<ToolCallView> calls = t.toolCalls().stream().map(ToolCallView::from).toList();
        return new ChatTraceResponse(
                t.answer(),
                new AgentDecisionView(t.ragUsed(), t.toolsUsed(), name, d.reason()),
                t.ragContext(),
                t.toolResult() == null ? "" : t.toolResult(),
                t.prompt(),
                calls,
                t.executedNodes(),
                t.graphExecutionId(),
                t.durationMs(),
                t.pendingApproval(),
                t.approvalToken()
        );
    }

    public static ChatTraceResponse answerOnly(String answer) {
        return new ChatTraceResponse(
                answer,
                new AgentDecisionView(false, false, "", ""),
                Collections.emptyList(),
                "",
                "",
                Collections.emptyList(),
                Collections.emptyList(),
                "",
                0L,
                false,
                ""
        );
    }
}
