package com.aics.spi;

import java.util.List;

/**
 * Prompt 组装（由 ai-prompt 实现）。
 */
public interface PromptComposer {

    String build(String history, List<String> context, String toolResult, String message);
}
