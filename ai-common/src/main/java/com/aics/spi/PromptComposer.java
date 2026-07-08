package com.aics.spi;

import com.aics.model.ToolCallRecord;

import java.util.List;

/**
 * Prompt 组装（由 ai-prompt 实现）。
 */
public interface PromptComposer {

    /**
     * 组装 Prompt
     */
    String build(String history, List<String> context, String toolResult, String message);

    /**
     * 多步工具结果版本；默认回退到 {@link #build(String, List, String, String)}。
     */
    default String build(String history,
                         List<String> context,
                         String toolResult,
                         String message,
                         List<ToolCallRecord> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            return build(history, context, toolResult, message);
        }
        StringBuilder aggregated = new StringBuilder();
        for (int i = 0; i < toolCalls.size(); i++) {
            ToolCallRecord call = toolCalls.get(i);
            aggregated.append('[').append(i + 1).append("] ")
                    .append(call.name()).append(": ").append(call.output()).append('\n');
        }
        String merged = aggregated.toString().trim();
        if (toolResult != null && !toolResult.isBlank()) {
            merged = merged.isBlank() ? toolResult : merged + "\n" + toolResult;
        }
        return build(history, context, merged, message);
    }
}
