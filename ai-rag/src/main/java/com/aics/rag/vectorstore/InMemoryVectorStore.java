package com.aics.rag.vectorstore;

import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.domain.VectorRecord;
import com.aics.rag.math.VectorMath;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现：进程内 Map 存储，适合开发与单测。
 */
@Component("inMemoryVectorStore")
@ConditionalOnProperty(prefix = "aics.rag", name = "vector-store", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryVectorStore implements VectorStore {

    private final Map<String, VectorRecord> rows = new ConcurrentHashMap<>();

    @Override
    public void upsertAll(Collection<VectorRecord> records) {
        for (VectorRecord r : records) {
            rows.put(r.id(), r);
        }
    }

    @Override
    public List<ScoredMatch> similaritySearch(float[] queryEmbedding, int topK, double minScore) {
        List<ScoredMatch> hits = new ArrayList<>();
        for (VectorRecord r : rows.values()) {
            double score = VectorMath.cosineSimilarity(queryEmbedding, r.embedding());
            if (score >= minScore) {
                hits.add(new ScoredMatch(r.id(), r.text(), score, r.metadata()));
            }
        }
        hits.sort(Comparator.comparingDouble(ScoredMatch::score).reversed());
        if (hits.size() > topK) {
            return List.copyOf(hits.subList(0, topK));
        }
        return hits;
    }

    @Override
    public void removeAll() {
        rows.clear();
    }
}
