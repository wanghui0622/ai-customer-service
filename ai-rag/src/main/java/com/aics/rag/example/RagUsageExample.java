package com.aics.rag.example;

import com.aics.rag.domain.RagDocument;
import com.aics.rag.service.RagIngestionService;
import com.aics.rag.service.RagService;

/**
 * 博客友好示例：演示「入库 → 问答」最小闭环（需在 Spring 上下文中注入 Bean）。
 * <p>
 * <b>与 ai-core 集成：</b>在应用模块中注册：
 * <pre>{@code
 * @Bean
 * RagLlmClient ragLlmClient(LlmProvider llm) {
 *     return llm::chat;
 * }
 * }</pre>
 */
public final class RagUsageExample {

    private RagUsageExample() {
    }

    /**
     * 将一段企业知识写入向量库，再基于 RAG 回答问题。
     */
    public static String demoIngestAndAsk(RagIngestionService ingestion, RagService rag) {
        RagDocument doc = RagDocument.plain("退换货政策", """
                自签收之日起 7 日内可申请无理由退货，商品需保持完好。
                电子类激活后不支持退货，除非存在质量问题。
                """);
        int n = ingestion.ingest(doc);
        if (n <= 0) {
            throw new IllegalStateException("expected chunks");
        }
        return rag.answer("激活后的手机还能退吗？");
    }
}
