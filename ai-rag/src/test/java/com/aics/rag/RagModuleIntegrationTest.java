package com.aics.rag;

import com.aics.rag.autoconfigure.AiRagAutoConfiguration;
import com.aics.rag.chunking.ChunkingOptions;
import com.aics.rag.chunking.TextChunker;
import com.aics.rag.domain.RagDocument;
import com.aics.rag.service.RagIngestionService;
import com.aics.rag.vectorstore.VectorStore;
import com.aics.spi.KnowledgeRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AiRagAutoConfiguration.class)
@TestPropertySource(properties = {
        "aics.rag.vector-store=in-memory",
        "aics.rag.chunk-size=120",
        "aics.rag.chunk-overlap=20",
        "aics.rag.retrieval-top-k=5",
        "aics.rag.min-score=0.0"
})
class RagModuleIntegrationTest {

    @Autowired
    RagIngestionService ragIngestionService;

    @Autowired
    KnowledgeRetriever knowledgeRetriever;

    @Autowired
    VectorStore vectorStore;

    @BeforeEach
    void clearStore() {
        vectorStore.removeAll();
    }

    @Test
    void chunkerProducesOverlappingWindows() {
        TextChunker chunker = new TextChunker();
        String text = "a".repeat(100) + "b".repeat(100);
        var chunks = chunker.split(text, new ChunkingOptions(80, 20));
        assertThat(chunks).isNotEmpty();
        assertThat(chunks.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void afterIngest_retrieveReturnsTextChunks() {
        RagDocument doc = RagDocument.plain(
                "退换货",
                """
                        用户签收后 7 日内可申请无理由退货，商品需保持完好未使用。
                        手机等电子产品一经激活，除质量问题外不予退货。
                        """);
        assertThat(ragIngestionService.ingest(doc)).isPositive();

        List<String> chunks = knowledgeRetriever.retrieve("激活后的手机还能退吗");
        assertThat(chunks).isNotEmpty();
        String joined = String.join("", chunks);
        assertThat(joined).contains("激活");
    }
}
