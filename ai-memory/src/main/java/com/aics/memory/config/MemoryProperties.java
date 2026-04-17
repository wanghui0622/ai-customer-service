package com.aics.memory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 记忆模块配置，前缀 {@code aics.memory}。
 */
@ConfigurationProperties(prefix = "aics.memory")
public class MemoryProperties {

    /**
     * 存储后端：{@code in-memory}（默认，单机 JVM）、{@code redis}（占位实现，需自行补全）。
     */
    private String store = "in-memory";

    /**
     * 会话历史经 {@link com.aics.memory.format.MemoryFormatter} 输出时的最大字符数；
     * 超出时截断并保留尾部，避免 Prompt 过长；后续可改为摘要（summarization）。
     */
    private int maxHistoryChars = 8000;

    public String getStore() {
        return store;
    }

    /**
     * @param store {@code in-memory} 或 {@code redis}
     */
    public void setStore(String store) {
        this.store = store;
    }

    public int getMaxHistoryChars() {
        return maxHistoryChars;
    }

    /**
     * @param maxHistoryChars 最大字符数，≤0 时格式化结果可能为空串
     */
    public void setMaxHistoryChars(int maxHistoryChars) {
        this.maxHistoryChars = maxHistoryChars;
    }
}
