package com.aics.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * 单次工具调用记录，供 ReAct 多步编排与 trace 展示。
 */
public record ToolCallRecord(
        String name,
        String input,
        String output,
        Instant timestamp
) implements Serializable {
    public ToolCallRecord {
        name = name == null ? "" : name.trim();
        input = input == null ? "" : input;
        output = output == null ? "" : output;
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
