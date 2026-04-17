package com.aics.prompt;

import com.aics.prompt.config.PromptEngineProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * {@code ai-prompt} 模块自动配置：扫描 {@code com.aics.prompt} 并启用
 * {@link PromptEngineProperties} 配置绑定。
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.aics.prompt")
@EnableConfigurationProperties(PromptEngineProperties.class)
public class AiPromptAutoConfiguration {
}
