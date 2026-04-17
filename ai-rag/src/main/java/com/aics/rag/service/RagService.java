package com.aics.rag.service;

import com.aics.prompt.builder.BuiltPrompt;
import com.aics.prompt.factory.PromptFactory;
import com.aics.prompt.factory.PromptScenario;
import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.llm.RagLlmClient;
import com.aics.rag.prompt.PromptAssembly;
import com.aics.rag.retriever.SemanticRetriever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG 编排：检索 → {@link PromptScenario#RAG} 模板 → {@link RagLlmClient}。
 * <p>
 * 需注册 {@link RagLlmClient} Bean；与 ai-core 解耦，仅依赖本接口。
 */
@Service
@ConditionalOnBean(RagLlmClient.class)
public class RagService {

    private final SemanticRetriever semanticRetriever;
    private final PromptFactory promptFactory;
    private final RagLlmClient ragLlmClient;

    public RagService(SemanticRetriever semanticRetriever,
                      PromptFactory promptFactory,
                      RagLlmClient ragLlmClient) {
        this.semanticRetriever = semanticRetriever;
        this.promptFactory = promptFactory;
        this.ragLlmClient = ragLlmClient;
    }

    /**
     * 基于知识库检索回答用户问题。
     */
    public String answer(String question) {
        List<ScoredMatch> matches = semanticRetriever.retrieve(question);
        String contextBlock = formatContext(matches);
        BuiltPrompt built = promptFactory.forScenario(PromptScenario.RAG)
                .variable("question", question == null ? "" : question)
                .context(contextBlock)
                .build();
        return ragLlmClient.complete(PromptAssembly.toSingleString(built));
    }

    private static String formatContext(List<ScoredMatch> matches) {
        if (matches.isEmpty()) {
            return "（暂无检索到相关片段）";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            ScoredMatch m = matches.get(i);
            sb.append("[").append(i + 1).append("] ").append(m.text().trim()).append('\n');
        }
        return sb.toString().trim();
    }
}
