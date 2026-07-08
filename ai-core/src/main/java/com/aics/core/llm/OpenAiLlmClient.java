package com.aics.core.llm;

import com.aics.spi.LlmClient;
import com.aics.spi.StreamingLlmClient;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.function.Consumer;

/**
 * 由 {@link com.aics.core.config.LlmConfig} 注册为 Bean，供 {@link ResilientLlmClient} 包装。
 */
public class OpenAiLlmClient implements StreamingLlmClient {

    private final OpenAiChatModel model;
    private final OpenAiStreamingChatModel streamingModel;

    public OpenAiLlmClient(OpenAiChatModel model, OpenAiStreamingChatModel streamingModel) {
        this.model = model;
        this.streamingModel = streamingModel;
    }

    @Override
    public String chat(String prompt) {
        return model.chat(prompt);
    }

    @Override
    public void stream(String prompt, Consumer<String> onChunk) {
        streamingModel.chat(prompt, new dev.langchain4j.model.chat.response.StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                if (partialResponse != null && !partialResponse.isEmpty()) {
                    onChunk.accept(partialResponse);
                }
            }

            @Override
            public void onCompleteResponse(dev.langchain4j.model.chat.response.ChatResponse response) {
            }

            @Override
            public void onError(Throwable error) {
                throw new IllegalStateException("streaming failed: " + error.getMessage(), error);
            }
        });
    }
}
