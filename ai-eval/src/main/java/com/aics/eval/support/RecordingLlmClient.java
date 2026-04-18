package com.aics.eval.support;

import com.aics.spi.LlmClient;

public final class RecordingLlmClient implements LlmClient {

    public String lastPrompt;

    @Override
    public String chat(String prompt) {
        this.lastPrompt = prompt;
        return fakeAnswer(prompt);
    }

    public static String fakeAnswer(String prompt) {
        boolean sys = prompt.contains("你是专业 AI 客服助手");
        boolean rag = prompt.contains("### 参考知识");
        boolean tool = prompt.contains("### 工具结果");
        boolean mem = prompt.contains("### 历史对话");
        if (sys && rag && tool && mem) {
            return "【FULL】订单123当前状态已同步：发货环节进行中；物流承运信息已可查；请结合订单页查看发货与物流状态。";
        }
        if (sys && rag && mem && !tool) {
            return "【MEMORY+RAG】根据历史与知识：订单与发货规则已适用；当前订单状态为处理中；物流更新请关注通知。";
        }
        if (sys && rag && !mem) {
            return "【RAG】知识库说明：订单发货时效与物流规则如下；请核对订单状态与物流单号。";
        }
        if (sys && !rag) {
            return "【PROMPT】请按客服规范说明订单、发货、状态与物流查询方式。";
        }
        return "【BASE】大概还没发吧，我也不确定订单和物流细节。";
    }
}
