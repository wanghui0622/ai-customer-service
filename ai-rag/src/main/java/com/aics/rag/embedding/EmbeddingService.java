package com.aics.rag.embedding;

import java.util.List;

/**
 * 嵌入抽象：可替换为远程 API、其他 ONNX 模型等，而不影响检索与编排层。
 */
public interface EmbeddingService {

    /**
     * 单段文本转向量。
     */
    float[] embed(String text);

    /**
     * 批量嵌入（默认顺序与输入一致）。
     */
    List<float[]> embedAll(List<String> texts);
}
