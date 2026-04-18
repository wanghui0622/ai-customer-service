package com.aics.service.chat;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.AgentRouter;
import com.aics.service.chat.dto.ChatTurnTraceResult;
import com.aics.service.config.OrchestrationProperties;
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
 * 系统唯一编排入口：Agent 路由决策 → 聚合 memory / RAG / tools，经 prompt 构建后调用 LLM，不写具体领域实现。
 * <p>
 * 流程：load memory → {@link AgentRouter} →（可选）RAG →（可选）tools → build prompt → LLM → save memory。<br>
 * 路由可由 LLM（JSON）或规则实现，见 {@code aics.orchestration.agent-router-llm-enabled}。
 */
@Service
public class AiChatService {

    private final ChatMemory memory;
    private final KnowledgeRetriever rag;
    private final ToolExecutor tools;
    private final PromptComposer promptComposer;
    private final LlmClient llm;
    private final AgentRouter agentRouter;
    private final OrchestrationProperties orchestrationProperties;

    public AiChatService(ChatMemory memory,
                         KnowledgeRetriever rag,
                         ToolExecutor tools,
                         PromptComposer promptComposer,
                         LlmClient llm,
                         AgentRouter agentRouter,
                         OrchestrationProperties orchestrationProperties) {
        this.memory = Objects.requireNonNull(memory);
        this.rag = Objects.requireNonNull(rag);
        this.tools = Objects.requireNonNull(tools);
        this.promptComposer = Objects.requireNonNull(promptComposer);
        this.llm = Objects.requireNonNull(llm);
        this.agentRouter = Objects.requireNonNull(agentRouter);
        this.orchestrationProperties = Objects.requireNonNull(orchestrationProperties);
    }

    /**
     * 完整对话编排：聚合上下文 → 生成 prompt → 调用模型 → 持久化本轮。
     *
     * @param sessionId 会话 ID
     * @param message   用户当前输入
     * @return 模型回复文本
     */
    public String chat(String sessionId, String message) {
        return chatWithTrace(sessionId, message).answer();
    }

    /**
     * 与 {@link #chat(String, String)} 相同管道，额外返回 Agent / RAG / Tool / Prompt 快照。
     */
    public ChatTurnTraceResult chatWithTrace(String sessionId, String message) {
        String history = memory.loadHistory(sessionId);

        AgentDecision decision = agentRouter.route(message, history);

        boolean useRag = orchestrationProperties.isRagEnabled() && decision.useRag();
        List<String> context = useRag ? rag.retrieve(message) : Collections.emptyList();

        boolean useTools = orchestrationProperties.isToolsEnabled() && decision.useTools();
        String toolResult = useTools
                ? tools.executeNamed(decision.toolName(), message)
                : "";

        String prompt = promptComposer.build(history, context, toolResult, message);
        String answer = llm.chat(prompt);
        memory.saveMessage(sessionId, message, answer);
        return new ChatTurnTraceResult(
                answer,
                decision,
                useRag,
                useTools,
                List.copyOf(context),
                toolResult,
                prompt
        );
    }
}
