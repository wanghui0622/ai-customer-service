package com.aics.rag.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 导入侧文档：由字符串或文件解析而来，经切分与向量化后进入 {@link com.aics.rag.vectorstore.VectorStore}。
 */
public final class RagDocument {

    private final String id;
    private final String title;
    private final String content;
    private final Map<String, String> metadata;

    public RagDocument(String id, String title, String content, Map<String, String> metadata) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.title = title == null ? "" : title;
        this.content = content == null ? "" : content;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    /**
     * 纯文本文档快捷构造。
     */
    public static RagDocument plain(String title, String content) {
        return new RagDocument(UUID.randomUUID().toString(), title, content, Map.of());
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public Map<String, String> metadata() {
        return metadata;
    }

    /**
     * 合并元数据（用于在 chunk 上附带来源文档信息）。
     */
    public Map<String, String> mergedMetadata(Map<String, String> extra) {
        Map<String, String> m = new HashMap<>(metadata);
        if (extra != null) {
            m.putAll(extra);
        }
        return Map.copyOf(m);
    }
}
