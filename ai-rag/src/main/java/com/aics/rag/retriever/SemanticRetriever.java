package com.aics.rag.retriever;

import com.aics.rag.config.RagProperties;
import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.embedding.EmbeddingService;
import com.aics.rag.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 语义检索：问题嵌入 + 向量库相似度 Top-K。
 */
@Service
public class SemanticRetriever {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final RagProperties properties;

    public SemanticRetriever(EmbeddingService embeddingService,
                             VectorStore vectorStore,
                             RagProperties properties) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.properties = properties;
    }

    /**
     * 对用户问题做检索，返回带分数的片段。
     */
    public List<ScoredMatch> retrieve(String query) {
        float[] q = embeddingService.embed(query == null ? "" : query);
        return vectorStore.similaritySearch(q, properties.getRetrievalTopK(), properties.getMinScore());
    }
}
