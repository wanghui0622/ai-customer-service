package com.aics.model.mapper;

import com.aics.model.dto.ChatRequestDto;
import com.aics.model.entity.ChatMessageEntity;

/**
 * {@link ChatRequestDto} 与 {@link ChatMessageEntity} 之间的静态转换，无状态。
 */
public final class ChatMessageMapper {

    private ChatMessageMapper() {
    }

    /**
     * 将 HTTP 请求 DTO 转为一条用户角色消息实体。
     *
     * @param dto 请求体
     * @return 用户消息实体
     */
    public static ChatMessageEntity from(ChatRequestDto dto) {
        return new ChatMessageEntity(dto.sessionId(), "user", dto.message());
    }
}
