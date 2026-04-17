package com.aics.service.chat;

import com.aics.core.chat.ChatService;
import org.springframework.stereotype.Service;

/**
 * 面向 Web 层的聊天门面，将请求委托给 {@link ChatService}，便于后续插入鉴权、限流、审计等横切逻辑。
 */
@Service
public class CustomerChatFacade {

    /** 核心会话聊天服务。 */
    private final ChatService chatService;

    /**
     * @param chatService 由 {@code ai-core} 提供的聊天编排 Bean
     */
    public CustomerChatFacade(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 在指定会话下发起一轮问答。
     *
     * @param sessionId 会话 ID
     * @param message   用户消息正文
     * @return 模型回复文本
     */
    public String ask(String sessionId, String message) {
        return chatService.chat(sessionId, message);
    }
}
