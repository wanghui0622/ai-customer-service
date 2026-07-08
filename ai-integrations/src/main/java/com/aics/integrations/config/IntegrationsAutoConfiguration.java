package com.aics.integrations.config;

import com.aics.integrations.connector.MockIntegrationConnector;
import com.aics.integrations.connector.RestIntegrationConnector;
import com.aics.integrations.spi.IntegrationConnector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoConfiguration
@ComponentScan(basePackages = "com.aics.integrations")
@EnableConfigurationProperties(IntegrationsProperties.class)
public class IntegrationsAutoConfiguration {

    @Bean
    List<IntegrationConnector> integrationConnectors(IntegrationsProperties properties,
                                                     WebClient.Builder webClientBuilder) {
        List<IntegrationConnector> connectors = new ArrayList<>();
        properties.getConnectors().forEach((id, cfg) -> {
            if ("mock".equalsIgnoreCase(cfg.getType())) {
                connectors.add(new MockIntegrationConnector(id, cfg.getOperations().keySet()));
            } else {
                connectors.add(new RestIntegrationConnector(
                        id, cfg, properties.getDefaultTimeout(), webClientBuilder));
            }
        });
        if (connectors.isEmpty()) {
            connectors.add(new MockIntegrationConnector("ticket-service", Set.of("create", "query")));
            connectors.add(new MockIntegrationConnector("order-service", Set.of("query")));
        }
        return connectors;
    }
}
