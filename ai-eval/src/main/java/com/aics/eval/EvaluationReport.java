package com.aics.eval;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一次评估的多版本结果与相对基线的分差。
 */
public record EvaluationReport(
        EvaluationCase evaluationCase,
        Map<AiVersion, VersionEvaluation> byVersion
) {

    public Map<AiVersion, Integer> deltaFromBase() {
        VersionEvaluation base = byVersion.get(AiVersion.BASE);
        int baseScore = base == null ? 0 : base.score();
        Map<AiVersion, Integer> out = new LinkedHashMap<>();
        for (AiVersion v : AiVersion.values()) {
            VersionEvaluation ve = byVersion.get(v);
            int s = ve == null ? 0 : ve.score();
            out.put(v, s - baseScore);
        }
        return out;
    }

    public String toMarkdownTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("| 版本 | 分数 | 相对BASE | 说明摘要 |\n");
        sb.append("|------|------|-----------|----------|\n");
        Map<AiVersion, Integer> d = deltaFromBase();
        for (AiVersion v : AiVersion.values()) {
            VersionEvaluation ve = byVersion.get(v);
            if (ve == null) {
                continue;
            }
            String shortExp = ve.explanation().replace('\n', ' ');
            if (shortExp.length() > 48) {
                shortExp = shortExp.substring(0, 45) + "...";
            }
            sb.append("| ").append(v)
                    .append(" | ").append(ve.score())
                    .append(" | ").append(d.get(v))
                    .append(" | ").append(shortExp)
                    .append(" |\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return byVersion.values().stream()
                .map(v -> v.version() + "=" + v.score())
                .collect(Collectors.joining(", ", "EvaluationReport[", "]"));
    }
}
