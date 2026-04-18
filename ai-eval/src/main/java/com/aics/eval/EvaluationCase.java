package com.aics.eval;

import java.util.List;

/**
 * 单次评估用例。
 */
public record EvaluationCase(
        String question,
        List<String> expectedKeywords,
        String category
) {
}
