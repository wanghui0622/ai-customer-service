package com.aics.agentrouter;

/**
 * 决定本轮对话是否启用 RAG / Tools（可扩展更多档位）。
 * <p>
 * 典型实现：{@link LlmAgentRouter}（模型输出 JSON）、固定决策（评估/单测）、规则回退。
 */
@FunctionalInterface
public interface AgentRouter {

    /**
     * @param message 用户当前输入
     * @param history 已格式化的会话历史（可为空串）
     */
    AgentDecision route(String message, String history);
}
