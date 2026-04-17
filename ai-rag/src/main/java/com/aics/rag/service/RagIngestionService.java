package com.aics.rag.service;

import com.aics.rag.chunking.ChunkingOptions;
import com.aics.rag.chunking.TextChunker;
import com.aics.rag.config.RagProperties;
import com.aics.rag.domain.RagDocument;
import com.aics.rag.domain.TextChunk;
import com.aics.rag.domain.VectorRecord;
import com.aics.rag.embedding.EmbeddingService;
import com.aics.rag.ingestion.DocumentIngestionService;
import com.aics.rag.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档导入编排：切分 → 嵌入 → 写入 {@link VectorStore}。
 */
@Service
public class RagIngestionService {

    private final DocumentIngestionService documentIngestionService;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final RagProperties properties;

    public RagIngestionService(DocumentIngestionService documentIngestionService,
                               TextChunker textChunker,
                               EmbeddingService embeddingService,
                               VectorStore vectorStore,
                               RagProperties properties) {
        this.documentIngestionService = documentIngestionService;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.properties = properties;
    }

    /**
     * 导入一篇文档并返回写入的向量条数。
     */
    public int ingest(RagDocument document) {
        ChunkingOptions opts = new ChunkingOptions(properties.getChunkSize(), properties.getChunkOverlap());
        List<TextChunk> chunks = textChunker.split(document.content(), opts);
        List<VectorRecord> batch = new ArrayList<>();
        for (TextChunk c : chunks) {
            HashMap<String, String> meta = new HashMap<>(document.mergedMetadata(c.metadata()));
            meta.put("documentId", document.id());
            if (!document.title().isEmpty()) {
                meta.putIfAbsent("docTitle", document.title());
            }
            float[] vec = embeddingService.embed(c.text());
            batch.add(new VectorRecord(c.id(), c.text(), vec, meta));
        }
        vectorStore.upsertAll(batch);
        return batch.size();
    }

    public DocumentIngestionService documents() {
        return documentIngestionService;
    }
}
