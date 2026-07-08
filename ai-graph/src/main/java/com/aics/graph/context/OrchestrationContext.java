package com.aics.graph.context;

import java.io.Serializable;

/**
 * 单次图执行的产品级开关（来自 {@code aics.orchestration}），与图内部状态分离。
 */
public record OrchestrationContext(
        boolean ragEnabled,
        boolean toolsEnabled,
        boolean agentRouterLlmEnabled
) implements Serializable {
    public static OrchestrationContext defaults() {
        return new OrchestrationContext(true, true, true);
    }
}
