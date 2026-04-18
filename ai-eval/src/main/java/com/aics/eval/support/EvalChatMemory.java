package com.aics.eval.support;

import com.aics.spi.ChatMemory;
import com.aics.spi.UserProfile;

public final class EvalChatMemory implements ChatMemory {

    private final String historyBlock;

    public EvalChatMemory(String historyBlock) {
        this.historyBlock = historyBlock == null ? "" : historyBlock;
    }

    @Override
    public String loadHistory(String sessionId) {
        return historyBlock;
    }

    @Override
    public void saveMessage(String sessionId, String userMsg, String aiMsg) {
    }

    @Override
    public UserProfile loadUserProfile(String userId) {
        return UserProfile.empty(userId);
    }

    @Override
    public void saveUserProfile(String userId, UserProfile profile) {
    }
}
