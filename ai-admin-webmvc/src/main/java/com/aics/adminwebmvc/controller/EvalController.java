package com.aics.adminwebmvc.controller;

import com.aics.adminwebmvc.dto.EvalRunRequest;
import com.aics.adminwebmvc.dto.EvalRunResponse;
import com.aics.adminwebmvc.service.EvaluationApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 能力评估：委托 {@link EvaluationApiService} 调用 ai-eval。
 */
@RestController
@RequestMapping("/api/eval")
public class EvalController {

    private final EvaluationApiService evaluationApiService;

    public EvalController(EvaluationApiService evaluationApiService) {
        this.evaluationApiService = evaluationApiService;
    }

    /**
     * 请求体可省略，默认跑「订单未发货」演示用例；也可传 question / expectedKeywords / category。
     */
    @PostMapping("/run")
    public EvalRunResponse run(@RequestBody(required = false) EvalRunRequest request) {
        EvalRunRequest req = request != null ? request : new EvalRunRequest(null, null, null);
        return evaluationApiService.run(req.toCase());
    }
}
