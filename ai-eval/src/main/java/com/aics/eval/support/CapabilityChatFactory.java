package com.aics.eval.support;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.FixedAgentRouter;
import com.aics.eval.AiVersion;
import com.aics.service.chat.AiChatService;
import com.aics.service.config.OrchestrationProperties;
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

    private static final OrchestrationProperties EVAL_ORCH = evalOrchestration();

    private CapabilityChatFactory() {
    }

    private static OrchestrationProperties evalOrchestration() {
        OrchestrationProperties p = new OrchestrationProperties();
        p.setRagEnabled(true);
        p.setToolsEnabled(true);
        return p;
    }

    public static AiChatService build(AiVersion version, RecordingLlmClient llm) {
        return switch (version) {
            case BASE -> new AiChatService(
                    new EvalChatMemory(""),
                    q -> List.of(),
                    m -> "",
                    new PassthroughPromptComposer(),
                    llm,
                    new FixedAgentRouter(AgentDecision.none()),
                    EVAL_ORCH
            );
            case PROMPT -> new AiChatService(
                    new EvalChatMemory(""),
                    q -> List.of(),
                    m -> "",
                    new DefaultPromptComposer(),
                    llm,
                    new FixedAgentRouter(AgentDecision.none()),
                    EVAL_ORCH
            );
            case RAG -> new AiChatService(
                    new EvalChatMemory(""),
                    ragOn(),
                    m -> "",
                    new DefaultPromptComposer(),
                    llm,
                    new FixedAgentRouter(new AgentDecision(true, false, "", "eval-rag")),
                    EVAL_ORCH
            );
            case MEMORY -> new AiChatService(
                    new EvalChatMemory("用户: 钱付了吗\nAI: 已支付，待仓库发货。\n"),
                    ragOn(),
                    m -> "",
                    new DefaultPromptComposer(),
                    llm,
                    new FixedAgentRouter(new AgentDecision(true, false, "", "eval-memory")),
                    EVAL_ORCH
            );
            case FULL -> new AiChatService(
                    new EvalChatMemory("用户: 钱付了吗\nAI: 已支付，待仓库发货。\n"),
                    ragOn(),
                    toolsOn(),
                    new DefaultPromptComposer(),
                    llm,
                    new FixedAgentRouter(new AgentDecision(true, true, "", "eval-full")),
                    EVAL_ORCH
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
