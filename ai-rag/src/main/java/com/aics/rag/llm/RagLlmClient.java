package com.aics.rag.llm;

/**
 * 与 LLM 交互的窄接口，避免 ai-rag 直接依赖 ai-core：由应用层将
 * ai-core 中的 LlmProvider（或任意网关）适配为此接口。
 */
@FunctionalInterface
public interface RagLlmClient {

    /**
     * @param prompt 已拼好的单条提示（可含 system + user 语义）
     * @return 模型回答
     */
    String complete(String prompt);
}
