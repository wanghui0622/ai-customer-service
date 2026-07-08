package com.aics.graph.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LangGraph 编排专属配置，前缀 {@code aics.orchestration.graph}。
 */
@ConfigurationProperties(prefix = "aics.orchestration.graph")
public class GraphOrchestrationProperties {

    private boolean enabled = true;
    private int maxSteps = 20;
    private int maxToolLoops = 5;
    private boolean reactEnabled = true;

    private Approval approval = new Approval();
    private Checkpoint checkpoint = new Checkpoint();
    private SubGraph subGraph = new SubGraph();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public int getMaxToolLoops() {
        return maxToolLoops;
    }

    public void setMaxToolLoops(int maxToolLoops) {
        this.maxToolLoops = maxToolLoops;
    }

    public boolean isReactEnabled() {
        return reactEnabled;
    }

    public void setReactEnabled(boolean reactEnabled) {
        this.reactEnabled = reactEnabled;
    }

    public Approval getApproval() {
        return approval;
    }

    public void setApproval(Approval approval) {
        this.approval = approval != null ? approval : new Approval();
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint != null ? checkpoint : new Checkpoint();
    }

    public SubGraph getSubGraph() {
        return subGraph;
    }

    public void setSubGraph(SubGraph subGraph) {
        this.subGraph = subGraph != null ? subGraph : new SubGraph();
    }

    public static class Approval {
        private boolean enabled = true;
        private String sensitiveTools = "ticket_create";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSensitiveTools() {
            return sensitiveTools;
        }

        public void setSensitiveTools(String sensitiveTools) {
            this.sensitiveTools = sensitiveTools;
        }
    }

    public static class Checkpoint {
        /** memory | redis */
        private String store = "memory";

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }
    }

    public static class SubGraph {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
