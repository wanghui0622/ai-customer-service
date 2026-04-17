package com.aics.core.llm;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

/**
 * 基于 LangChain4j {@link OpenAiChatModel} 的 {@link LlmProvider} 实现。
 */
@Component
public class OpenAiLlmClient implements LlmProvider {

    /** LangChain4j 封装的 OpenAI 兼容聊天模型。 */
    private final OpenAiChatModel model;

    /**
     * @param model 由 {@link com.aics.core.config.LlmConfig} 注册的聊天模型 Bean
     */
    public OpenAiLlmClient(OpenAiChatModel model) {
        this.model = model;
    }

    @Override
    public String chat(String prompt) {
        return model.chat(prompt);
    }
}
