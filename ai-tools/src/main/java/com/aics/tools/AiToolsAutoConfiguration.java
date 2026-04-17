package com.aics.tools;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 扫描 {@code com.aics.tools} 及子包，注册 {@link Tool}、{@link com.aics.tools.registry.ToolRegistry}、
 * {@link com.aics.tools.spi.OrchestrationToolExecutor} 等 Bean。
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.aics.tools")
public class AiToolsAutoConfiguration {
}
