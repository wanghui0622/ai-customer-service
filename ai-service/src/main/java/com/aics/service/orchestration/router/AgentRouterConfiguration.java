package com.aics.service.orchestration.router;

import com.aics.agentrouter.AgentRouter;
import com.aics.agentrouter.LlmAgentRouter;
import com.aics.service.config.OrchestrationProperties;
import com.aics.service.orchestration.policy.RagEligibilityPolicy;
import com.aics.service.orchestration.policy.ToolEligibilityPolicy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 装配主用 {@link AgentRouter}：启用 LLM 时包装为 {@link FallbackAgentRouter}，否则仅用规则。
 */
@Configuration
public class AgentRouterConfiguration {

    @Bean
    public RuleBasedAgentRouter ruleBasedAgentRouter(
            RagEligibilityPolicy ragPolicy,
            ToolEligibilityPolicy toolPolicy) {
        return new RuleBasedAgentRouter(ragPolicy, toolPolicy);
    }

    @Bean
    @Primary
    public AgentRouter agentRouter(
            ObjectProvider<LlmAgentRouter> llmRouterProvider,
            RuleBasedAgentRouter ruleRouter,
            OrchestrationProperties properties) {
        if (!properties.isAgentRouterLlmEnabled()) {
            return ruleRouter;
        }
        LlmAgentRouter llm = llmRouterProvider.getIfAvailable();
        if (llm == null) {
            return ruleRouter;
        }
        return new FallbackAgentRouter(llm, ruleRouter);
    }
}
