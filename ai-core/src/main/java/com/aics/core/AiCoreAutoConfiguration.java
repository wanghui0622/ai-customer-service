package com.aics.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {"com.aics.core.llm", "com.aics.core.config"})
public class AiCoreAutoConfiguration {
}
