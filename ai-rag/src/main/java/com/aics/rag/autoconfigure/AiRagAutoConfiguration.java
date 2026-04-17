package com.aics.rag.autoconfigure;

import com.aics.rag.config.RagProperties;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnProperty(prefix = "aics.rag", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RagProperties.class)
@ComponentScan(basePackages = {
        "com.aics.rag.chunking",
        "com.aics.rag.embedding",
        "com.aics.rag.ingestion",
        "com.aics.rag.retriever",
        "com.aics.rag.service",
        "com.aics.rag.vectorstore"
})
public class AiRagAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel ragDefaultEmbeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}
