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
}
