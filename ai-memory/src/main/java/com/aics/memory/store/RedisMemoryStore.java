package com.aics.memory.store;

import com.aics.memory.model.MessageTurn;
import com.aics.spi.UserProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Redis 持久化占位实现。设置 {@code aics.memory.store=redis} 时生效；
 * 接入生产需引入 Redis 客户端、定义 key 前缀与 TTL、序列化 {@link MessageTurn} / {@link UserProfile}。
 */
@Component
@ConditionalOnProperty(prefix = "aics.memory", name = "store", havingValue = "redis")
public class RedisMemoryStore implements MemoryStore {

    @Override
    public List<MessageTurn> listTurns(String sessionId) {
        throw unsupported();
    }

    @Override
    public void appendTurn(String sessionId, MessageTurn turn) {
        throw unsupported();
    }

    @Override
    public void clearSession(String sessionId) {
        throw unsupported();
    }

    @Override
    public UserProfile loadProfile(String userId) {
        throw unsupported();
    }

    @Override
    public void saveProfile(String userId, UserProfile profile) {
        throw unsupported();
    }

    /** 所有方法在未实现前统一抛出，避免误用静默失败。 */
    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(
                "RedisMemoryStore 为预留实现：请配置 RedisTemplate 与 key 规范后实现 listTurns/appendTurn/saveProfile。");
    }
}
