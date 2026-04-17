package com.aics.rag.chunking;

/**
 * 按字符窗口切分（教学版）；生产可换为按 token / 句子边界等策略。
 */
public record ChunkingOptions(int maxChars, int overlapChars) {

    public ChunkingOptions {
        if (maxChars <= 0) {
            throw new IllegalArgumentException("maxChars must be positive");
        }
        if (overlapChars < 0 || overlapChars >= maxChars) {
            throw new IllegalArgumentException("overlapChars must be in [0, maxChars)");
        }
    }
}
