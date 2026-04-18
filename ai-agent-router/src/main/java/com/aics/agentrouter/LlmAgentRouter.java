package com.aics.agentrouter;

import com.aics.agentrouter.parse.AgentDecisionJsonParser;
import com.aics.agentrouter.prompt.AgentRouterPrompts;
import com.aics.spi.LlmClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

/**
 * 调用主模型做一次「结构化路由」：输出 JSON，解析为 {@link AgentDecision}。
 * <p>
 * 与主对话共用 {@link LlmClient}，会产生额外一次模型调用；可通过配置关闭并回退到规则路由器。
 */
@Component("llmAgentRouter")
@ConditionalOnProperty(
        prefix = "aics.orchestration",
        name = "agent-router-llm-enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class LlmAgentRouter implements AgentRouter {

    private static final Set<String> KNOWN_TOOLS = Set.of(
            "echo",
            "order_query",
            "weather_query"
    );

    private final LlmClient llm;

    public LlmAgentRouter(LlmClient llm) {
        this.llm = llm;
    }

    @Override
    public AgentDecision route(String message, String history) {
        String prompt = AgentRouterPrompts.build(history, message);
        String raw = llm.chat(prompt);
        AgentDecision d = AgentDecisionJsonParser.parse(raw);
        return sanitize(d);
    }

    private static AgentDecision sanitize(AgentDecision d) {
        if (!d.useTools() || d.toolName().isEmpty()) {
            return d;
        }
        String name = d.toolName().toLowerCase(Locale.ROOT).trim();
        if (KNOWN_TOOLS.contains(name)) {
            return new AgentDecision(d.useRag(), true, name, d.reason());
        }
        return new AgentDecision(d.useRag(), d.useTools(), "", d.reason());
    }
}
