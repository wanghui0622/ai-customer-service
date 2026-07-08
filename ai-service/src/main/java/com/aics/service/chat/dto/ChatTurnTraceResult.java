package com.aics.service.chat.dto;

import com.aics.agentrouter.AgentDecision;
import com.aics.model.ToolCallRecord;

import java.util.Collections;
import java.util.List;

/**
 * 单次对话编排的完整快照（供调试面板 / 可观测性）。
 */
public record ChatTurnTraceResult(
        String answer,
        AgentDecision routerDecision,
        boolean ragUsed,
        boolean toolsUsed,
        List<String> ragContext,
        String toolResult,
        String prompt,
        List<ToolCallRecord> toolCalls,
        List<String> executedNodes,
        String graphExecutionId,
        long durationMs,
        boolean pendingApproval,
        String approvalToken
) {
    public ChatTurnTraceResult {
        toolCalls = toolCalls == null ? List.of() : List.copyOf(toolCalls);
        executedNodes = executedNodes == null ? List.of() : List.copyOf(executedNodes);
        graphExecutionId = graphExecutionId == null ? "" : graphExecutionId;
        approvalToken = approvalToken == null ? "" : approvalToken;
    }

    public static ChatTurnTraceResult of(String answer,
                                         AgentDecision routerDecision,
                                         boolean ragUsed,
                                         boolean toolsUsed,
                                         List<String> ragContext,
                                         String toolResult,
                                         String prompt) {
        return new ChatTurnTraceResult(
                answer,
                routerDecision,
                ragUsed,
                toolsUsed,
                ragContext,
                toolResult,
                prompt,
                Collections.emptyList(),
                Collections.emptyList(),
                "",
                0L,
                false,
                ""
        );
    }
}
