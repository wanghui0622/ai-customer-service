package com.aics.prompt.builder;

import com.aics.prompt.template.PromptTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 链式构建 Prompt：支持模板 + 历史 + 上下文 + 指令，以及变量替换。
 * <p>
 * 典型顺序：{@code template -> variables -> history -> context -> instruction -> build}
 */
public final class PromptBuilder {

    /** 基础模板，在 {@link #build()} 时先做变量替换。 */
    private PromptTemplate base = PromptTemplate.of("", "", "");
    /** 占位符变量累积。 */
    private final Map<String, String> variables = new HashMap<>();
    /** 历史对话文本。 */
    private String history = "";
    /** RAG 或外部检索得到的上下文。 */
    private String context = "";
    /** 额外任务说明。 */
    private String instruction = "";
    /** 是否在 user 块中追加「历史对话」小节。 */
    private boolean includeHistorySection = true;
    /** 是否在 user 块中追加「参考上下文」小节。 */
    private boolean includeContextSection = true;

    /**
     * 设置基础模板；{@code null} 视为空模板。
     *
     * @param template 多角色模板
     * @return {@code this}
     */
    public PromptBuilder template(PromptTemplate template) {
        this.base = Objects.requireNonNullElse(template, PromptTemplate.of("", "", ""));
        return this;
    }

    /**
     * 追加单个占位符变量（与模板中 {@code {key}} 对应）。
     *
     * @param key   占位符键
     * @param value 替换值；键或值为 {@code null} 时忽略
     * @return {@code this}
     */
    public PromptBuilder variable(String key, String value) {
        if (key != null && value != null) {
            variables.put(key, value);
        }
        return this;
    }

    /**
     * 批量追加占位符变量。
     *
     * @param extra 键值对；{@code null} 忽略
     * @return {@code this}
     */
    public PromptBuilder variables(Map<String, String> extra) {
        if (extra != null) {
            extra.forEach((k, v) -> {
                if (k != null && v != null) {
                    variables.put(k, v);
                }
            });
        }
        return this;
    }

    /**
     * 设置会话历史，将拼入「历史对话」小节（若启用）。
     *
     * @param history 历史文本；{@code null} 视为空
     * @return {@code this}
     */
    public PromptBuilder history(String history) {
        this.history = history == null ? "" : history;
        return this;
    }

    /**
     * 设置参考上下文（如 RAG 检索片段），将拼入「参考上下文」小节（若启用）。
     *
     * @param context 上下文文本；{@code null} 视为空
     * @return {@code this}
     */
    public PromptBuilder context(String context) {
        this.context = context == null ? "" : context;
        return this;
    }

    /**
     * 设置任务说明，将拼入「任务说明」小节。
     *
     * @param instruction 说明文本；{@code null} 视为空
     * @return {@code this}
     */
    public PromptBuilder instruction(String instruction) {
        this.instruction = instruction == null ? "" : instruction;
        return this;
    }

    /**
     * 控制是否在 user 块中包含历史与上下文两个小节。
     *
     * @param includeHistory 是否包含「历史对话」
     * @param includeContext 是否包含「参考上下文」
     * @return {@code this}
     */
    public PromptBuilder sections(boolean includeHistory, boolean includeContext) {
        this.includeHistorySection = includeHistory;
        this.includeContextSection = includeContext;
        return this;
    }

    /**
     * 完成变量替换并按规则拼接 user 块，生成 {@link BuiltPrompt}。
     *
     * @return 不可变的构建结果
     */
    public BuiltPrompt build() {
        PromptTemplate rendered = base.render(variables);
        String sys = rendered.system();
        String userCore = rendered.user();
        String asst = rendered.assistant();

        StringBuilder userBlock = new StringBuilder();
        if (!userCore.isEmpty()) {
            userBlock.append(userCore.trim());
        }
        if (includeHistorySection && !history.isEmpty()) {
            appendSection(userBlock, "历史对话", history);
        }
        if (includeContextSection && !context.isEmpty()) {
            appendSection(userBlock, "参考上下文", context);
        }
        if (!instruction.isEmpty()) {
            appendSection(userBlock, "任务说明", instruction);
        }

        return new BuiltPrompt(sys, userBlock.toString(), asst, rendered);
    }

    /**
     * 向 user 块追加 Markdown 风格小节。
     */
    private static void appendSection(StringBuilder sb, String title, String body) {
        if (!sb.isEmpty()) {
            sb.append("\n\n");
        }
        sb.append("### ").append(title).append("\n").append(body.trim());
    }
}
