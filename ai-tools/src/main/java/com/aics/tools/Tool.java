package com.aics.tools;

/**
 * 可调用工具抽象：与 LLM 解耦，便于对接 MCP / OpenAI Function Calling（通过 {@link #inputSchemaJson()} 描述入参）。
 * <p>
 * 实现类应注册为 Spring Bean，由 {@link com.aics.tools.registry.ToolRegistry} 统一收集。
 */
public interface Tool {

    /**
     * 全局唯一工具名，用于路由与注册（建议 snake_case，如 {@code order_query}）。
     */
    String name();

    /**
     * 人类可读说明，可供 Prompt 列举「可用工具」时使用。
     */
    String description();

    /**
     * 入参 JSON Schema（字符串），无结构要求时可返回空对象 {@code "{}"}。
     */
    String inputSchemaJson();

    /**
     * 执行工具逻辑；{@code input} 一般为自然语言或序列化后的参数，由调用方约定。
     *
     * @return 文本化结果，供编排层写入 Prompt；异常应由实现捕获并转为错误信息字符串，避免向上抛出未检查异常。
     */
    String execute(String input);
}
