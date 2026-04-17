package com.aics.prompt.builder;

import com.aics.prompt.template.PromptTemplate;

/**
 * 构建完成后的 Prompt 快照：便于传给 LLM 或写入 {@link com.aics.prompt.evaluator.PromptLog}。
 *
 * @param system          系统提示最终文本
 * @param user            用户侧合并后的文本（含模板 user 段与 history/context/instruction 等拼接）
 * @param assistant       助手侧前缀文本（如 few-shot）
 * @param sourceTemplate  渲染变量后的源模板引用，便于审计；可为 {@code null}
 */
public record BuiltPrompt(
        String system,
        String user,
        String assistant,
        PromptTemplate sourceTemplate
) {

    /**
     * @return 三端均为空且不带源模板的空快照
     */
    public static BuiltPrompt empty() {
        return new BuiltPrompt("", "", "", null);
    }

    /**
     * 将 assistant 前缀与 user 块合并为单条用户向内容（按常见「前缀 + 正文」顺序）。
     *
     * @return 合并后的字符串
     */
    public String combinedUserBlock() {
        StringBuilder sb = new StringBuilder();
        if (assistant != null && !assistant.isEmpty()) {
            sb.append(assistant).append("\n\n");
        }
        if (user != null && !user.isEmpty()) {
            sb.append(user);
        }
        return sb.toString();
    }
}
