package com.aics.prompt.evaluator;

import com.aics.prompt.builder.BuiltPrompt;
import com.aics.prompt.config.PromptEngineProperties;
import com.aics.prompt.factory.PromptScenario;
import com.aics.prompt.factory.PromptVersion;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Service;

/**
 * 内存中的 Prompt 输入输出记录，供基础评估与排障；后续可换为消息队列或 OLAP。
 */
@Service
public class PromptEvaluationService {

    /** 新日志从头部插入，按时间近似倒序遍历。 */
    private final ConcurrentLinkedDeque<PromptLog> buffer = new ConcurrentLinkedDeque<>();
    /** 用于读取日志保留上限等配置。 */
    private final PromptEngineProperties properties;

    /**
     * @param properties Prompt 引擎配置（含 {@code log-retention-max}）
     */
    public PromptEvaluationService(PromptEngineProperties properties) {
        this.properties = properties;
    }

    /**
     * 记录一次调用：将 {@link BuiltPrompt} 各段与模型输出写入内存队列，并触发容量裁剪。
     *
     * @param scenario    业务场景
     * @param version     Prompt 版本
     * @param built       构建完成的提示快照
     * @param modelOutput 模型返回文本
     * @return 新生成的日志条目
     */
    public PromptLog record(
            PromptScenario scenario,
            PromptVersion version,
            BuiltPrompt built,
            String modelOutput) {
        PromptLog log = new PromptLog(
                UUID.randomUUID().toString(),
                Instant.now(),
                scenario,
                version,
                safe(built.system()),
                safe(built.user()),
                safe(built.assistant()),
                safe(modelOutput));
        buffer.addFirst(log);
        trim();
        return log;
    }

    /**
     * 返回最近若干条日志（从新到旧），最多 {@code limit} 条。
     *
     * @param limit 条数上限；非正数时返回空列表
     * @return 不可变列表
     */
    public List<PromptLog> recent(int limit) {
        int n = Math.max(0, limit);
        if (n == 0) {
            return List.of();
        }
        List<PromptLog> list = new ArrayList<>(Math.min(n, buffer.size()));
        int i = 0;
        for (PromptLog log : buffer) {
            list.add(log);
            i++;
            if (i >= n) {
                break;
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * 清空全部内存日志（例如测试或运维重置）。
     */
    public void clear() {
        buffer.clear();
    }

    /**
     * 按 {@link PromptEngineProperties#getLogRetentionMax()} 丢弃最旧记录。
     */
    private void trim() {
        int max = Math.max(1, properties.getLogRetentionMax());
        while (buffer.size() > max) {
            buffer.removeLast();
        }
    }

    /** null 安全转为空串，避免日志中出现字面量 "null"。 */
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
