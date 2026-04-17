package com.aics.rag;

import com.aics.prompt.AiPromptAutoConfiguration;
import com.aics.rag.config.RagProperties;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * RAG 模块自动配置：启用后扫描 {@code com.aics.rag}，并导入 {@link AiPromptAutoConfiguration}。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "aics.rag", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RagProperties.class)
@Import(AiPromptAutoConfiguration.class)
@ComponentScan(basePackages = "com.aics.rag")
public class AiRagAutoConfiguration {

    /**
     * 默认进程内 ONNX 嵌入模型；应用可提供自己的 {@link EmbeddingModel} Bean 以替换（例如 OpenAI Embeddings）。
     */
    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel ragDefaultEmbeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}
