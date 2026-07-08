package com.aics.spi;

import java.util.List;

/**
 * 知识检索（由 ai-rag 实现）。
 */
public interface KnowledgeRetriever {

    /**
     * 检索指定问题的知识。
     * @param question
     * @return
     */
    List<String> retrieve(String question);
}
