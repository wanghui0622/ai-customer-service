package com.aics.adminwebmvc.dto;

import java.util.List;

/**
 * POST /api/eval/run 响应体。
 */
public record EvalRunResponse(List<EvalVersionItem> versions) {
}
