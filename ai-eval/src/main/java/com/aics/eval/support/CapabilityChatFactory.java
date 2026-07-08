package com.aics.eval.support;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.AgentRouter;
import com.aics.agentrouter.FixedAgentRouter;
import com.aics.eval.AiVersion;
import com.aics.graph.CustomerServiceGraph;
import com.aics.graph.config.GraphOrchestrationProperties;
import com.aics.service.chat.AiChatService;
import com.aics.service.config.OrchestrationProperties;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolCatalog;
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
        p.setEngine("linear");
        p.setRagEnabled(true);
        p.setToolsEnabled(true);
        p.setAgentRouterLlmEnabled(false);
        return p;
    }

    public static AiChatService build(AiVersion version, RecordingLlmClient llm) {
        try {
            return switch (version) {
                case BASE -> assemble(
                        new EvalChatMemory(""),
                        q -> List.of(),
                        m -> "",
                        new PassthroughPromptComposer(),
                        llm,
                        new FixedAgentRouter(AgentDecision.none())
                );
                case PROMPT -> assemble(
                        new EvalChatMemory(""),
                        q -> List.of(),
                        m -> "",
                        new DefaultPromptComposer(),
                        llm,
                        new FixedAgentRouter(AgentDecision.none())
                );
                case RAG -> assemble(
                        new EvalChatMemory(""),
                        ragOn(),
                        m -> "",
                        new DefaultPromptComposer(),
                        llm,
                        new FixedAgentRouter(new AgentDecision(true, false, "", "eval-rag"))
                );
                case MEMORY -> assemble(
                        new EvalChatMemory("用户: 钱付了吗\nAI: 已支付，待仓库发货。\n"),
                        ragOn(),
                        m -> "",
                        new DefaultPromptComposer(),
                        llm,
                        new FixedAgentRouter(new AgentDecision(true, false, "", "eval-memory"))
                );
                case FULL -> assemble(
                        new EvalChatMemory("用户: 钱付了吗\nAI: 已支付，待仓库发货。\n"),
                        ragOn(),
                        toolsOn(),
                        new DefaultPromptComposer(),
                        llm,
                        new FixedAgentRouter(new AgentDecision(true, true, "", "eval-full"))
                );
            };
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build eval AiChatService", e);
        }
    }

    private static AiChatService assemble(
            ChatMemory memory,
            KnowledgeRetriever rag,
            ToolExecutor tools,
            PromptComposer promptComposer,
            RecordingLlmClient llm,
            AgentRouter agentRouter) throws Exception {
        CustomerServiceGraph graph = new CustomerServiceGraph(
                memory,
                rag,
                tools,
                promptComposer,
                llm,
                agentRouter,
                emptyCatalog(),
                evalGraphProperties());
        return new AiChatService(
                memory,
                rag,
                tools,
                promptComposer,
                llm,
                agentRouter,
                EVAL_ORCH,
                graph);
    }

    private static GraphOrchestrationProperties evalGraphProperties() {
        GraphOrchestrationProperties p = new GraphOrchestrationProperties();
        p.setReactEnabled(false);
        p.getApproval().setEnabled(false);
        return p;
    }

    private static ToolCatalog emptyCatalog() {
        return List::of;
    }

    private static KnowledgeRetriever ragOn() {
        return q -> List.of(KB);
    }

    private static ToolExecutor toolsOn() {
        return m -> TOOL_JSON;
    }
}
