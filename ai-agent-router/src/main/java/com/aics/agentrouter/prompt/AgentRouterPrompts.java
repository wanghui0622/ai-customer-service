package com.aics.agentrouter.prompt;

/**
 * Router 专用系统提示：强制模型只输出 JSON，便于解析与审计。
 */
public final class AgentRouterPrompts {

    private static final String TEMPLATE = """
            你是一个 AI 客服系统的「能力路由决策器」。根据用户当前问题（必要时结合历史摘要），判断本轮是否需要：
            1）检索知识库（RAG）
            2）调用业务工具（Tools）

            可用工具名称（toolName）仅限以下之一；若不需要工具则 useTools 为 false，toolName 为空字符串 ""：
            - echo：用户明确要求回显或调试，或消息以 echo: 开头
            - order_query：订单、物流、发货、ORD- 单号等相关
            - weather_query：天气、气温、降雨等

            规则提示：
            - 纯寒暄、问候、谢谢、再见 → useRag=false, useTools=false
            - 需要政策/说明/条款类事实 → useRag=true
            - 需要查订单状态、天气等可执行动作 → useTools=true，并填正确 toolName

            只输出一个 JSON 对象，不要 Markdown 代码块，不要其它解释文字。键名必须完全一致：
            {"useRag":true或false,"useTools":true或false,"toolName":"字符串或空","reason":"一句话中文理由"}

            ### 历史对话（可能为空）
            %s

            ### 用户当前问题
            %s
            """;

    private AgentRouterPrompts() {
    }

    public static String build(String history, String message) {
        String h = history == null || history.isBlank() ? "（无）" : history.trim();
        String m = message == null ? "" : message.trim();
        return String.format(TEMPLATE, h, m);
    }
}
