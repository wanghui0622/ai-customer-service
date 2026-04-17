/**
 * AI 客服记忆模块：短期会话（按 sessionId）与长期用户画像（按 userId），
 * 仅负责数据存取与格式化为 Prompt 文本，不调用 LLM、不依赖 ai-core / ai-prompt。
 * <p>
 * 入口：{@link com.aics.spi.ChatMemory} 由 {@link com.aics.memory.DefaultChatMemory} 实现；
 * 存储抽象为 {@link com.aics.memory.store.MemoryStore}，默认进程内实现
 * {@link com.aics.memory.store.InMemoryMemoryStore}。
 */
package com.aics.memory;
