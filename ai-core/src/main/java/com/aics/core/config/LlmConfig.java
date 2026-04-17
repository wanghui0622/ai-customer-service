package com.aics.core.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * LangChain4j OpenAI 兼容聊天模型配置，供 {@link com.aics.core.llm.OpenAiLlmClient} 注入使用。
 * <p>
 * 生产环境应将 apiKey、baseUrl、modelName 等改为配置项或密钥管理，避免硬编码。
 */
@Configuration
public class LlmConfig {

    /**
     * 注册 {@link OpenAiChatModel} Bean，使用演示用 endpoint 与 demo key。
     *
     * @return 已配置超时、温度与请求/响应日志的聊天模型实例
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey("demo")
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .modelName("gpt-4o-mini")
                .temperature(0.3)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
