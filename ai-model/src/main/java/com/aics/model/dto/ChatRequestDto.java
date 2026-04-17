package com.aics.model.dto;

/**
 * 聊天请求入参 DTO。
 *
 * @param sessionId 会话 ID
 * @param message   用户输入内容
 */
public record ChatRequestDto(String sessionId, String message) {
}
