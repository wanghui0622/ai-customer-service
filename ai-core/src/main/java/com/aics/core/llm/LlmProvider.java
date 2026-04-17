package com.aics.core.llm;

/**
 * 大语言模型调用抽象，屏蔽具体供应商（OpenAI、Azure、本地模型等）实现细节。
 */
public interface LlmProvider {

    /**
     * 根据完整提示词发起一次对话补全请求。
     *
     * @param prompt 已拼接好的提示文本（可含 system/user 语义，由上层决定格式）
     * @return 模型生成的回复文本
     */
    String chat(String prompt);
}
