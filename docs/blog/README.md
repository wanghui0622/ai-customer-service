# LangChain4j 实战博客系列（发布版长文）

> ai-customer-service 不是 LangChain4j 全家桶 Demo，而是 **「LC4J 作 Model Layer + Spring 自研编排」** 的可运行骨架。

本目录为 **对外发布用完整长文**（通用 Markdown，保留 mermaid）。每篇含：导读、环境准备、动手验证（bash + 预期输出）、截图占位、FAQ、系列导航。

- **发布规范**：[PUBLISHING.md](./PUBLISHING.md)
- **截图清单**：[assets/README.md](./assets/README.md)（png 可按清单二期补拍）

## 系列目录

| 序号 | 标题 | 文件 | 阅读时间 | 截图数 |
|------|------|------|:--------:|:------:|
| 1 | ai-customer-service 系统架构与设计理念 | [01-system-architecture.md](./01-system-architecture.md) | ~22 min | 4 |
| 2 | LangChain4j 能力全景 × 项目使用矩阵 | [02-langchain4j-capability-matrix.md](./02-langchain4j-capability-matrix.md) | ~24 min | 3 |
| 3 | ChatModel：已用能力与改进空间 | [03-chatmodel.md](./03-chatmodel.md) | ~20 min | 3 |
| 4 | EmbeddingModel + RAG：半用半自建 | [04-embedding-rag.md](./04-embedding-rag.md) | ~22 min | 4 |
| 5 | ChatMemory：未用原因与混合演进 | [05-chat-memory.md](./05-chat-memory.md) | ~18 min | 3 |
| 6 | Tools：Router 分离与 @Tool 分阶段演进 | [06-tools.md](./06-tools.md) | ~22 min | 4 |
| 7 | AiServices vs AiChatService：编排选型 | [07-aiservices.md](./07-aiservices.md) | ~18 min | 2 |
| 8 | Streaming、观测与生产演进路线图 | [08-streaming-production.md](./08-streaming-production.md) | ~24 min | 3 |
| 9 | LangGraph4j 落地（一）— 图编排骨架与等价迁移 | [09-langgraph4j-phase1-graph.md](./09-langgraph4j-phase1-graph.md) | ~26 min | 0 |
| 10 | LangGraph4j 落地（二）— ReAct 多步工具与 trace | [10-langgraph4j-phase2-react.md](./10-langgraph4j-phase2-react.md) | ~24 min | 0 |
| 11 | LangGraph4j 落地（三）— 可扩展外部系统集成层 | [11-langgraph4j-phase3-integrations.md](./11-langgraph4j-phase3-integrations.md) | ~28 min | 0 |
| 12 | LangGraph4j 落地（四）— HITL、子图与真流式 | [12-langgraph4j-phase4-advanced.md](./12-langgraph4j-phase4-advanced.md) | ~30 min | 0 |

**合计预估阅读时间**：约 4–4.5 小时（可按篇跳读）

## 摘要（各篇一句话）

1. **架构篇**：多模块 + `AiChatService` 编排 + SPI，LangChain4j 仅作 Model Layer 预告。  
2. **矩阵篇**：LC4J 六层能力 vs 项目逐项对照，四维选型表。  
3. **ChatModel篇**：`OpenAiLlmClient` 分层、双 LLM 调用、生产改进。  
4. **RAG篇**：嵌入用 LC4J，VectorStore 自研，8080 入库 + 8081 检索验证。  
5. **Memory篇**：自研 Memory 理由，多轮 session 与 Redis 桥接。  
6. **Tools篇**：Router 与执行分离，三工具 curl，@Tool 演进。  
7. **AiServices篇**：显式管道 vs 声明式 Agent 选型决策树。  
8. **生产篇**：SSE、观测、checklist、演进路线。  
9. **LangGraph Phase1**：`ai-graph`、双引擎、`ChatGraphState` 等价迁移。  
10. **LangGraph Phase2**：ReAct 循环、`toolCalls` trace。  
11. **LangGraph Phase3**：`ai-integrations` Connector SPI、工单/订单。  
12. **LangGraph Phase4**：HITL、子图、真流式、GraphTrace 面板。

## 阅读顺序

1. **第 1–2 篇** — 建立全局认知  
2. **第 3–8 篇** — 按兴趣跳读（每篇独立）  
3. **上线前** — 第 8 篇 + 第 3 篇改进项  
4. **LangGraph4j 四阶段落地** — 第 9–12 篇（建议已读完第 1、6、7 篇后阅读）

## 快速动手（系列共用）

```bash
cd ai-customer-service
mvn -pl ai-reactive-chat spring-boot:run
# 另开终端
curl -s -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"readme-demo","message":"你好"}' | jq .
```

开发环境请保持 `aics.observability.expose-prompt-trace: true`（见 `ai-reactive-chat` 的 `application.yml`）。

## 相关资源

- [系统架构说明](../SYSTEM_ARCHITECTURE.md)
- [项目 README](../../README.md)
- 前端联调：[ai-customer-front/docs/BACKEND_HANDOFF.md](../../../ai-customer-front/docs/BACKEND_HANDOFF.md)
