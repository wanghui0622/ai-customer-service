package com.aics.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 可观测性与调试响应开关。
 */
@ConfigurationProperties(prefix = "aics.observability")
public class ObservabilityProperties {

    /**
     * 是否在聊天 API 中返回完整 prompt、RAG 片段等编排快照。
     * 生产环境建议 false，仅在内网调试或授权后开启。
     */
    private boolean exposePromptTrace = false;

    public boolean isExposePromptTrace() {
        return exposePromptTrace;
    }

    public void setExposePromptTrace(boolean exposePromptTrace) {
        this.exposePromptTrace = exposePromptTrace;
    }
}
