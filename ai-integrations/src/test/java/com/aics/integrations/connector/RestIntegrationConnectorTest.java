package com.aics.integrations.connector;

import com.aics.integrations.config.IntegrationsProperties;
import com.aics.integrations.spi.IntegrationRequest;
import com.aics.integrations.spi.IntegrationResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RestIntegrationConnectorTest {

    private static WireMockServer wireMock;

    @BeforeAll
    static void startWireMock() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        WireMock.configureFor("localhost", wireMock.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMock != null) {
            wireMock.stop();
        }
    }

    @Test
    void executesConfiguredRestOperation() {
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v1/orders/ORD-123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"orderId\":\"ORD-123\",\"status\":\"shipped\"}")));

        IntegrationsProperties.ConnectorProperties cfg = new IntegrationsProperties.ConnectorProperties();
        cfg.setBaseUrl("http://localhost:" + wireMock.port());
        cfg.setOperations(Map.of(
                "query", operation("GET", "/api/v1/orders/{orderId}")
        ));

        RestIntegrationConnector connector = new RestIntegrationConnector(
                "order-service",
                cfg,
                Duration.ofSeconds(3),
                WebClient.builder()
        );

        IntegrationResponse response = connector.execute(
                "query",
                IntegrationRequest.ofPath("orderId", "ORD-123"));

        assertThat(response.success()).isTrue();
        assertThat(response.body()).contains("shipped");
    }

    private static IntegrationsProperties.OperationProperties operation(String method, String path) {
        IntegrationsProperties.OperationProperties op = new IntegrationsProperties.OperationProperties();
        op.setMethod(method);
        op.setPath(path);
        return op;
    }
}
