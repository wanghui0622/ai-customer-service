package com.aics.memory.model;

/**
 * 单轮对话，短期记忆的原子单元；按时间顺序追加在会话（sessionId）下。
 *
 * @param userMessage       用户侧内容
 * @param assistantMessage 助手侧回复
 */
public record MessageTurn(String userMessage, String assistantMessage) {
}
