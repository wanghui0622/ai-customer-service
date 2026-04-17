package com.aics.rag.domain;

import java.util.Map;

/**
 * 已计算嵌入向量的可检索记录。
 */
public record VectorRecord(String id, String text, float[] embedding, Map<String, String> metadata) {

    public VectorRecord {
        if (embedding == null) {
            throw new IllegalArgumentException("embedding required");
        }
    }
}
