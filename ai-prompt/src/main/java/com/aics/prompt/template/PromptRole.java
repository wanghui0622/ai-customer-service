package com.aics.prompt.template;

/**
 * LLM 消息角色，对应 system / user / assistant 三段式提示词。
 */
public enum PromptRole {
    /** 系统级指令，定义助手身份与行为约束。 */
    SYSTEM,
    /** 用户侧输入内容。 */
    USER,
    /** 助手侧内容，常用于 few-shot 示例或前缀。 */
    ASSISTANT
}
