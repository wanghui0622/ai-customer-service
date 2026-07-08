---
title: "ChatMemory — 未用原因与混合演进"
series: "LangChain4j 实战"
series_index: 5
tags: [LangChain4j, ChatMemory, Redis, Session]
summary: "对比 LangChain4j ChatMemory 与项目 DefaultChatMemory：格式化字符串、字符截断、UserProfile 与 Redis 存储；附多轮 session curl 验证、截断演示及 ChatMemoryStore 桥接混合方案。"
estimated_reading_minutes: 18
---

# 第 5 篇：ChatMemory — 未用原因与混合演进

> ai-customer-service 不是 LangChain4j 全家桶 Demo，而是 **「LC4J 作 Model Layer + Spring 自研编排」** 的可运行骨架。

**上一篇**：[第 4 篇](./04-embedding-rag.md) | **下一篇**：[第 6 篇：Tools](./06-tools.md)

---

## 写在前面

多轮对话离不开 Memory。LangChain4j 提供 `MessageWindowChatMemory`；本项目用 **自研 `ChatMemory` SPI**，输出 **可直接进 Prompt 的格式化字符串**。本篇说明为何这样设计，以及如何与 LC4J 混合演进。

---

## 你将学到什么

- LC4J `ChatMemory` / `ChatMemoryStore` 用法
- `DefaultChatMemory` + `MemoryFormatter` + `MemoryStore`
- 多轮 session 实操与 prompt 中 `### 历史对话`
- `maxHistoryChars` 截断
- Redis 配置与 `ChatMemoryStore` 桥接

---

## 1. LangChain4j ChatMemory

```java
MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
    .id(sessionId)
    .maxMessages(20)
    .chatMemoryStore(redisStore)
    .build();
memory.add(UserMessage.from("你好"));
memory.add(AiMessage.from("您好，有什么可以帮您？"));
List<ChatMessage> messages = memory.messages();
```

配合 AiServices：`@MemoryId String sessionId`。

---

## 2. 项目替代方案

### 2.1 SPI

[`ChatMemory.java`](../../ai-common/src/main/java/com/aics/spi/ChatMemory.java)：`loadHistory()` 返回 **String**，另含 `UserProfile`。

### 2.2 DefaultChatMemory

[`DefaultChatMemory.java`](../../ai-memory/src/main/java/com/aics/memory/DefaultChatMemory.java) → `MemoryFormatter.formatSessionHistory`。

### 2.3 截断

[`MemoryFormatter.trimToMax`](../../ai-memory/src/main/java/com/aics/memory/format/MemoryFormatter.java)：

```text
...[历史已截断]
（保留尾部 maxHistoryChars 字符）
```

配置：[`MemoryProperties`](../../ai-memory/src/main/java/com/aics/memory/config/MemoryProperties.java) `maxHistoryChars: 8000`。

### 2.4 Redis

```yaml
aics:
  memory:
    store: redis   # 需 spring-boot-starter-data-redis
    redis-key-prefix: "aics:mem:"
    session-ttl-seconds: 604800
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

![图1：第二轮 prompt 含历史对话](./assets/05-chat-memory/fig-01-multi-turn-prompt.png)

![图2：历史截断提示](./assets/05-chat-memory/fig-02-history-truncation.png)

![图3：管理端 Memory 会话列表](./assets/05-chat-memory/fig-03-memory-admin.png)

---

## 3. 是否应该用 LC4J ChatMemory？

| 需求 | LC4J | 项目自研 |
|------|------|----------|
| 直出 Prompt 字符串 | 需转换 | **原生** |
| 按字符截断 | 按条数 | **支持** |
| UserProfile | 无 | **有** |
| 演进测试 | mock 复杂 | **简单** |

**结论**：当前 **不建议全量替换**；可选 Redis 桥接 `ChatMemoryStore`。

### 混合桥接示例

```java
public class RedisChatMemoryStore implements ChatMemoryStore {
    private final MemoryStore legacy;
    // getMessages / updateMessages 与 MessageTurn 互转
}
// 编排层仍用 DefaultChatMemory.loadHistory() → String
```

---

## 动手验证

### 多轮同一 sessionId

```bash
# 第一轮
curl -s -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"mem-demo","message":"我叫小明，刚付了订单123的款"}' | jq .answer

# 第二轮
curl -s -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"mem-demo","message":"我刚才说我叫什么？"}' | jq .prompt
```

```text
# 预期 prompt 含：
### 历史对话
用户: 我叫小明，刚付了订单123的款
AI: （上一轮答复）
...
### 用户问题
我刚才说我叫什么？
```

### 截断演示（开发配置）

```yaml
aics:
  memory:
    max-history-chars: 200
```

长会话后 prompt 中出现 `...[历史已截断]`。

### 演进测试 MEMORY 档位

```bash
mvn -pl ai-service test -Dtest=AiServiceEvolutionTest#testFullAiService -DskipTests=false -Dmaven.test.skip=false
```

```text
# 断言 lastPrompt 含 ### 历史对话 与 拣货中
```

---

## FAQ

**Q：LC4J Memory 完全不能用吗？**  
A：实验性 Agent 分支可用；生产客服保留 Formatter 更稳。

**Q：sessionId 谁生成？**  
A：前端 [`chatSession.ts`](../../../ai-customer-front/src/lib/chatSession.ts) 或客户端 UUID。

---

## 本篇小结

> **Memory 是存储 + 格式化 + 画像；项目自研更贴合 Prompt 与测试；Redis 桥接是可选演进。**

---

## 系列导航

[第 4 篇](./04-embedding-rag.md) | [第 6 篇](./06-tools.md) | [README](./README.md)
