package com.aics.core.chat;

import com.aics.core.llm.LlmProvider;
import com.aics.core.memory.MemoryStore;
import com.aics.prompt.builder.BuiltPrompt;
import com.aics.prompt.factory.PromptFactory;
import com.aics.prompt.factory.PromptScenario;
import org.springframework.stereotype.Service;

/**
 * 会话聊天编排：读取记忆、通过 {@link PromptFactory} 组装提示词、调用 LLM、写回记忆。
 */
@Service
public class ChatService {

    private final LlmProvider llmProvider;
    private final MemoryStore memoryStore;
    private final PromptFactory promptFactory;

    /**
     * @param llmProvider    模型调用入口
     * @param memoryStore    会话记忆存储
     * @param promptFactory  提示词模板工厂（{@code ai-prompt}）
     */
    public ChatService(LlmProvider llmProvider,
                       MemoryStore memoryStore,
                       PromptFactory promptFactory) {
        this.llmProvider = llmProvider;
        this.memoryStore = memoryStore;
        this.promptFactory = promptFactory;
    }

    /**
     * 在指定会话下处理用户消息并返回模型回复。
     *
     * @param sessionId 会话 ID
     * @param message   用户当前输入
     * @return 模型回复文本
     */
    public String chat(String sessionId, String message) {
        String history = memoryStore.load(sessionId);
        BuiltPrompt built = promptFactory.forScenario(PromptScenario.CHAT)
                .variable("userName", "访客")
                .variable("category", "通用")
                .variable("question", message)
                .history(history)
                .build();
        String answer = llmProvider.chat(toSingleTurnPrompt(built));
        memoryStore.save(sessionId, message, answer);
        return answer;
    }

    /**
     * 将 {@link BuiltPrompt} 拼成当前 {@link LlmProvider#chat(String)} 使用的单条文本
     *（system 与 user/assistant 块顺序拼接）。
     */
    private static String toSingleTurnPrompt(BuiltPrompt built) {
        String system = built.system();
        String body = built.combinedUserBlock();
        if (system.isEmpty()) {
            return body;
        }
        if (body.isEmpty()) {
            return system;
        }
        return system + "\n\n" + body;
    }
}
