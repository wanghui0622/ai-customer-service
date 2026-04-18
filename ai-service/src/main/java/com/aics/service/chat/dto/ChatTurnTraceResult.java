package com.aics.service.chat.dto;

import com.aics.agentrouter.AgentDecision;

import java.util.List;

/**
 * 单次对话编排的完整快照（供调试面板 / 可观测性）。
 */
public record ChatTurnTraceResult(
        String answer,
        /** 路由器原始决策（含 reason） */
        AgentDecision routerDecision,
        /** 经产品开关后是否实际执行了 RAG */
        boolean ragUsed,
        /** 经产品开关后是否实际执行了工具 */
        boolean toolsUsed,
        List<String> ragContext,
        String toolResult,
        String prompt
) {
}
