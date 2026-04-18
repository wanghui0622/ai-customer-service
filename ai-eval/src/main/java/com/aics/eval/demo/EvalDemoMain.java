package com.aics.eval.demo;

import com.aics.eval.AiVersion;
import com.aics.eval.DemoData;
import com.aics.eval.EvaluationReport;
import com.aics.eval.EvaluationRunner;
import com.aics.eval.VersionEvaluation;

public final class EvalDemoMain {

    public static void main(String[] args) {
        EvaluationRunner runner = new EvaluationRunner();
        EvaluationReport report = runner.run(DemoData.ORDER_SHIPPING);

        System.out.println("问题：" + report.evaluationCase().question());
        System.out.println("期望关键词：" + report.evaluationCase().expectedKeywords());
        System.out.println();

        for (AiVersion v : AiVersion.values()) {
            VersionEvaluation ve = report.byVersion().get(v);
            if (ve == null) {
                continue;
            }
            System.out.println("=== " + v + " ===");
            System.out.println(ve.answer());
            System.out.println("score=" + ve.score() + " | " + ve.explanation());
            System.out.println();
        }

        System.out.println("--- Markdown ---");
        System.out.println(report.toMarkdownTable());
        System.out.println("--- delta vs BASE ---");
        System.out.println(report.deltaFromBase());
    }
}
