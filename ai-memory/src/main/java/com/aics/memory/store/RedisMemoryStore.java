package com.aics.memory.store;

import com.aics.memory.config.MemoryProperties;
import com.aics.memory.model.MessageTurn;
import com.aics.spi.UserProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 会话与画像存储；需配置 {@code spring.data.redis.*} 与 {@code aics.memory.store=redis}。
 */
@Component
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "aics.memory", name = "store", havingValue = "redis")
public class RedisMemoryStore implements MemoryStore {

    private static final TypeReference<List<MessageTurn>> TURNS_TYPE = new TypeReference<>() {
    };

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final String keyPrefix;
    private final Duration sessionTtl;

    public RedisMemoryStore(StringRedisTemplate redis,
                            ObjectMapper objectMapper,
                            MemoryProperties memoryProperties) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.keyPrefix = memoryProperties.getRedisKeyPrefix();
        this.sessionTtl = Duration.ofSeconds(Math.max(60, memoryProperties.getSessionTtlSeconds()));
    }

    private String sessionKey(String sessionId) {
        return keyPrefix + "session:" + sessionId;
    }

    private String profileKey(String userId) {
        return keyPrefix + "profile:" + userId;
    }

    @Override
    public List<String> listSessionIds() {
        String pattern = keyPrefix + "session:*";
        return Objects.requireNonNull(redis.keys(pattern)).stream()
                .map(k -> k.substring((keyPrefix + "session:").length()))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageTurn> listTurns(String sessionId) {
        String raw = redis.opsForValue().get(sessionKey(sessionId));
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        try {
            List<MessageTurn> list = objectMapper.readValue(raw, TURNS_TYPE);
            return List.copyOf(list);
        } catch (Exception e) {
            throw new IllegalStateException("反序列化会话失败: " + sessionId, e);
        }
    }

    @Override
    public void appendTurn(String sessionId, MessageTurn turn) {
        List<MessageTurn> list = new ArrayList<>(listTurns(sessionId));
        list.add(turn);
        try {
            String json = objectMapper.writeValueAsString(list);
            String key = sessionKey(sessionId);
            redis.opsForValue().set(key, json);
            redis.expire(key, sessionTtl.toSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("写入会话失败: " + sessionId, e);
        }
    }

    @Override
    public void clearSession(String sessionId) {
        redis.delete(sessionKey(sessionId));
    }

    @Override
    public UserProfile loadProfile(String userId) {
        String raw = redis.opsForValue().get(profileKey(userId));
        if (raw == null || raw.isBlank()) {
            return UserProfile.empty(userId);
        }
        try {
            Map<String, Object> map = objectMapper.readValue(raw, new TypeReference<>() {
            });
            @SuppressWarnings("unchecked")
            Map<String, String> attrs = (Map<String, String>) map.getOrDefault("attributes", Map.of());
            return new UserProfile(String.valueOf(map.get("userId")), attrs);
        } catch (Exception e) {
            throw new IllegalStateException("反序列化画像失败: " + userId, e);
        }
    }

    @Override
    public void saveProfile(String userId, UserProfile profile) {
        try {
            Map<String, Object> map = Map.of(
                    "userId", profile.userId(),
                    "attributes", profile.attributes()
            );
            String json = objectMapper.writeValueAsString(map);
            String key = profileKey(userId);
            redis.opsForValue().set(key, json);
            redis.expire(key, sessionTtl.toSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("写入画像失败: " + userId, e);
        }
    }
}
