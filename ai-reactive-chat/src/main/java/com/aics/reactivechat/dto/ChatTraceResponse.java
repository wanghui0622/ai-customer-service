package com.aics.reactivechat.dto;

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
        String prompt
) {
    public static ChatTraceResponse fromTrace(ChatTurnTraceResult t) {
        var d = t.routerDecision();
        String name = (t.toolsUsed() && d.toolName() != null && !d.toolName().isBlank())
                ? d.toolName()
                : "";
        return new ChatTraceResponse(
                t.answer(),
                new AgentDecisionView(t.ragUsed(), t.toolsUsed(), name, d.reason()),
                t.ragContext(),
                t.toolResult() == null ? "" : t.toolResult(),
                t.prompt()
        );
    }

    /**
     * 未授权展示编排快照时：仅返回模型答复，其余字段占位。
     */
    public static ChatTraceResponse answerOnly(String answer) {
        return new ChatTraceResponse(
                answer,
                new AgentDecisionView(false, false, "", ""),
                Collections.emptyList(),
                "",
                ""
        );
    }
}
