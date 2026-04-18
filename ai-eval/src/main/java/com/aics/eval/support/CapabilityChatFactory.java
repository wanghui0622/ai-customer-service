package com.aics.eval.support;

import com.aics.eval.AiVersion;
import com.aics.service.chat.AiChatService;
import com.aics.service.orchestration.policy.RagEligibilityPolicy;
import com.aics.service.orchestration.policy.ToolEligibilityPolicy;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.ToolExecutor;
import com.aics.prompt.composer.DefaultPromptComposer;

import java.util.List;

/**
 * 按档位装配 {@link AiChatService}（无 Spring），评估与博客复现实验共用。
 */
public final class CapabilityChatFactory {

    public static final String KB =
            "订单支付成功后1-3个工作日发货；可在订单页查看物流状态与承运商信息。";

    public static final String TOOL_JSON =
            "{\"orderId\":\"123\",\"status\":\"拣货中\",\"物流\":\"华东仓已揽收\"}";

    private CapabilityChatFactory() {
    }

    public static AiChatService build(AiVersion version, RecordingLlmClient llm) {
        return switch (version) {
            case BASE -> new AiChatService(
                    new EvalChatMemory(""),
                    q -> List.of(),
                    m -> "",
                    new PassthroughPromptComposer(),
                    llm,
                    m -> false,
                    m -> false
            );
            case PROMPT -> new AiChatService(
                    new EvalChatMemory(""),
                    q -> List.of(),
                    m -> "",
                    new DefaultPromptComposer(),
                    llm,
                    m -> false,
                    m -> false
            );
            case RAG -> new AiChatService(
                    new EvalChatMemory(""),
                    ragOn(),
                    m -> "",
                    new DefaultPromptComposer(),
                    llm,
                    m -> true,
                    m -> false
            );
            case MEMORY -> new AiChatService(
                    new EvalChatMemory("用户: 钱付了吗\nAI: 已支付，待仓库发货。\n"),
                    ragOn(),
                    m -> "",
                    new DefaultPromptComposer(),
                    llm,
                    m -> true,
                    m -> false
            );
            case FULL -> new AiChatService(
                    new EvalChatMemory("用户: 钱付了吗\nAI: 已支付，待仓库发货。\n"),
                    ragOn(),
                    toolsOn(),
                    new DefaultPromptComposer(),
                    llm,
                    m -> true,
                    m -> true
            );
        };
    }

    private static KnowledgeRetriever ragOn() {
        return q -> List.of(KB);
    }

    private static ToolExecutor toolsOn() {
        return m -> TOOL_JSON;
    }
}
