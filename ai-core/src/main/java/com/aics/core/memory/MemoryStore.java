package com.aics.core.memory;

/**
 * 会话级对话记忆存储抽象，短期可实现为内存，长期可换 Redis / DB。
 */
public interface MemoryStore {

    /**
     * 加载指定会话的历史文本（格式由实现决定）。
     *
     * @param sessionId 会话标识
     * @return 历史内容；无记录时通常返回空字符串
     */
    String load(String sessionId);

    /**
     * 追加一轮用户消息与助手回复。
     *
     * @param sessionId 会话标识
     * @param userMsg   用户输入
     * @param aiMsg     模型回复
     */
    void save(String sessionId, String userMsg, String aiMsg);
}
