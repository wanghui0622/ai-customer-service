package com.aics.rag.prompt;

import com.aics.prompt.builder.BuiltPrompt;

/**
 * 将 {@link BuiltPrompt} 拼成单条文本，便于对接仅支持单字符串的 LLM 客户端（与 ai-core 的 {@code LlmProvider} 对齐）。
 */
public final class PromptAssembly {

    private PromptAssembly() {
    }

    /**
     * system 与 user/assistant 块顺序拼接。
     */
    public static String toSingleString(BuiltPrompt built) {
        String system = built.system();
        String body = built.combinedUserBlock();
        if (system.isEmpty()) {
            return body;
        }
        if (body.isEmpty()) {
            return system;
        }
        return system + "\n\n" + body;
    }
}
