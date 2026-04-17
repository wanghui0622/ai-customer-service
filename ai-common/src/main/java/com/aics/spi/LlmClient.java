package com.aics.spi;

/**
 * 大模型调用（由 ai-core 实现）。
 */
public interface LlmClient {

    String chat(String prompt);
}
