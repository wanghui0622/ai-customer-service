package com.aics.service;

import com.aics.service.config.OrchestrationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 启用编排配置绑定；Bean 由应用根扫描 {@code com.aics} 注册，此处不再重复 {@code ComponentScan}。
 */
@AutoConfiguration
@EnableConfigurationProperties(OrchestrationProperties.class)
public class AiServiceAutoConfiguration {
}
