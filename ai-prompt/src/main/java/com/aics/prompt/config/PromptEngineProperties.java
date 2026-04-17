package com.aics.prompt.config;

import com.aics.prompt.factory.PromptVersion;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Prompt 引擎配置：默认版本与日志保留条数（内存环形保留）。
 * <p>
 * 配置前缀：{@code aics.prompt}。
 */
@ConfigurationProperties(prefix = "aics.prompt")
public class PromptEngineProperties {

    /**
     * 未显式指定版本时使用的 Prompt 版本。
     */
    private PromptVersion defaultVersion = PromptVersion.V1;

    /**
     * 内存中保留的最近日志条数上限。
     */
    private int logRetentionMax = 2000;

    /**
     * @return 默认 Prompt 版本
     */
    public PromptVersion getDefaultVersion() {
        return defaultVersion;
    }

    /**
     * @param defaultVersion 默认版本，对应 {@code aics.prompt.default-version}
     */
    public void setDefaultVersion(PromptVersion defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    /**
     * @return 内存日志最大条数
     */
    public int getLogRetentionMax() {
        return logRetentionMax;
    }

    /**
     * @param logRetentionMax 最大条数，对应 {@code aics.prompt.log-retention-max}
     */
    public void setLogRetentionMax(int logRetentionMax) {
        this.logRetentionMax = logRetentionMax;
    }
}
