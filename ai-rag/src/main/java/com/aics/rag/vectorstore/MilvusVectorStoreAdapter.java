package com.aics.rag.vectorstore;

import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.domain.VectorRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Milvus 占位实现：接入生产时引入官方 Java SDK，实现写入与 ANN 检索，并替换为 {@link VectorStore} Bean。
 */
@Component("milvusVectorStore")
@ConditionalOnProperty(prefix = "aics.rag", name = "vector-store", havingValue = "milvus")
public class MilvusVectorStoreAdapter implements VectorStore {

    @Override
    public void upsertAll(Collection<VectorRecord> records) {
        throw unsupported();
    }

    @Override
    public List<ScoredMatch> similaritySearch(float[] queryEmbedding, int topK, double minScore) {
        throw unsupported();
    }

    @Override
    public void removeAll() {
        throw unsupported();
    }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(
                "MilvusVectorStoreAdapter 为预留实现：请引入 milvus-sdk-java，创建 Collection / Index，并实现 upsert 与向量检索。");
    }
}
