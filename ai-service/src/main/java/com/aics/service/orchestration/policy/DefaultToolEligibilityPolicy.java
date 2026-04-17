package com.aics.service.orchestration.policy;

import com.aics.service.config.OrchestrationProperties;
import com.aics.tools.ToolService;
import org.springframework.stereotype.Component;

/**
 * 委托 ai-tools 的启发式路由判断；未来可换为显式 tool_calls 解析策略实现本接口。
 */
@Component
public class DefaultToolEligibilityPolicy implements ToolEligibilityPolicy {

    private final OrchestrationProperties properties;
    private final ToolService toolService;

    public DefaultToolEligibilityPolicy(OrchestrationProperties properties,
                                        ToolService toolService) {
        this.properties = properties;
        this.toolService = toolService;
    }

    @Override
    public boolean shouldExecuteTools(String userMessage) {
        if (!properties.isToolsEnabled()) {
            return false;
        }
        return toolService.hasHeuristicToolMatch(userMessage);
    }
}
