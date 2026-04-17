package com.aics.prompt.evaluator;

import com.aics.prompt.factory.PromptScenario;
import com.aics.prompt.factory.PromptVersion;
import java.time.Instant;

/**
 * 一次 Prompt 调用的轻量审计记录，便于后续评估与 A/B 分析。
 *
 * @param id               日志唯一标识
 * @param createdAt        记录时间（UTC）
 * @param scenario         业务场景
 * @param version          使用的 Prompt 版本
 * @param systemText       最终 system 文本
 * @param userText         最终 user 侧文本
 * @param assistantPrefix  最终 assistant 前缀（若有）
 * @param modelOutput      模型输出原文
 */
public record PromptLog(
        String id,
        Instant createdAt,
        PromptScenario scenario,
        PromptVersion version,
        String systemText,
        String userText,
        String assistantPrefix,
        String modelOutput
) {
}
