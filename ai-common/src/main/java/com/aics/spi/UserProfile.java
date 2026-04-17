package com.aics.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 长期记忆中的用户画像（可序列化扩展）。
 */
public final class UserProfile {

    private final String userId;
    private final Map<String, String> attributes;

    public UserProfile(String userId, Map<String, String> attributes) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static UserProfile empty(String userId) {
        return new UserProfile(userId, Map.of());
    }

    public String userId() {
        return userId;
    }

    public Map<String, String> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public UserProfile withAttribute(String key, String value) {
        Map<String, String> m = new HashMap<>(attributes);
        m.put(key, value);
        return new UserProfile(userId, m);
    }
}
