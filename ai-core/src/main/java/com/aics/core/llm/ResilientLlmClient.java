package com.aics.core.llm;

import com.aics.spi.LlmClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

/**
 * 对 {@link LlmClient} 出站调用施加熔断与重试（超时由 LangChain4j 模型配置）。
 */
public final class ResilientLlmClient implements LlmClient {

    private final LlmClient delegate;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ResilientLlmClient(LlmClient delegate, CircuitBreaker circuitBreaker, Retry retry) {
        this.delegate = delegate;
        this.circuitBreaker = circuitBreaker;
        this.retry = retry;
    }

    @Override
    public String chat(String prompt) {
        try {
            return retry.executeSupplier(() -> circuitBreaker.executeSupplier(() -> delegate.chat(prompt)));
        } catch (CallNotPermittedException e) {
            throw new IllegalStateException("LLM 熔断开启，暂时不可用", e);
        }
    }
}
