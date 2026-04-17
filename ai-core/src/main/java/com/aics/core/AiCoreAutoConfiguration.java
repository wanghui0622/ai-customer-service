package com.aics.core;

import com.aics.prompt.AiPromptAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * {@code ai-core} 模块的 Spring Boot 自动配置：扫描并注册 {@code com.aics.core} 包下的 Bean。
 * <p>
 * 通过 {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports} 注册。
 */
@AutoConfiguration
@Import(AiPromptAutoConfiguration.class)
@ComponentScan(basePackages = "com.aics.core")
public class AiCoreAutoConfiguration {
}
