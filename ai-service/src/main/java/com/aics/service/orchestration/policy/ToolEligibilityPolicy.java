package com.aics.service.orchestration.policy;

/**
 * 是否对当前用户消息执行工具调用。由编排层调用，不实现具体工具。
 */
@FunctionalInterface
public interface ToolEligibilityPolicy {

    /**
     * @param userMessage 当前轮用户输入
     * @return true 则调用 {@link com.aics.spi.ToolExecutor#execute(String)}
     */
    boolean shouldExecuteTools(String userMessage);
}
