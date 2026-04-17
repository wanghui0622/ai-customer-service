package com.aics.tools;

import com.aics.tools.builtin.EchoTool;
import com.aics.tools.builtin.OrderQueryTool;
import com.aics.tools.builtin.WeatherTool;
import com.aics.tools.exec.ToolInvocationExecutor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 工具门面：提供显式按名调用，以及基于关键词的<strong>演示级</strong>路由（无 LLM）。
 * <p>
 * 生产环境推荐由编排层根据模型结构化输出调用 {@link #executeNamed(String, String)}；
 * {@link #tryExecuteFromUserMessage(String)} 仅用于演示或与简单规则配合。
 */
@Service
public class ToolService {

    private static final Pattern ORDER_ID = Pattern.compile("(?i)(ORD[-_]?\\d+|订单[:：]?\\s*\\d+)");

    private final ToolInvocationExecutor invocationExecutor;

    public ToolService(ToolInvocationExecutor invocationExecutor) {
        this.invocationExecutor = invocationExecutor;
    }

    /**
     * 显式执行：编排层已知工具名与入参时使用（推荐主路径，利于 MCP / function calling 对齐）。
     */
    public String executeNamed(String toolName, String input) {
        return invocationExecutor.invoke(toolName, input);
    }

    /**
     * 根据用户整句消息做轻量规则匹配；未命中时返回空串，表示「本轮无工具结果」。
     * <p>
     * 规则可逐步替换为：解析 LLM 返回的 tool_calls、或独立意图分类服务。
     */
    public String tryExecuteFromUserMessage(String userMessage) {
        return route(userMessage)
                .map(r -> invocationExecutor.invoke(r.toolName, r.payload))
                .orElse("");
    }

    /**
     * 供编排层判断本轮是否「可能」走内置工具路由（与 {@link #tryExecuteFromUserMessage(String)} 命中条件一致）。
     */
    public boolean hasHeuristicToolMatch(String userMessage) {
        return route(userMessage).isPresent();
    }

    private Optional<Route> route(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        String m = message.trim();
        String lower = m.toLowerCase(Locale.ROOT);

        if (m.regionMatches(true, 0, "echo:", 0, 5)) {
            return Optional.of(new Route(EchoTool.NAME, m.substring(5).trim()));
        }
        if (lower.contains("order") || m.contains("订单") || ORDER_ID.matcher(m).find()) {
            return Optional.of(new Route(OrderQueryTool.NAME, m));
        }
        if (lower.contains("weather") || m.contains("天气")) {
            return Optional.of(new Route(WeatherTool.NAME, m));
        }
        return Optional.empty();
    }

    private record Route(String toolName, String payload) {
    }
}
