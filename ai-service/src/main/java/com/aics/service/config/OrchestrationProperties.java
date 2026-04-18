package com.aics.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 编排层开关与阈值，前缀 {@code aics.orchestration}。
 */
@ConfigurationProperties(prefix = "aics.orchestration")
public class OrchestrationProperties {

    /**
     * 是否允许走知识检索（关闭则永远不调用 RAG）。
     */
    private boolean ragEnabled = true;

    /**
     * 是否允许走工具执行（关闭则永远不调用 ToolExecutor）。
     */
    private boolean toolsEnabled = true;

    /**
     * 用户消息达到该长度时，可作为「需要知识」的弱信号之一。
     */
    private int minMessageLengthHintForRag = 12;

    /**
     * 是否启用 LLM 路由（{@link com.aics.agentrouter.LlmAgentRouter}）。
     * 为 false 时仅使用规则路由器（与原先启发式门控等价）。
     */
    private boolean agentRouterLlmEnabled = true;

    public boolean isRagEnabled() {
        return ragEnabled;
    }

    public void setRagEnabled(boolean ragEnabled) {
        this.ragEnabled = ragEnabled;
    }

    public boolean isToolsEnabled() {
        return toolsEnabled;
    }

    public void setToolsEnabled(boolean toolsEnabled) {
        this.toolsEnabled = toolsEnabled;
    }

    public int getMinMessageLengthHintForRag() {
        return minMessageLengthHintForRag;
    }

    public void setMinMessageLengthHintForRag(int minMessageLengthHintForRag) {
        this.minMessageLengthHintForRag = minMessageLengthHintForRag;
    }

    public boolean isAgentRouterLlmEnabled() {
        return agentRouterLlmEnabled;
    }

    public void setAgentRouterLlmEnabled(boolean agentRouterLlmEnabled) {
        this.agentRouterLlmEnabled = agentRouterLlmEnabled;
    }
}
