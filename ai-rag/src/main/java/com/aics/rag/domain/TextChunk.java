package com.aics.rag.domain;

import java.util.Map;

/**
 * 切分后的文本块，作为向量检索的最小单元。
 */
public record TextChunk(String id, String text, Map<String, String> metadata) {
}
