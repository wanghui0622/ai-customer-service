package com.aics.eval;

/**
 * 某一 {@link AiVersion} 下单次评分快照。
 */
public record VersionEvaluation(
        AiVersion version,
        String answer,
        int score,
        String explanation
) {
}
