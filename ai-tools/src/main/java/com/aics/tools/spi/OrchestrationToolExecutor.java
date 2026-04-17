package com.aics.tools.spi;

import com.aics.spi.ToolExecutor;
import com.aics.tools.ToolService;
import org.springframework.stereotype.Component;

/**
 * 对接 {@link com.aics.spi.ToolExecutor}：将编排层传入的整句用户消息交给 {@link ToolService#tryExecuteFromUserMessage(String)}，
 * 未命中任何演示规则时返回空串，由 Prompt 侧视为无工具输出。
 */
@Component
public class OrchestrationToolExecutor implements ToolExecutor {

    private final ToolService toolService;

    public OrchestrationToolExecutor(ToolService toolService) {
        this.toolService = toolService;
    }

    @Override
    public String execute(String message) {
        return toolService.tryExecuteFromUserMessage(message);
    }
}
