package com.aics.prompt.factory;

/**
 * 业务场景：不同场景使用不同模板组合，便于扩展 RAG / Tool 等。
 */
public enum PromptScenario {
    /** 通用多轮对话。 */
    CHAT,
    /** 检索增强生成（知识库）。 */
    RAG,
    /** 工具调用 / 函数调用。 */
    TOOL
}
