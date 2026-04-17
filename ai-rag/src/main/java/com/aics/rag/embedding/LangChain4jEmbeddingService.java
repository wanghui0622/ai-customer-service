package com.aics.rag.embedding;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 基于 LangChain4j {@link EmbeddingModel} 的默认实现（进程内 ONNX 或远程模型均可）。
 */
@Service
public class LangChain4jEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public LangChain4jEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = Objects.requireNonNull(embeddingModel);
    }

    @Override
    public float[] embed(String text) {
        Response<Embedding> r = embeddingModel.embed(text == null ? "" : text);
        return r.content().vector();
    }

    @Override
    public List<float[]> embedAll(List<String> texts) {
        List<float[]> out = new ArrayList<>();
        if (texts == null || texts.isEmpty()) {
            return out;
        }
        for (String t : texts) {
            out.add(embed(t));
        }
        return out;
    }
}
