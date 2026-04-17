package com.aics.core.memory;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 JVM 内存的 {@link MemoryStore} 实现，适用于单机开发与演示；生产多实例需换分布式存储。
 */
@Component
public class InMemoryChatMemory implements MemoryStore {

    /** 会话 ID → 累积的对话文本。 */
    private final ConcurrentHashMap<String, StringBuilder> store = new ConcurrentHashMap<>();

    @Override
    public String load(String sessionId) {
        return store.getOrDefault(sessionId, new StringBuilder()).toString();
    }

    @Override
    public void save(String sessionId, String userMsg, String aiMsg) {
        store.computeIfAbsent(sessionId, k -> new StringBuilder())
                .append("用户: ").append(userMsg).append("\n")
                .append("AI: ").append(aiMsg).append("\n");
    }
}
