package com.aics.memory.format;

import com.aics.memory.config.MemoryProperties;
import com.aics.memory.model.MessageTurn;
import com.aics.spi.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 记忆格式化：将 {@link MessageTurn} 列表与 {@link UserProfile} 转为纯文本，供上层拼 Prompt。
 * 不包含业务决策；截断策略由 {@link MemoryProperties#getMaxHistoryChars()} 控制。
 * <p>
 * 扩展点：可在此接入「历史摘要」「结构化标签」等，而不修改 {@link MemoryStore}。
 */
@Component
public class MemoryFormatter {

    private final MemoryProperties properties;

    public MemoryFormatter(MemoryProperties properties) {
        this.properties = properties;
    }

    /**
     * 将会话多轮对话格式化为「用户:/AI:」交替文本，并在末尾按配置做长度截断。
     */
    public String formatSessionHistory(List<MessageTurn> turns) {
        if (turns == null || turns.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (MessageTurn t : turns) {
            sb.append("用户: ").append(nullToEmpty(t.userMessage())).append('\n');
            sb.append("AI: ").append(nullToEmpty(t.assistantMessage())).append('\n');
        }
        return trimToMax(sb.toString(), properties.getMaxHistoryChars());
    }

    /**
     * 将用户画像格式化为固定前缀 {@code 【用户画像】} 下的键值行；无属性时返回空串。
     */
    public String formatUserProfile(UserProfile profile) {
        if (profile == null || profile.attributes().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("【用户画像】\n");
        profile.attributes().forEach((k, v) -> sb.append(k).append(": ").append(v).append('\n'));
        return sb.toString().trim();
    }

    /**
     * 画像在前、会话历史在后，中间空一行；编排层若只需 history，可仅用 {@link #formatSessionHistory(List)}。
     */
    public String combineProfileAndHistory(UserProfile profile, List<MessageTurn> turns) {
        String p = formatUserProfile(profile);
        String h = formatSessionHistory(turns);
        if (p.isEmpty()) {
            return h;
        }
        if (h.isEmpty()) {
            return p;
        }
        return p + "\n\n" + h;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /**
     * 超长文本保留尾部并加截断提示前缀；供内部与测试复用。
     *
     * @param maxChars 最大字符数；≤0 时返回空串
     */
    public String trimToMax(String text, int maxChars) {
        if (text == null || text.isEmpty() || maxChars <= 0) {
            return "";
        }
        if (text.length() <= maxChars) {
            return text;
        }
        return "...[历史已截断]\n" + text.substring(text.length() - maxChars);
    }
}
