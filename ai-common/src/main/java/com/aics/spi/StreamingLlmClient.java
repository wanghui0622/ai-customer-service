package com.aics.spi;

import java.util.function.Consumer;

/**
 * 可选流式 LLM 能力；{@link LlmClient} 实现类可按需实现。
 */
public interface StreamingLlmClient extends LlmClient {

    default boolean supportsStreaming() {
        return true;
    }

    void stream(String prompt, Consumer<String> onChunk);
}
