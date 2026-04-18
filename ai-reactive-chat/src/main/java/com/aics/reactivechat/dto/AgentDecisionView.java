package com.aics.reactivechat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 前端 Agent 面板：与路由器决策对齐的展示结构（布尔值为<strong>实际是否执行</strong>）。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgentDecisionView(
        boolean useRag,
        boolean useTools,
        String toolName,
        String reason
) {
}
