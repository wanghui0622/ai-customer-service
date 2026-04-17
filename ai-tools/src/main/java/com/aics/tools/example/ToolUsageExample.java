package com.aics.tools.example;

import com.aics.tools.ToolService;
import com.aics.tools.builtin.OrderQueryTool;
import com.aics.tools.builtin.WeatherTool;

/**
 * 博客/文档用调用示例（需由 Spring 注入 {@link ToolService}）。
 */
public final class ToolUsageExample {

    private ToolUsageExample() {
    }

    /**
     * 推荐：编排层已知模型选择的工具名与参数时显式调用。
     */
    public static String explicitNamedCall(ToolService toolService) {
        return toolService.executeNamed(OrderQueryTool.NAME, "查一下订单 ORD-10086");
    }

    /**
     * 演示：依赖内置关键词路由（无 LLM）。
     */
    public static String heuristicRoute(ToolService toolService) {
        return toolService.tryExecuteFromUserMessage("上海今天天气怎么样");
    }

    /**
     * 天气演示工具名常量用法。
     */
    public static String weatherExplicit(ToolService toolService) {
        return toolService.executeNamed(WeatherTool.NAME, "杭州");
    }
}
