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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 线性七步管道（legacy），与图编排行为等价，便于回归对比。
 */
public class LinearChatPipeline {

    private final ChatMemory memory;
    private final KnowledgeRetriever rag;
    private final ToolExecutor tools;
    private final PromptComposer promptComposer;
    private final LlmClient llm;
    private final AgentRouter agentRouter;
    private final OrchestrationProperties orchestrationProperties;

    public LinearChatPipeline(ChatMemory memory,
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
        return ChatTurnTraceResult.of(
                answer,
                decision,
                useRag,
                useTools,
                List.copyOf(context),
                toolResult,
                prompt
        );
    }

    public String buildPromptOnly(String sessionId, String message) {
        String history = memory.loadHistory(sessionId);
        AgentDecision decision = agentRouter.route(message, history);
        boolean useRag = orchestrationProperties.isRagEnabled() && decision.useRag();
        List<String> context = useRag ? rag.retrieve(message) : Collections.emptyList();
        boolean useTools = orchestrationProperties.isToolsEnabled() && decision.useTools();
        String toolResult = useTools
                ? tools.executeNamed(decision.toolName(), message)
                : "";
        return promptComposer.build(history, context, toolResult, message);
    }

    public void saveAnswer(String sessionId, String message, String answer) {
        memory.saveMessage(sessionId, message, answer);
    }
}
