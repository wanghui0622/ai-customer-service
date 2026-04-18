package com.aics.agentrouter;

import java.util.Objects;

/**
 * 固定决策，用于评估、单测或与 Spring 解耦的手动装配。
 */
public final class FixedAgentRouter implements AgentRouter {

    private final AgentDecision decision;

    public FixedAgentRouter(AgentDecision decision) {
        this.decision = Objects.requireNonNull(decision);
    }

    @Override
    public AgentDecision route(String message, String history) {
        return decision;
    }
}
