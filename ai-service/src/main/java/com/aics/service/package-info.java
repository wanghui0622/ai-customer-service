/**
 * AI 编排层（Orchestration）：唯一协调 {@code ai-memory}、{@code ai-rag}、{@code ai-tools}、
 * {@code ai-prompt}、{@code ai-core}，本包不实现具体 AI 能力，仅做调用与策略组合。
 * <p>
 * 入口：{@link com.aics.service.chat.AiChatService}、{@link com.aics.service.chat.CustomerChatFacade}。
 */
package com.aics.service;
