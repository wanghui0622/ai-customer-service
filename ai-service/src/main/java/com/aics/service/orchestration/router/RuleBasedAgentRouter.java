package com.aics.service.orchestration.router;

import com.aics.agentrouter.AgentDecision;
import com.aics.agentrouter.AgentRouter;
import com.aics.service.orchestration.policy.RagEligibilityPolicy;
import com.aics.service.orchestration.policy.ToolEligibilityPolicy;

/**
 * 原启发式门控的等价实现：输出 {@link AgentDecision}，供 LLM 路由失败或未启用时回退。
 * 由 {@link AgentRouterConfiguration} 注册为 Bean。
 */
public class RuleBasedAgentRouter implements AgentRouter {

    private final RagEligibilityPolicy ragPolicy;
    private final ToolEligibilityPolicy toolPolicy;

    public RuleBasedAgentRouter(RagEligibilityPolicy ragPolicy,
                                ToolEligibilityPolicy toolPolicy) {
        this.ragPolicy = ragPolicy;
        this.toolPolicy = toolPolicy;
    }

    @Override
    public AgentDecision route(String message, String history) {
        boolean rag = ragPolicy.shouldRetrieveKnowledge(message);
        boolean tools = toolPolicy.shouldExecuteTools(message);
        return new AgentDecision(rag, tools, "", "rule-based");
    }
}
