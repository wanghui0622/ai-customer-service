package com.aics.spi;

import java.util.List;

/**
 * 知识检索（由 ai-rag 实现）。
 */
public interface KnowledgeRetriever {

    List<String> retrieve(String question);
}
