package com.aics.graph;

import com.aics.graph.config.GraphOrchestrationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = CustomerServiceGraph.class)
@EnableConfigurationProperties(GraphOrchestrationProperties.class)
public class AiGraphAutoConfiguration {
}
