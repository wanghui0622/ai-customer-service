package com.aics.service.evolution;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.FixedAgentRouter;
import com.aics.graph.CustomerServiceGraph;
import com.aics.graph.config.GraphOrchestrationProperties;
import com.aics.graph.context.OrchestrationContext;
import com.aics.service.chat.LinearChatPipeline;
import com.aics.service.chat.dto.ChatTurnTraceResult;
import com.aics.service.config.OrchestrationProperties;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.LlmClient;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolCatalog;
import com.aics.spi.ToolExecutor;
import com.aics.prompt.composer.DefaultPromptComposer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphOrchestrationEquivalenceTest {

    private static final String SESSION = "graph-equiv";
    private static final String MESSAGE = "我的订单123为什么还没有发货？";

    private static final String KB =
            "订单支付成功后，仓库通常在1-3个工作日内发货；大促期间可能延长。";
    private static final String TOOL_JSON =
            "{\"orderId\":\"123\",\"status\":\"拣货中\"}";

    @Test
    void linearAndGraphProduceEquivalentTrace() throws Exception {
        RecordingLlmClient llm = new RecordingLlmClient();
        KnowledgeRetriever rag = q -> List.of(KB);
        ToolExecutor tools = m -> TOOL_JSON;
        OrchestrationProperties orch = orchestration();
        GraphOrchestrationProperties graphProps = graphProperties(false);

        LinearChatPipeline linear = new LinearChatPipeline(
                new EmptyChatMemory(),
                rag,
                tools,
                new DefaultPromptComposer(),
                llm,
                new FixedAgentRouter(new AgentDecision(true, true, "order_query", "test")),
                orch
        );

        CustomerServiceGraph graph = new CustomerServiceGraph(
                new EmptyChatMemory(),
                rag,
                tools,
                new DefaultPromptComposer(),
                llm,
                new FixedAgentRouter(new AgentDecision(true, true, "order_query", "test")),
                emptyCatalog(),
                graphProps
        );

        ChatTurnTraceResult linearResult = linear.chatWithTrace(SESSION, MESSAGE);
        ChatTurnTraceResult graphResult = toTrace(graph.invoke(
                SESSION, MESSAGE, OrchestrationContext.defaults()));

        assertThat(graphResult.answer()).isEqualTo(linearResult.answer());
        assertThat(graphResult.ragUsed()).isEqualTo(linearResult.ragUsed());
        assertThat(graphResult.toolsUsed()).isEqualTo(linearResult.toolsUsed());
        assertThat(graphResult.toolResult()).isEqualTo(linearResult.toolResult());
        assertThat(graphResult.prompt()).isEqualTo(linearResult.prompt());
        assertThat(graphResult.ragContext()).isEqualTo(linearResult.ragContext());
    }

    private static ChatTurnTraceResult toTrace(com.aics.graph.state.ChatGraphState state) {
        return new ChatTurnTraceResult(
                state.answer(),
                state.routerDecision(),
                state.ragUsed(),
                state.toolsUsed(),
                List.copyOf(state.ragContext()),
                state.toolResult(),
                state.prompt(),
                List.copyOf(state.toolCalls()),
                List.copyOf(state.executedNodes()),
                state.graphExecutionId(),
                state.durationMs(),
                state.pendingApproval(),
                state.approvalToken()
        );
    }

    private static OrchestrationProperties orchestration() {
        OrchestrationProperties p = new OrchestrationProperties();
        p.setRagEnabled(true);
        p.setToolsEnabled(true);
        p.setAgentRouterLlmEnabled(false);
        return p;
    }

    private static GraphOrchestrationProperties graphProperties(boolean react) {
        GraphOrchestrationProperties p = new GraphOrchestrationProperties();
        p.setReactEnabled(react);
        p.getApproval().setEnabled(false);
        return p;
    }

    private static ToolCatalog emptyCatalog() {
        return List::of;
    }

    private static final class EmptyChatMemory implements ChatMemory {
        @Override
        public String loadHistory(String sessionId) {
            return "";
        }

        @Override
        public void saveMessage(String sessionId, String userMsg, String aiMsg) {
        }

        @Override
        public com.aics.spi.UserProfile loadUserProfile(String userId) {
            return com.aics.spi.UserProfile.empty(userId);
        }

        @Override
        public void saveUserProfile(String userId, com.aics.spi.UserProfile profile) {
        }
    }

    private static final class RecordingLlmClient implements LlmClient {
        @Override
        public String chat(String prompt) {
            return EvolutionLlmFixtures.fakeAnswer(prompt);
        }
    }
}
