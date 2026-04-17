package com.aics.webfluxchat.controller;

import com.aics.service.chat.CustomerChatFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 对外聊天 HTTP 接口（WebFlux），将请求委托给业务门面 {@link CustomerChatFacade}。
 */
@RestController
public class ChatController {

    /** 聊天业务门面。 */
    private final CustomerChatFacade customerChatFacade;

    /**
     * @param customerChatFacade 注入的业务门面
     */
    public ChatController(CustomerChatFacade customerChatFacade) {
        this.customerChatFacade = customerChatFacade;
    }

    /**
     * 同步阻塞式调用封装在 {@link Mono#fromSupplier} 中，避免阻塞 Reactor 线程（后续可改为全链路异步）。
     *
     * @param sessionId 会话 ID
     * @param message   用户消息
     * @return 模型回复的异步流
     */
    @GetMapping("/api/chat")
    public Mono<String> chat(@RequestParam String sessionId, @RequestParam String message) {
        return Mono.fromSupplier(() -> customerChatFacade.ask(sessionId, message));
    }
}
