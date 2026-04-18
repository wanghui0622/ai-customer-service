package com.aics.eval;

import com.aics.eval.support.CapabilityChatFactory;
import com.aics.eval.support.RecordingLlmClient;
import com.aics.service.chat.AiChatService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对 {@link EvaluationCase} 在全部 {@link AiVersion} 上跑通并汇总评分。
 */
public final class EvaluationRunner {

    public EvaluationReport run(EvaluationCase evaluationCase) {
        Map<AiVersion, VersionEvaluation> map = new LinkedHashMap<>();
        for (AiVersion v : AiVersion.values()) {
            RecordingLlmClient llm = new RecordingLlmClient();
            AiChatService svc = CapabilityChatFactory.build(v, llm);
            String answer = svc.chat("eval-session-" + v.name(), evaluationCase.question());
            int score = AiEvaluator.score(answer, evaluationCase);
            String explanation = AiEvaluator.explain(answer, evaluationCase);
            map.put(v, new VersionEvaluation(v, answer, score, explanation));
        }
        return new EvaluationReport(evaluationCase, map);
    }
}
