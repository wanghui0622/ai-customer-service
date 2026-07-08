# LangChain4j 实战博客系列 — 发布规范

## 平台

通用 Markdown，适用于 GitHub、博客园、自建静态站。保留 mermaid 图与代码高亮。

## 单篇结构

每篇长文按以下顺序组织：

1. **YAML front matter** — title、series、tags、summary、estimated_reading_minutes
2. **系列叙事线** — 固定引用块（1 段）
3. **写在前面** — 导读、读者画像、阅读前提
4. **你将学到什么** —  bullet 列表
5. **环境准备** — JDK、Maven、端口、配置项
6. **正文** — 原提纲章节扩写
7. **动手验证** — bash 命令 + text 预期输出（分组）
8. **常见问题 FAQ**
9. **本篇小结** — 表格或结论框
10. **系列导航** — 上一篇 / 下一篇链接

## 截图占位规范

```markdown
![图N：简短标题](./assets/XX-topic/fig-NN-slug.png)

> **截图说明**：具体拍摄步骤（启动什么服务、点什么、期望看到什么）。
```

- 图片路径相对文章文件：`./assets/...`
- 真实 png 按 [assets/README.md](./assets/README.md) 清单补拍
- 发布前可将占位图替换为实际截图

## 命令输出规范

```markdown
### 步骤标题

\`\`\`bash
cd ai-customer-service
mvn -pl ai-reactive-chat spring-boot:run
\`\`\`

\`\`\`text
# 预期输出（结构真实，文案可略有简化）
...
\`\`\`
```

- bash 与 text 分块
- 标注依赖配置（如 `expose-prompt-trace: true`）
- JSON 响应与 [ChatTraceResponse](../../ai-reactive-chat/src/main/java/com/aics/reactivechat/dto/ChatTraceResponse.java) 字段对齐

## 术语统一

| 术语 | 含义 |
|------|------|
| 编排层 | `AiChatService` |
| Model Layer | LangChain4j `ChatModel` / `EmbeddingModel` |
| 薄集成 | 仅用 LC4J 模型 I/O，不用 Agent/RAG/Memory 高层 API |

## 发布检查清单

- [ ] front matter 完整
- [ ] 至少 3 个截图占位 + 说明块
- [ ] 至少 2 组 bash + text
- [ ] 2–3 处源码路径链接
- [ ] mermaid 节点 ID 无空格
- [ ] 系列导航链接正确
