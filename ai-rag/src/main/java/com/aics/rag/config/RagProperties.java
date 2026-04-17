package com.aics.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RAG 模块配置。
 */
@ConfigurationProperties(prefix = "aics.rag")
public class RagProperties {

    /**
     * 是否启用本模块自动配置。
     */
    private boolean enabled = true;

    /**
     * 向量存储类型：{@code in-memory}（默认）、{@code h2}、{@code milvus}（占位）。
     */
    private String vectorStore = "in-memory";

    /**
     * 切分窗口（字符数，教学版）。
     */
    private int chunkSize = 512;

    /**
     * 相邻窗口重叠（字符数）。
     */
    private int chunkOverlap = 64;

    /**
     * 检索返回条数上限。
     */
    private int retrievalTopK = 5;

    /**
     * 最低余弦相似度阈值。
     */
    private double minScore = 0.0;

    /**
     * H2 JDBC URL（仅当 {@code vector-store=h2} 时使用）。
     */
    private String h2JdbcUrl = "jdbc:h2:mem:rag-store;DB_CLOSE_DELAY=-1";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVectorStore() {
        return vectorStore;
    }

    public void setVectorStore(String vectorStore) {
        this.vectorStore = vectorStore;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getChunkOverlap() {
        return chunkOverlap;
    }

    public void setChunkOverlap(int chunkOverlap) {
        this.chunkOverlap = chunkOverlap;
    }

    public int getRetrievalTopK() {
        return retrievalTopK;
    }

    public void setRetrievalTopK(int retrievalTopK) {
        this.retrievalTopK = retrievalTopK;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public String getH2JdbcUrl() {
        return h2JdbcUrl;
    }

    public void setH2JdbcUrl(String h2JdbcUrl) {
        this.h2JdbcUrl = h2JdbcUrl;
    }
}
