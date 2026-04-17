package com.aics.service.rag;

import com.aics.core.llm.LlmProvider;
import com.aics.rag.llm.RagLlmClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将 ai-core 的 {@link LlmProvider} 适配为 ai-rag 的 {@link RagLlmClient}，完成跨模块解耦后的接线。
 */
@Configuration
public class RagLlmBridgeConfiguration {

    @Bean
    @ConditionalOnBean(LlmProvider.class)
    public RagLlmClient ragLlmClient(LlmProvider llmProvider) {
        return llmProvider::chat;
    }
}
