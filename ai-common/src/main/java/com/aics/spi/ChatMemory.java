package com.aics.spi;

/**
 * 对话与画像记忆（由 ai-memory 实现）。
 */
public interface ChatMemory {

    /**
     * 加载会话历史，供 Prompt 使用（通常已含长度截断与格式化）。
     */
    String loadHistory(String sessionId);

    /**
     * 追加一轮对话。
     */
    void saveMessage(String sessionId, String userMsg, String aiMsg);

    /**
     * 加载用户画像。
     * @param userId
     * @return
     */
    UserProfile loadUserProfile(String userId);

    /**
     * 保存用户画像。
     * @param userId
     * @param profile
     */
    void saveUserProfile(String userId, UserProfile profile);
}
