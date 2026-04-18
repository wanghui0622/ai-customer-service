package com.aics.service.evolution;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.FixedAgentRouter;
import com.aics.service.chat.AiChatService;
import com.aics.service.config.OrchestrationProperties;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.LlmClient;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolExecutor;
import com.aics.spi.UserProfile;
import com.aics.prompt.composer.DefaultPromptComposer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AiServiceEvolutionTest {

    private static final Logger log = LoggerFactory.getLogger(AiServiceEvolutionTest.class);

    private static final String QUESTION = "我的订单123为什么还没有发货？";
    private static final String SESSION = "evolution-demo";

    private static final String KB_ORDER_SHIP =
            "订单支付成功后，仓库通常在1-3个工作日内发货；大促期间可能延长。订单进入「拣货中」表示已排产，请耐心等待物流更新。";

    private static final String TOOL_ORDER_JSON =
            "{\"orderId\":\"123\",\"status\":\"拣货中\",\"warehouse\":\"华东仓\",\"payTime\":\"2026-04-17T10:00:00Z\",\"note\":\"预计12小时内交接快递\"}";

    private static final OrchestrationProperties TEST_ORCH = testOrchestration();

    private static OrchestrationProperties testOrchestration() {
        OrchestrationProperties p = new OrchestrationProperties();
        p.setRagEnabled(true);
        p.setToolsEnabled(true);
        return p;
    }

    @Test
    @Order(1)
    void testBaseLLM() {
        RecordingLlmClient llm = new RecordingLlmClient();
        AiChatService svc = new AiChatService(
                new EmptyChatMemory(),
                q -> List.of(),
                m -> "",
                new PassthroughPromptComposer(),
                llm,
                new FixedAgentRouter(AgentDecision.none()),
                TEST_ORCH
        );
        String answer = svc.chat(SESSION, QUESTION);
        printStage("BASE", answer);
        assertThat(answer).contains("纯模型");
        assertThat(llm.lastPrompt).doesNotContain("参考知识");
    }

    @Test
    @Order(2)
    void testPromptEnhanced() {
        RecordingLlmClient llm = new RecordingLlmClient();
        AiChatService svc = new AiChatService(
                new EmptyChatMemory(),
                q -> List.of(),
                m -> "",
                new DefaultPromptComposer(),
                llm,
                new FixedAgentRouter(AgentDecision.none()),
                TEST_ORCH
        );
        String answer = svc.chat(SESSION, QUESTION);
        printStage("PROMPT", answer);
        assertThat(answer).contains("Prompt版");
        assertThat(llm.lastPrompt).contains("你是专业 AI 客服助手");
        assertThat(llm.lastPrompt).contains("### 用户问题");
    }

    @Test
    @Order(3)
    void testRagEnhanced() {
        RecordingLlmClient llm = new RecordingLlmClient();
        KnowledgeRetriever rag = q -> List.of(KB_ORDER_SHIP);
        AiChatService svc = new AiChatService(
                new EmptyChatMemory(),
                rag,
                m -> "",
                new DefaultPromptComposer(),
                llm,
                new FixedAgentRouter(new AgentDecision(true, false, "", "test-rag")),
                TEST_ORCH
        );
        String answer = svc.chat(SESSION, QUESTION);
        printStage("RAG", answer);
        assertThat(answer).contains("RAG");
        assertThat(llm.lastPrompt).contains("### 参考知识");
        assertThat(llm.lastPrompt).contains("大促");
    }

    @Test
    @Order(4)
    void testFullAiService() {
        RecordingLlmClient llm = new RecordingLlmClient();
        ChatMemory memory = new SeededChatMemory(
                "用户: 我昨天付了款\nAI: 已确认收款，仓库处理中。\n"
        );
        KnowledgeRetriever rag = q -> List.of(KB_ORDER_SHIP);
        ToolExecutor tools = m -> TOOL_ORDER_JSON;
        AiChatService svc = new AiChatService(
                memory,
                rag,
                tools,
                new DefaultPromptComposer(),
                llm,
                new FixedAgentRouter(new AgentDecision(true, true, "", "test-full")),
                TEST_ORCH
        );
        String answer = svc.chat(SESSION, QUESTION);
        printStage("FULL", answer);
        assertThat(answer).contains("全能力");
        assertThat(llm.lastPrompt).contains("### 历史对话");
        assertThat(llm.lastPrompt).contains("### 参考知识");
        assertThat(llm.lastPrompt).contains("### 工具结果");
        assertThat(llm.lastPrompt).contains("拣货中");
    }

    private static void printStage(String title, String answer) {
        String line = "=".repeat(12);
        log.info("\n{} {} {}\n{}\n", line, title, line, answer);
        System.out.printf("%n%s %s %s%n%s%n", line, title, line, answer);
    }

    /** 仅把用户当前句交给模型，模拟「无模板、无检索」的裸调。 */
    private static final class PassthroughPromptComposer implements PromptComposer {
        @Override
        public String build(String history, List<String> context, String toolResult, String message) {
            return message == null ? "" : message;
        }
    }

    private static final class EmptyChatMemory implements ChatMemory {
        @Override
        public String loadHistory(String sessionId) {
            return "";
        }

        @Override
        public void saveMessage(String sessionId, String userMsg, String aiMsg) {
            // no-op
        }

        @Override
        public UserProfile loadUserProfile(String userId) {
            return UserProfile.empty(userId);
        }

        @Override
        public void saveUserProfile(String userId, UserProfile profile) {
            // no-op
        }
    }

    private static final class SeededChatMemory implements ChatMemory {

        private final String seed;

        SeededChatMemory(String seed) {
            this.seed = seed;
        }

        @Override
        public String loadHistory(String sessionId) {
            return seed;
        }

        @Override
        public void saveMessage(String sessionId, String userMsg, String aiMsg) {
            // no-op
        }

        @Override
        public UserProfile loadUserProfile(String userId) {
            return UserProfile.empty(userId);
        }

        @Override
        public void saveUserProfile(String userId, UserProfile profile) {
            // no-op
        }
    }

    /**
     * 根据 prompt 中是否包含模板块，返回可读的「模拟模型答复」，便于博客对比阶段差异。
     */
    private static final class RecordingLlmClient implements LlmClient {

        String lastPrompt;

        @Override
        public String chat(String prompt) {
            this.lastPrompt = prompt;
            return fakeAnswer(prompt);
        }

        static String fakeAnswer(String prompt) {
            boolean sys = prompt.contains("你是专业 AI 客服助手");
            boolean rag = prompt.contains("### 参考知识");
            boolean tool = prompt.contains("### 工具结果");
            boolean mem = prompt.contains("### 历史对话");
            if (sys && rag && tool && mem) {
                return "【模拟答复-全能力】结合历史、知识库与订单工具：订单123当前「拣货中」，华东仓预计12小时内交快递；昨日已确认收款，与知识库发货时效一致。";
            }
            if (sys && rag && !tool) {
                return "【模拟答复-RAG】依据参考知识：未发货常见于大促排期；您的订单123应已进入仓库流程，请留意物流更新。";
            }
            if (sys && !rag) {
                return "【模拟答复-Prompt版】已按客服角色与段落结构组织问题；建议在后台核对订单与支付状态后再答复用户。";
            }
            return "【模拟答复-纯模型】仓库可能比较忙吧，一般等等就会发（未使用业务知识与订单数据，易泛化）。";
        }
    }
}
