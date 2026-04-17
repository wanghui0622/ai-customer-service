package com.aics.core.llm;

import com.aics.spi.LlmClient;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
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
