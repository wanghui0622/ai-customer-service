package com.aics.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON 序列化小工具，基于共享 {@link ObjectMapper}。
 */
public final class JsonUtils {

    /** 模块内复用的 Jackson 映射器。 */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 任意可序列化对象
     * @return JSON 文本
     * @throws IllegalStateException 序列化失败时包装原始异常
     */
    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize object", e);
        }
    }
}
