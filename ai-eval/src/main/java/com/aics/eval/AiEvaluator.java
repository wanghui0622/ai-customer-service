package com.aics.eval;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于期望关键词的简单命中率评分（0–100）。
 */
public final class AiEvaluator {

    private AiEvaluator() {
    }

    public static int score(String answer, EvaluationCase caze) {
        if (answer == null || answer.isBlank()) {
            return 0;
        }
        List<String> keys = caze.expectedKeywords();
        if (keys == null || keys.isEmpty()) {
            return 100;
        }
        int hit = 0;
        for (String k : keys) {
            if (k != null && !k.isBlank() && answer.contains(k.trim())) {
                hit++;
            }
        }
        return (int) Math.round(100.0 * hit / keys.size());
    }

    public static String explain(String answer, EvaluationCase caze) {
        List<String> keys = caze.expectedKeywords();
        if (keys == null || keys.isEmpty()) {
            return "未配置关键词，默认满分逻辑由调用方处理。";
        }
        List<String> hit = new ArrayList<>();
        List<String> miss = new ArrayList<>();
        String a = answer == null ? "" : answer;
        for (String k : keys) {
            if (k == null || k.isBlank()) {
                continue;
            }
            String t = k.trim();
            if (a.contains(t)) {
                hit.add(t);
            } else {
                miss.add(t);
            }
        }
        return "命中 " + hit.size() + "/" + keys.size() + "：" + hit
                + (miss.isEmpty() ? "" : "；未命中：" + miss);
    }
}
