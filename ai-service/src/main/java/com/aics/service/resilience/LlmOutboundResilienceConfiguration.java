package com.aics.service.resilience;

import com.aics.core.llm.OpenAiLlmClient;
import com.aics.core.llm.ResilientLlmClient;
import com.aics.spi.LlmClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 出站 LLM 调用统一套熔断与重试实例名 {@code llm}，配置见各应用 {@code application.yml}。
 */
@Configuration
public class LlmOutboundResilienceConfiguration {

    @Bean
    @Primary
    public LlmClient resilientLlmClient(
            @Qualifier("openAiLlmDelegate") OpenAiLlmClient delegate,
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry) {
        return new ResilientLlmClient(
                delegate,
                circuitBreakerRegistry.circuitBreaker("llm"),
                retryRegistry.retry("llm"));
    }
}
