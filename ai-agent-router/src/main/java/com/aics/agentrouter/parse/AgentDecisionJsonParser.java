package com.aics.agentrouter.parse;

import com.aics.agentrouter.AgentDecision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 从模型原始输出中抽取 JSON 并解析为 {@link AgentDecision}（容忍 ```json 围栏）。
 */
public final class AgentDecisionJsonParser {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private AgentDecisionJsonParser() {
    }

    public static AgentDecision parse(String rawModelOutput) {
        try {
            String json = extractJsonObject(rawModelOutput);
            JsonNode node = MAPPER.readTree(json);
            boolean useRag = node.path("useRag").asBoolean(false);
            boolean useTools = node.path("useTools").asBoolean(false);
            String toolName = node.path("toolName").asText("");
            String reason = node.path("reason").asText("");
            return new AgentDecision(useRag, useTools, toolName, reason);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("解析 router JSON 失败", e);
        }
    }

    static String extractJsonObject(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("router 输出为空");
        }
        String s = raw.trim();
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("router 输出中未找到 JSON 对象");
        }
        return s.substring(start, end + 1);
    }
}
