# 博客系列截图清单

发布前按本清单补拍 png，放入对应子目录。文件名与文章中 `![...](...)` 路径一致。

## 01-architecture（第 1 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-dual-app-health.png` | 双服务健康 | 分别启动 admin:8080、chat:8081，访问 `/actuator/health` |
| `fig-02-chat-page.png` | 前端聊天主界面 | `ai-customer-front` 开发模式打开 Chat 页 |
| `fig-03-agent-debug-panels.png` | 四调试面板 | 发送订单问题，展开 Agent/RAG/Tool/Prompt |
| `fig-04-maven-modules.png` | 多模块结构 | IDE Project 视图或 `find . -name pom.xml` |

## 02-matrix（第 2 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-capability-layers.png` | LC4J 能力分层 | 导出 mermaid 或手绘拍照 |
| `fig-02-pom-dependencies.png` | ai-core/ai-rag 依赖 | IDE pom 或 dependency:tree |
| `fig-03-prompt-diff.png` | BASE vs FULL prompt | 演进测试输出或 Prompt 面板对比 |

## 03-chatmodel（第 3 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-llm-request-log.png` | logRequests 日志 | 启动 chat 服务，发一条消息看控制台 |
| `fig-02-dual-llm-sequence.png` | 双 LLM 调用 | 时序图截图或 draw.io |
| `fig-03-router-decision-ui.png` | Agent 面板决策 | 前端 AgentDecisionPanel |

## 04-embedding-rag（第 4 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-rag-ingest-api.png` | RAG 入库响应 | curl POST /api/rag/add |
| `fig-02-rag-context-panel.png` | RAG 上下文面板 | 聊天命中知识库后截图 |
| `fig-03-vector-store-debug.png` | 向量存储 | H2 console 或日志 |
| `fig-04-prompt-rag-section.png` | Prompt 参考知识段 | Prompt 面板 |

## 05-chat-memory（第 5 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-multi-turn-prompt.png` | 含历史对话的 prompt | 同 sessionId 第二轮 |
| `fig-02-history-truncation.png` | 历史截断提示 | maxHistoryChars 调小后 |
| `fig-03-memory-admin.png` | 会话列表 | 管理端 Memory 页（若有） |

## 06-tools（第 6 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-tool-decision.png` | useTools + toolName | 订单查询消息 |
| `fig-02-tool-result-json.png` | Tool 结果面板 | 展开 ToolResultPanel |
| `fig-03-prompt-tool-section.png` | ### 工具结果 | Prompt 面板 |
| `fig-04-rule-fallback.png` | rule-based 回退 | 关闭 LLM 路由或模拟解析失败 |

## 07-aiservices（第 7 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-pipeline-vs-aiservices.png` | 管道对比 | 文章内表格截图 |
| `fig-02-trace-field-mapping.png` | trace 与 UI 映射 | 标注 ChatTraceResponse 字段 |

## 08-production（第 8 篇）

| 文件 | 内容 | 拍摄步骤 |
|------|------|----------|
| `fig-01-sse-stream-ui.png` | 流式逐字输出 | /api/chat/stream |
| `fig-02-trace-hidden.png` | trace 关闭 | expose-prompt-trace=false |
| `fig-03-prometheus-metrics.png` | 指标端点 | /actuator/prometheus 片段 |
