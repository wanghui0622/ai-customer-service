package com.aics.service.evolution;

final class EvolutionLlmFixtures {

    private EvolutionLlmFixtures() {
    }

    static String fakeAnswer(String prompt) {
        boolean sys = prompt.contains("你是专业 AI 客服助手");
        boolean rag = prompt.contains("### 参考知识");
        boolean tool = prompt.contains("### 工具结果");
        boolean mem = prompt.contains("### 历史对话");
        if (sys && rag && tool && mem) {
            return "【模拟答复-全能力】结合历史、知识库与订单工具：订单123当前「拣货中」，华东仓预计12小时内交快递；昨日已确认收款，与知识库发货时效一致。";
        }
        if (sys && rag && !tool) {
            return "【模拟答复-RAG】依据参考知识：未发货常见于大促排期；您的订单123应已进入仓库流程，请留意物流更新。";
        }
        if (sys && !rag) {
            return "【模拟答复-Prompt版】已按客服角色与段落结构组织问题；建议在后台核对订单与支付状态后再答复用户。";
        }
        return "【模拟答复-纯模型】仓库可能比较忙吧，一般等等就会发（未使用业务知识与订单数据，易泛化）。";
    }
}
