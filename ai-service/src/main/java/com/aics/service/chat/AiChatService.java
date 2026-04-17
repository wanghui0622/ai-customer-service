package com.aics.service.chat;

import com.aics.service.orchestration.policy.RagEligibilityPolicy;
import com.aics.service.orchestration.policy.ToolEligibilityPolicy;
import com.aics.spi.ChatMemory;
import com.aics.spi.KnowledgeRetriever;
import com.aics.spi.LlmClient;
import com.aics.spi.PromptComposer;
import com.aics.spi.ToolExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 系统唯一编排入口：按策略聚合 memory / RAG / tools，经 prompt 构建后调用 LLM，不写具体领域实现。
 * <p>
 * 流程：load memory →（可选）RAG →（可选）tools → build prompt → LLM → save memory。<br>
 * 策略可替换以实现 Agent、多步工具链等扩展。
 */
@Service
public class AiChatService {

    private final ChatMemory memory;
    private final KnowledgeRetriever rag;
    private final ToolExecutor tools;
    private final PromptComposer promptComposer;
    private final LlmClient llm;
    private final RagEligibilityPolicy ragPolicy;
    private final ToolEligibilityPolicy toolPolicy;

    public AiChatService(ChatMemory memory,
                         KnowledgeRetriever rag,
                         ToolExecutor tools,
                         PromptComposer promptComposer,
                         LlmClient llm,
                         RagEligibilityPolicy ragPolicy,
                         ToolEligibilityPolicy toolPolicy) {
        this.memory = Objects.requireNonNull(memory);
        this.rag = Objects.requireNonNull(rag);
        this.tools = Objects.requireNonNull(tools);
        this.promptComposer = Objects.requireNonNull(promptComposer);
        this.llm = Objects.requireNonNull(llm);
        this.ragPolicy = Objects.requireNonNull(ragPolicy);
        this.toolPolicy = Objects.requireNonNull(toolPolicy);
    }

    /**
     * 完整对话编排：聚合上下文 → 生成 prompt → 调用模型 → 持久化本轮。
     *
     * @param sessionId 会话 ID
     * @param message   用户当前输入
     * @return 模型回复文本
     */
    public String chat(String sessionId, String message) {
        String history = memory.loadHistory(sessionId);

        List<String> context = ragPolicy.shouldRetrieveKnowledge(message)
                ? rag.retrieve(message)
                : Collections.emptyList();

        String toolResult = toolPolicy.shouldExecuteTools(message)
                ? tools.execute(message)
                : "";

        String prompt = promptComposer.build(history, context, toolResult, message);
        String answer = llm.chat(prompt);
        memory.saveMessage(sessionId, message, answer);
        return answer;
    }
}
