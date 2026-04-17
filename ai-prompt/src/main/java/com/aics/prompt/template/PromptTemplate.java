package com.aics.prompt.template;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多角色 Prompt 模板：分别持有 system / user / assistant 片段，支持 {@code {var}} 占位符替换。
 * <p>
 * assistant 片段常用于 few-shot 示例或对话前缀，可按业务选择是否参与最终拼接。
 */
public final class PromptTemplate {

    /** 占位符格式：{@code {key}}，key 仅含字母、数字、下划线与点号。 */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z0-9_.]+)}");

    /** 系统提示片段。 */
    private final String system;
    /** 用户侧模板片段。 */
    private final String user;
    /** 助手侧模板片段（如 few-shot）。 */
    private final String assistant;

    /**
     * 使用三段文本构造模板；{@code null} 视为空字符串。
     *
     * @param system    系统提示
     * @param user      用户模板
     * @param assistant 助手模板
     */
    public PromptTemplate(String system, String user, String assistant) {
        this.system = system == null ? "" : system;
        this.user = user == null ? "" : user;
        this.assistant = assistant == null ? "" : assistant;
    }

    /**
     * 仅包含 system 段的模板。
     *
     * @param system 系统提示文本
     * @return 模板实例
     */
    public static PromptTemplate ofSystem(String system) {
        return new PromptTemplate(system, "", "");
    }

    /**
     * 仅包含 user 段的模板。
     *
     * @param user 用户模板文本
     * @return 模板实例
     */
    public static PromptTemplate ofUser(String user) {
        return new PromptTemplate("", user, "");
    }

    /**
     * 包含 system 与 user，不含 assistant。
     *
     * @param system 系统提示
     * @param user   用户模板
     * @return 模板实例
     */
    public static PromptTemplate of(String system, String user) {
        return new PromptTemplate(system, user, "");
    }

    /**
     * 完整三段模板工厂方法。
     *
     * @param system    系统提示
     * @param user      用户模板
     * @param assistant 助手模板
     * @return 模板实例
     */
    public static PromptTemplate of(String system, String user, String assistant) {
        return new PromptTemplate(system, user, assistant);
    }

    /**
     * @return 系统提示原文（未替换占位符）
     */
    public String system() {
        return system;
    }

    /**
     * @return 用户模板原文
     */
    public String user() {
        return user;
    }

    /**
     * @return 助手模板原文
     */
    public String assistant() {
        return assistant;
    }

    /**
     * 使用变量渲染各段文本；未提供的占位符保留原样，便于排查。
     *
     * @param variables 占位符键值；{@code null} 视为空映射
     * @return 替换后的新模板实例（不可变）
     */
    public PromptTemplate render(Map<String, String> variables) {
        Map<String, String> safe = variables == null ? Map.of() : variables;
        return new PromptTemplate(
                apply(system, safe),
                apply(user, safe),
                apply(assistant, safe));
    }

    /**
     * 先按变量渲染，再返回指定角色的文本。
     *
     * @param role      要取出的角色段
     * @param variables 占位符变量
     * @return 该角色渲染后的字符串
     */
    public String renderRole(PromptRole role, Map<String, String> variables) {
        PromptTemplate r = render(variables);
        return switch (role) {
            case SYSTEM -> r.system;
            case USER -> r.user;
            case ASSISTANT -> r.assistant;
        };
    }

    /**
     * 将 {@code raw} 中的 {@code {key}} 替换为 {@code variables} 中对应值。
     */
    private static String apply(String raw, Map<String, String> variables) {
        if (raw.isEmpty() || variables.isEmpty()) {
            return raw;
        }
        Matcher m = PLACEHOLDER.matcher(raw);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String key = m.group(1);
            String value = variables.get(key);
            m.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value : m.group(0)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 返回非空角色段与文本的只读映射，便于按角色遍历。
     *
     * @return 角色到文本的映射
     */
    public Map<PromptRole, String> asRoleMap() {
        Map<PromptRole, String> map = new HashMap<>(3);
        if (!system.isEmpty()) {
            map.put(PromptRole.SYSTEM, system);
        }
        if (!user.isEmpty()) {
            map.put(PromptRole.USER, user);
        }
        if (!assistant.isEmpty()) {
            map.put(PromptRole.ASSISTANT, assistant);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PromptTemplate that)) {
            return false;
        }
        return Objects.equals(system, that.system)
                && Objects.equals(user, that.user)
                && Objects.equals(assistant, that.assistant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, user, assistant);
    }
}
