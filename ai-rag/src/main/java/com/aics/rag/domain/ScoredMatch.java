package com.aics.rag.domain;

import java.util.Map;

/**
 * 语义检索命中结果（余弦相似度分数越高越相关）。
 */
public record ScoredMatch(String id, String text, double score, Map<String, String> metadata) {
}
