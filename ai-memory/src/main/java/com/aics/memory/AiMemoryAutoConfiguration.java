package com.aics.memory;

import com.aics.memory.config.MemoryProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot 自动配置：注册 {@link MemoryProperties}，并扫描 {@code com.aics.memory} 下
 * {@link org.springframework.stereotype.Component} / {@link org.springframework.stereotype.Service}。
 * <p>
 * 通过 {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports} 加载。
 */
@AutoConfiguration
@EnableConfigurationProperties(MemoryProperties.class)
@ComponentScan(basePackages = "com.aics.memory")
public class AiMemoryAutoConfiguration {
}
