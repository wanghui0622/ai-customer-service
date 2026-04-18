package com.aics.service.chat;

import com.aics.service.chat.dto.ChatTurnTraceResult;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

/**
 * 对外统一入口（Web / RPC 等应仅依赖本门面），内部委托 {@link AiChatService}，便于鉴权、审计、限流等横切扩展。
 */
@Service
public class CustomerChatFacade {

    private final AiChatService aiChatService;

    public CustomerChatFacade(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 用户请求唯一入口：进入编排管道 {@link AiChatService#chat(String, String)}。
     */
    @RateLimiter(name = "chat")
    public String ask(String sessionId, String message) {
        return aiChatService.chat(sessionId, message);
    }

    /**
     * 返回带编排快照的答复（调试用 UI）。
     */
    @RateLimiter(name = "chat")
    public ChatTurnTraceResult askWithTrace(String sessionId, String message) {
        return aiChatService.chatWithTrace(sessionId, message);
    }
}
