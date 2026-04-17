package com.aics.memory.store;

import com.aics.memory.model.MessageTurn;
import com.aics.spi.UserProfile;

import java.util.List;

/**
 * 记忆存储抽象：短期（多轮对话）与长期（用户画像）分层，不包含格式化与 Prompt 决策。
 */
public interface MemoryStore {

    /**
     * @param sessionId 会话标识，与业务侧 session 一致
     * @return 该会话下已存储的对话轮次，按追加顺序；无则空列表
     */
    List<MessageTurn> listTurns(String sessionId);

    /**
     * 在会话末尾追加一轮对话。
     */
    void appendTurn(String sessionId, MessageTurn turn);

    /**
     * 清空某会话的短期记忆（画像不受影响）。
     */
    void clearSession(String sessionId);

    /**
     * @param userId 业务用户 ID
     * @return 已存画像；从未保存过时由实现决定（如返回 {@link UserProfile#empty(String)}）
     */
    UserProfile loadProfile(String userId);

    /**
     * 覆盖写入用户画像（全量替换语义由实现定义，当前内存实现为 put 覆盖）。
     */
    void saveProfile(String userId, UserProfile profile);
}
