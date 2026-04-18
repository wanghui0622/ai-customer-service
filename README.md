# ai-customer-service

基于 **Spring Boot** 与 **LangChain4j** 的 AI 客服示例工程：多模块拆分（对话编排、提示词、记忆、RAG、工具等），便于扩展与替换实现。

👉 **第一次阅读请先打开：[系统架构说明（面向读者）](docs/SYSTEM_ARCHITECTURE.md)** — 全局视角、调用链、模块职责与运行方式，不涉及代码细节。

## 技术栈

| 项目 | 说明 |
|------|------|
| Java | 17 |
| 构建 | Apache Maven |
| 框架 | Spring Boot 3.3.x |
| LLM | LangChain4j（OpenAI 兼容接口等，具体见各模块配置） |

## 模块一览

| 模块 | 职责简述 |
|------|----------|
| `ai-parent` | 父 POM：统一 Java 版本、Spring Boot BOM、插件管理 |
| `ai-common` | 公共工具与常量 |
| `ai-model` | 领域模型 / DTO |
| `ai-core` | 对话编排、LLM 抽象与默认实现 |
| `ai-prompt` | 提示词模板、构建器与场景化工厂 |
| `ai-rag` | 检索增强相关能力 |
| `ai-memory` | 会话记忆存储 |
| `ai-tools` | 工具 / 函数调用侧扩展 |
| `ai-eval` | 评估与实验相关 |
| `ai-service` | 业务门面与模块组装 |
| `ai-reactive-chat` | 纯 Netty WebFlux 聊天服务（独立部署，默认 8081） |
| `ai-admin-webmvc` | 管理端与评估等 HTTP API（Spring MVC，可执行 fat jar，默认 8080） |

## 环境要求

- JDK 17+
- Maven 3.8+（或与项目兼容的版本）

## 构建

在项目根目录执行：

```bash
mvn clean verify
```

仅编译（跳过测试可按需加 `-DskipTests`）：

```bash
mvn clean compile
```

## 运行

**管理后台 / 评估 / Prompt / RAG 等（Spring MVC）**：

```bash
mvn -pl ai-admin-webmvc spring-boot:run
```

主类：`com.aics.adminwebmvc.AiCustomerServiceApplication`（扫描 `com.aics` 根包），默认端口 **8080**。

**聊天（纯 Netty WebFlux）** 独立部署，默认端口 **8081**：

```bash
mvn -pl ai-reactive-chat spring-boot:run
```

主类：`com.aics.reactivechat.ReactiveChatApplication`。接口：`POST /api/chat`、`POST /api/chat/stream`。

打包后运行：

```bash
mvn -pl ai-admin-webmvc package
java -jar ai-admin-webmvc/target/ai-admin-webmvc-*.jar
```

## 配置说明

各模块可通过 Spring `application.yml` / `application.properties` 配置。例如提示词默认版本等可使用前缀 `aics.prompt`（详见 `ai-prompt` 模块中的 `PromptEngineProperties`）。

LLM 的 API 地址、密钥等**请勿提交到版本库**，请使用环境变量或本地覆盖配置。

## 许可证

本项目采用 **MIT License** 发布，可自由使用、修改与再分发，详见仓库根目录 [`LICENSE`](LICENSE)。

## 免责声明

本仓库代码仅供学习与研究参考；用于生产环境前请自行完成安全、合规与容量评估。第三方模型与服务的使用须遵守其服务条款。
