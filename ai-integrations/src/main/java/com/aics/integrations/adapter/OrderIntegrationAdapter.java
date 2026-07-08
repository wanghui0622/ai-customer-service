package com.aics.integrations.adapter;

import com.aics.integrations.domain.order.OrderDto;
import com.aics.integrations.spi.IntegrationRegistry;
import com.aics.integrations.spi.IntegrationRequest;
import com.aics.integrations.spi.IntegrationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OrderIntegrationAdapter {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Pattern ORDER_ID = Pattern.compile("(ORD[-_]?\\d+|\\d{6,})");

    private final IntegrationRegistry registry;
    private final String connectorId;

    public OrderIntegrationAdapter(IntegrationRegistry registry,
                                   @Value("${aics.integrations.order-connector-id:order-service}") String connectorId) {
        this.registry = registry;
        this.connectorId = connectorId;
    }

    public OrderDto query(String userInput) {
        String orderId = extractOrderId(userInput);
        IntegrationResponse response = registry.require(connectorId).execute(
                "query",
                IntegrationRequest.ofPath("orderId", orderId));
        if (!response.success()) {
            throw new IllegalStateException("order query failed: " + response.body());
        }
        return parseOrder(response.body());
    }

    private static String extractOrderId(String input) {
        Matcher matcher = ORDER_ID.matcher(input == null ? "" : input);
        if (matcher.find()) {
            return matcher.group(1).replace('_', '-');
        }
        return "DEMO-001";
    }

    private OrderDto parseOrder(String body) {
        try {
            JsonNode node = JSON.readTree(body);
            return new OrderDto(
                    node.path("orderId").asText(""),
                    node.path("status").asText(""),
                    node.path("carrier").asText(""),
                    node.path("eta").asText("")
            );
        } catch (Exception e) {
            throw new IllegalStateException("invalid order response: " + body, e);
        }
    }
}
