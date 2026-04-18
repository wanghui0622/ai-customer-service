package com.aics.adminwebmvc.dto;

import com.aics.eval.DemoData;
import com.aics.eval.EvaluationCase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EvalRunRequest(String question, List<String> expectedKeywords, String category) {

    public EvaluationCase toCase() {
        if (question == null || question.isBlank()) {
            return DemoData.ORDER_SHIPPING;
        }
        List<String> kw = expectedKeywords != null && !expectedKeywords.isEmpty()
                ? expectedKeywords
                : List.of();
        String cat = category != null && !category.isBlank() ? category : "general";
        return new EvaluationCase(question, kw, cat);
    }
}
