package com.aics.prompt.factory;

/**
 * 轻量 Prompt 版本，用于 A/B 或渐进升级（与配置 {@code aics.prompt.default-version} 对齐）。
 */
public enum PromptVersion {
    /** 第一版默认话术与结构。 */
    V1,
    /** 第二版迭代话术（如更强调依据、工具协议等）。 */
    V2
}
