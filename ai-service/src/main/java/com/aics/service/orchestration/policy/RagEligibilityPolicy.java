package com.aics.service.orchestration.policy;

/**
 * 是否对当前用户消息执行知识检索（RAG）。由编排层调用，不实现检索本身。
 */
@FunctionalInterface
public interface RagEligibilityPolicy {

    /**
     * @param userMessage 当前轮用户输入
     * @return true 则调用 {@link com.aics.spi.KnowledgeRetriever#retrieve(String)}
     */
    boolean shouldRetrieveKnowledge(String userMessage);
}
