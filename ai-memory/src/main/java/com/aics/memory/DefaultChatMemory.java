package com.aics.memory;

import com.aics.memory.format.MemoryFormatter;
import com.aics.memory.model.MessageTurn;
import com.aics.memory.store.MemoryStore;
import com.aics.spi.ChatMemory;
import com.aics.spi.UserProfile;
import org.springframework.stereotype.Service;

/**
 * {@link ChatMemory} 的默认实现：读写给 {@link MemoryStore}，会话历史展示格式由 {@link MemoryFormatter} 统一处理
 * （含长度截断，见 {@link com.aics.memory.config.MemoryProperties#getMaxHistoryChars()}）。
 */
@Service
public class DefaultChatMemory implements ChatMemory {

    private final MemoryStore memoryStore;
    private final MemoryFormatter memoryFormatter;

    /**
     * @param memoryStore     底层存储（in-memory 或 redis 等实现）
     * @param memoryFormatter 将轮次列表格式化为 Prompt 可用文本
     */
    public DefaultChatMemory(MemoryStore memoryStore, MemoryFormatter memoryFormatter) {
        this.memoryStore = memoryStore;
        this.memoryFormatter = memoryFormatter;
    }

    /**
     * 返回当前会话已格式化的历史文本，可直接作为编排层 {@code promptComposer.build(history, ...)} 的 history 参数。
     */
    @Override
    public String loadHistory(String sessionId) {
        return memoryFormatter.formatSessionHistory(memoryStore.listTurns(sessionId));
    }

    /**
     * 持久化本轮用户消息与模型回复，追加到会话末尾。
     */
    @Override
    public void saveMessage(String sessionId, String userMsg, String aiMsg) {
        memoryStore.appendTurn(sessionId, new MessageTurn(userMsg, aiMsg));
    }

    @Override
    public UserProfile loadUserProfile(String userId) {
        return memoryStore.loadProfile(userId);
    }

    @Override
    public void saveUserProfile(String userId, UserProfile profile) {
        memoryStore.saveProfile(userId, profile);
    }
}
