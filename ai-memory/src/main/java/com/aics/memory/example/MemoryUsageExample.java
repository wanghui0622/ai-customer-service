package com.aics.memory.example;

import com.aics.memory.config.MemoryProperties;
import com.aics.memory.format.MemoryFormatter;
import com.aics.memory.model.MessageTurn;
import com.aics.memory.store.InMemoryMemoryStore;
import com.aics.memory.store.MemoryStore;
import com.aics.spi.UserProfile;

import java.util.List;
import java.util.Map;

/**
 * 非 Spring 场景下的最小串联示例：手动 new {@link InMemoryMemoryStore} 与 {@link MemoryFormatter}，
 * 演示「写入轮次 + 画像 → 合并格式化」。生产环境请使用 Spring 注入 {@link com.aics.spi.ChatMemory}。
 */
public final class MemoryUsageExample {

    private MemoryUsageExample() {
    }

    /**
     * @return 合并用户画像与会话历史后的 Prompt 片段（仅演示，非 Bean）
     */
    public static String demoManualCompose() {
        MemoryStore store = new InMemoryMemoryStore();
        MemoryProperties props = new MemoryProperties();
        props.setMaxHistoryChars(4000);
        MemoryFormatter formatter = new MemoryFormatter(props);

        store.appendTurn("s1", new MessageTurn("你好", "您好，有什么可以帮您？"));
        store.appendTurn("s1", new MessageTurn("查订单", "请提供订单号。"));

        List<MessageTurn> turns = store.listTurns("s1");
        store.saveProfile("u10086", new UserProfile("u10086", Map.of("tier", "黄金会员")));

        UserProfile profile = store.loadProfile("u10086");
        return formatter.combineProfileAndHistory(profile, turns);
    }
}
