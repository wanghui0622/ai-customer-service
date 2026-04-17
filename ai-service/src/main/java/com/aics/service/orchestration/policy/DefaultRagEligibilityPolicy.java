package com.aics.service.orchestration.policy;

import com.aics.service.config.OrchestrationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * 基于可组合谓词的 RAG 门控：短寒暄默认不检索；疑问/长度/主题词等正向信号触发检索。
 * 扩展方式：新增 {@link Predicate} Bean 并注入列表（可演进为责任链）。
 */
@Component
public class DefaultRagEligibilityPolicy implements RagEligibilityPolicy {

    private final OrchestrationProperties properties;
    private final List<Predicate<String>> positiveSignals;

    public DefaultRagEligibilityPolicy(OrchestrationProperties properties) {
        this.properties = properties;
        this.positiveSignals = List.of(
                KnowledgeIntentSignals::containsQuestionMark,
                m -> m != null && m.length() >= properties.getMinMessageLengthHintForRag(),
                KnowledgeIntentSignals::containsTopicHints
        );
    }

    @Override
    public boolean shouldRetrieveKnowledge(String userMessage) {
        if (!properties.isRagEnabled()) {
            return false;
        }
        if (userMessage == null || userMessage.isBlank()) {
            return false;
        }
        if (KnowledgeIntentSignals.looksLikeSmallTalk(userMessage)) {
            return false;
        }
        return positiveSignals.stream().anyMatch(p -> p.test(userMessage));
    }
}
