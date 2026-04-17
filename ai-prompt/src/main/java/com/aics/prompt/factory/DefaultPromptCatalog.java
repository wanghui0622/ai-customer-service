package com.aics.prompt.factory;

import com.aics.prompt.template.PromptTemplate;

/**
 * 内置模板目录：后续可替换为 YAML / DB / 远程配置。
 * <p>
 * 包内可见，由 {@link PromptFactory} 统一对外暴露。
 */
final class DefaultPromptCatalog {

    private DefaultPromptCatalog() {
    }

    /**
     * 普通多轮聊天场景模板。
     *
     * @param version Prompt 版本
     * @return 对应版本的聊天模板
     */
    static PromptTemplate chat(PromptVersion version) {
        return switch (version) {
            case V1 -> PromptTemplate.of(
                    "你是专业 AI 客服助手，回答简洁、礼貌，使用中文。",
                    "用户：{userName}\n当前问题：{question}",
                    "");
            case V2 -> PromptTemplate.of(
                    "你是企业级 AI 客服。优先依据「参考上下文」作答；若无相关信息则说明并给出通用建议。语气专业、友好。",
                    "客户：{userName}\n咨询类别：{category}\n问题：{question}",
                    "");
        };
    }

    /**
     * 知识库 / RAG 检索增强场景模板。
     *
     * @param version Prompt 版本
     * @return 对应版本的 RAG 模板
     */
    static PromptTemplate rag(PromptVersion version) {
        return switch (version) {
            case V1 -> PromptTemplate.of(
                    "你是客服知识库助手。仅根据下方「参考上下文」回答；若上下文不足以回答，请明确说明并建议转人工。",
                    "用户问题：{question}",
                    "");
            case V2 -> PromptTemplate.of(
                    "你是 RAG 客服助手。必须引用知识片段编号（如 [1][2]）说明依据；禁止编造事实。",
                    "用户问题：{question}\n检索到的知识片段已放入「参考上下文」区域。",
                    "");
        };
    }

    /**
     * 工具 / Function Calling 场景模板。
     *
     * @param version Prompt 版本
     * @return 对应版本的工具调用模板
     */
    static PromptTemplate tool(PromptVersion version) {
        return switch (version) {
            case V1 -> PromptTemplate.of(
                    "你可以在需要时调用工具完成任务。先思考再决定是否调用；工具返回后整合为自然语言回复。",
                    "用户目标：{goal}\n可用工具说明：{toolHints}",
                    "");
            case V2 -> PromptTemplate.of(
                    "你是带工具能力的客服代理。严格遵守工具调用协议：一次只选一个最相关工具，参数必须来自用户表述或上下文。",
                    "用户目标：{goal}\n工具列表与约束：{toolHints}",
                    "");
        };
    }
}
