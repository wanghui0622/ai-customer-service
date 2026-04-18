package com.aics.agentrouter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * LLM 或规则路由器对「本轮应启用哪些能力」的结构化决策。
 *
 * @param useRag    是否检索知识库
 * @param useTools  是否执行工具
 * @param toolName  显式工具名（如 {@code order_query}）；可空，空时由 {@link com.aics.spi.ToolExecutor} 走启发式路由
 * @param reason    简短理由（可审计、可调试）
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgentDecision(
        boolean useRag,
        boolean useTools,
        String toolName,
        String reason
) {
    public AgentDecision {
        toolName = toolName == null ? "" : toolName.trim();
        reason = reason == null ? "" : reason.trim();
    }

    public static AgentDecision none() {
        return new AgentDecision(false, false, "", "");
    }
}
