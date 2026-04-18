package com.aics.service.orchestration.router;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.AgentRouter;
import com.aics.agentrouter.LlmAgentRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LLM 路由优先；解析失败或未预期异常时回退到规则路由器，避免整条对话失败。
 */
public final class FallbackAgentRouter implements AgentRouter {

    private static final Logger log = LoggerFactory.getLogger(FallbackAgentRouter.class);

    private final LlmAgentRouter llmRouter;
    private final RuleBasedAgentRouter ruleRouter;

    public FallbackAgentRouter(LlmAgentRouter llmRouter, RuleBasedAgentRouter ruleRouter) {
        this.llmRouter = llmRouter;
        this.ruleRouter = ruleRouter;
    }

    @Override
    public AgentDecision route(String message, String history) {
        try {
            return llmRouter.route(message, history);
        } catch (RuntimeException e) {
            log.warn("LLM AgentRouter 失败，回退规则路由: {}", e.toString());
            return ruleRouter.route(message, history);
        }
    }
}
