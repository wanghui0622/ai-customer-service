package com.aics.memory.store;

import com.aics.memory.model.MessageTurn;
import com.aics.spi.UserProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进程内默认实现：{@link ConcurrentHashMap} 保存会话轮次与用户画像，重启即丢失，适合开发与单测。
 */
@Component
@ConditionalOnProperty(prefix = "aics.memory", name = "store", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryMemoryStore implements MemoryStore {

    /** sessionId → 有序对话轮次 */
    private final Map<String, List<MessageTurn>> sessions = new ConcurrentHashMap<>();
    /** userId → 用户画像 */
    private final Map<String, UserProfile> profiles = new ConcurrentHashMap<>();

    @Override
    public List<String> listSessionIds() {
        return sessions.keySet().stream().sorted().toList();
    }

    @Override
    public List<MessageTurn> listTurns(String sessionId) {
        return List.copyOf(sessions.getOrDefault(sessionId, List.of()));
    }

    @Override
    public void appendTurn(String sessionId, MessageTurn turn) {
        sessions.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(turn);
    }

    @Override
    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public UserProfile loadProfile(String userId) {
        return profiles.getOrDefault(userId, UserProfile.empty(userId));
    }

    @Override
    public void saveProfile(String userId, UserProfile profile) {
        profiles.put(userId, profile);
    }
}
