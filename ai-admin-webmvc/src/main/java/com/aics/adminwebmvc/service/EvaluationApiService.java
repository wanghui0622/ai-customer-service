package com.aics.adminwebmvc.service;

import com.aics.adminwebmvc.dto.EvalRunResponse;
import com.aics.adminwebmvc.dto.EvalVersionItem;
import com.aics.eval.AiVersion;
import com.aics.eval.EvaluationCase;
import com.aics.eval.EvaluationReport;
import com.aics.eval.EvaluationRunner;
import com.aics.eval.VersionEvaluation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationApiService {

    private final EvaluationRunner runner = new EvaluationRunner();

    public EvalRunResponse run(EvaluationCase evaluationCase) {
        EvaluationReport report = runner.run(evaluationCase);
        var by = report.byVersion();
        List<EvalVersionItem> items = new ArrayList<>();
        for (AiVersion v : AiVersion.values()) {
            VersionEvaluation ve = by.get(v);
            if (ve != null) {
                items.add(new EvalVersionItem(v.name(), ve.answer(), ve.score()));
            }
        }
        return new EvalRunResponse(items);
    }
}
