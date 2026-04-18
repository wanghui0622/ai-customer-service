package com.aics.core.llm;

import com.aics.spi.LlmClient;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * 由 {@link com.aics.core.config.LlmConfig} 注册为 Bean，供 {@link ResilientLlmClient} 包装。
 */
public class OpenAiLlmClient implements LlmClient {

    private final OpenAiChatModel model;

    public OpenAiLlmClient(OpenAiChatModel model) {
        this.model = model;
    }

    @Override
    public String chat(String prompt) {
        return model.chat(prompt);
    }
}
