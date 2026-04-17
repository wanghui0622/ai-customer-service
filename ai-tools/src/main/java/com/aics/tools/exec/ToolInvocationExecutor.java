package com.aics.tools.exec;

import com.aics.tools.Tool;
import com.aics.tools.registry.ToolRegistry;
import org.springframework.stereotype.Component;

/**
 * 按工具名调度执行：不包含「是否应调用工具」的决策，仅负责查找并执行。
 * <p>
 * 上层（如编排或未来 Agent）在确定 {@code toolName} 与入参后调用 {@link #invoke(String, String)}。
 */
@Component
public class ToolInvocationExecutor {

    private final ToolRegistry registry;

    public ToolInvocationExecutor(ToolRegistry registry) {
        this.registry = registry;
    }

    /**
     * @param toolName 已在注册表中的名称
     * @param input    传入工具的字符串参数
     * @return 工具输出
     * @throws IllegalArgumentException 未注册的工具名
     */
    public String invoke(String toolName, String input) {
        Tool tool = registry.get(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tool: " + toolName));
        return tool.execute(input == null ? "" : input);
    }
}
