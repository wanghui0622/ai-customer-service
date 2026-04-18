package com.aics.reactivechat.controller;

import com.aics.reactivechat.dto.ChatRequest;
import com.aics.reactivechat.dto.ChatResponse;
import com.aics.service.chat.CustomerChatFacade;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Netty 上运行的 WebFlux 控制器：仅聊天；编排仍经 {@link CustomerChatFacade}。
 */
@RestController
@RequestMapping("/api/chat")
public class ChatReactiveController {

    private final CustomerChatFacade customerChatFacade;

    public ChatReactiveController(CustomerChatFacade customerChatFacade) {
        this.customerChatFacade = customerChatFacade;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        require(request);
        return Mono.fromCallable(() -> customerChatFacade.ask(request.sessionId(), request.message()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(ChatResponse::new);
    }

    @PostMapping(
            path = "/stream",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> stream(@RequestBody ChatRequest request) {
        require(request);
        return Flux.defer(() ->
                Mono.fromCallable(() -> customerChatFacade.ask(request.sessionId(), request.message()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMapMany(ChatReactiveController::toCharFlux)
        );
    }

    private static void require(ChatRequest request) {
        if (request == null || request.sessionId() == null || request.sessionId().isBlank()) {
            throw new IllegalArgumentException("sessionId 不能为空");
        }
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("message 不能为空");
        }
    }

    private static Flux<String> toCharFlux(String text) {
        if (text == null || text.isEmpty()) {
            return Flux.empty();
        }
        return Flux.fromStream(text.codePoints().mapToObj(cp -> new String(Character.toChars(cp))))
                .delayElements(Duration.ofMillis(4));
    }
}
