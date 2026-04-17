package com.aics.rag.retriever;

import com.aics.rag.config.RagProperties;
import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.embedding.EmbeddingService;
import com.aics.rag.vectorstore.VectorStore;
import com.aics.spi.KnowledgeRetriever;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultKnowledgeRetriever implements KnowledgeRetriever {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final RagProperties properties;

    public DefaultKnowledgeRetriever(EmbeddingService embeddingService,
                                     VectorStore vectorStore,
                                     RagProperties properties) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.properties = properties;
    }

    @Override
    public List<String> retrieve(String question) {
        float[] q = embeddingService.embed(question == null ? "" : question);
        List<ScoredMatch> hits = vectorStore.similaritySearch(q, properties.getRetrievalTopK(), properties.getMinScore());
        return hits.stream().map(ScoredMatch::text).toList();
    }
}
