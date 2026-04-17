package com.aics.rag.vectorstore;

import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.domain.VectorRecord;

import java.util.Collection;
import java.util.List;

/**
 * 向量存储抽象：可替换为 Milvus、PGVector、Elasticsearch 等实现。
 */
public interface VectorStore {

    /**
     * 批量写入（覆盖同 id 行为由实现定义；内存/H2 实现为追加或替换均可，此处约定按 id upsert）。
     */
    void upsertAll(Collection<VectorRecord> records);

    /**
     * 余弦相似度 Top-K 检索。
     *
     * @param queryEmbedding 查询向量
     * @param topK           返回条数上限
     * @param minScore       最低相似度阈值（余弦相似度）
     * @return 按分数降序
     */
    List<ScoredMatch> similaritySearch(float[] queryEmbedding, int topK, double minScore);

    /**
     * 清空全部向量（测试或重建索引用）。
     */
    void removeAll();
}
