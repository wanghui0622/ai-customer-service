/**
 * AI 工具执行层：可插拔 {@link com.aics.tools.Tool}、注册表 {@link com.aics.tools.registry.ToolRegistry}、
 * 按名调度 {@link com.aics.tools.exec.ToolInvocationExecutor}、门面 {@link com.aics.tools.ToolService}。
 * 不依赖 LLM，不包含业务编排（编排见 ai-service）。
 */
package com.aics.tools;
