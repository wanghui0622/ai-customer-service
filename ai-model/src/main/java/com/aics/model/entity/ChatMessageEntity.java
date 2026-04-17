package com.aics.model.entity;

/**
 * 聊天消息领域实体（持久化或传输用）。
 *
 * @param sessionId 会话标识
 * @param role      消息角色，如 user / assistant / system
 * @param content   消息正文
 */
public record ChatMessageEntity(String sessionId, String role, String content) {
}
