package com.aics.agentrouter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * LLM 或规则路由器对「本轮应启用哪些能力」的结构化决策。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgentDecision(
        boolean useRag,
        boolean useTools,
        String toolName,
        String reason
) implements Serializable {
    public AgentDecision {
        toolName = toolName == null ? "" : toolName.trim();
        reason = reason == null ? "" : reason.trim();
    }

    public static AgentDecision none() {
        return new AgentDecision(false, false, "", "");
    }
}
