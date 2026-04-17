package com.aics.service.orchestration.policy;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 知识类意图的轻量信号（规则可逐步迁移为模型分类或独立服务）。
 */
public final class KnowledgeIntentSignals {

    private static final Pattern SMALL_TALK = Pattern.compile(
            "^(你好|在吗|在么|嗨|hi|hello|哈喽|拜拜|再见|谢谢|感谢)\\s*[!！.。…]*$",
            Pattern.CASE_INSENSITIVE);

    private static final List<String> TOPIC_HINTS = List.of(
            "什么", "怎么", "如何", "为什么", "哪", "是否", "可以", "能", "吗", "么",
            "政策", "规则", "退款", "退货", "包邮", "发票", "保修", "售后", "客服"
    );

    private KnowledgeIntentSignals() {
    }

    public static boolean looksLikeSmallTalk(String message) {
        if (message == null) {
            return true;
        }
        String m = message.trim();
        return m.length() <= 8 && SMALL_TALK.matcher(m).matches();
    }

    public static boolean containsQuestionMark(String message) {
        return message != null && (message.contains("？") || message.contains("?"));
    }

    public static boolean containsTopicHints(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        return TOPIC_HINTS.stream().anyMatch(message::contains);
    }
}
