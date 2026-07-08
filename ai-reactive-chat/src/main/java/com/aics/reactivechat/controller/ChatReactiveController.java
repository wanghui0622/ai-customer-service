package com.aics.reactivechat.controller;

import com.aics.reactivechat.dto.ChatRequest;
import com.aics.reactivechat.dto.ChatResumeRequest;
import com.aics.reactivechat.dto.ChatTraceResponse;
import com.aics.service.chat.CustomerChatFacade;
import com.aics.service.config.ObservabilityProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Netty 上运行的 WebFlux 控制器：仅聊天；编排仍经 {@link CustomerChatFacade}。
 */
@RestController
@RequestMapping("/api/chat")
public class ChatReactiveController {

    private final CustomerChatFacade customerChatFacade;
    private final ObservabilityProperties observabilityProperties;

    public ChatReactiveController(CustomerChatFacade customerChatFacade,
                                  ObservabilityProperties observabilityProperties) {
        this.customerChatFacade = customerChatFacade;
        this.observabilityProperties = observabilityProperties;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatTraceResponse> chat(@RequestBody ChatRequest request) {
        require(request);
        return Mono.fromCallable(() -> {
                    if (observabilityProperties.isExposePromptTrace()) {
                        return ChatTraceResponse.fromTrace(
                                customerChatFacade.askWithTrace(request.sessionId(), request.message()));
                    }
                    String answer = customerChatFacade.ask(request.sessionId(), request.message());
                    return ChatTraceResponse.answerOnly(answer);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(
            path = "/resume",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ChatTraceResponse> resume(@RequestBody ChatResumeRequest request) {
        requireResume(request);
        return Mono.fromCallable(() -> ChatTraceResponse.fromTrace(
                        customerChatFacade.resumeWithApproval(
                                request.sessionId(),
                                request.approvalToken(),
                                request.decision())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(
            path = "/stream",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> stream(@RequestBody ChatRequest request) {
        require(request);
        return Flux.<String>create(sink -> Schedulers.boundedElastic().schedule(() -> {
            try {
                customerChatFacade.askStream(request.sessionId(), request.message(), sink::next);
                sink.complete();
            } catch (Exception ex) {
                sink.error(ex);
            }
        })).subscribeOn(Schedulers.boundedElastic());
    }

    private static void require(ChatRequest request) {
        if (request == null || request.sessionId() == null || request.sessionId().isBlank()) {
            throw new IllegalArgumentException("sessionId 不能为空");
        }
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("message 不能为空");
        }
    }

    private static void requireResume(ChatResumeRequest request) {
        if (request == null || request.sessionId() == null || request.sessionId().isBlank()) {
            throw new IllegalArgumentException("sessionId 不能为空");
        }
        if (request.approvalToken() == null || request.approvalToken().isBlank()) {
            throw new IllegalArgumentException("approvalToken 不能为空");
        }
        if (request.decision() == null || request.decision().isBlank()) {
            throw new IllegalArgumentException("decision 不能为空");
        }
    }
}
