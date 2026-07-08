package com.aics.integrations.connector;

import com.aics.integrations.spi.IntegrationConnector;
import com.aics.integrations.spi.IntegrationRequest;
import com.aics.integrations.spi.IntegrationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

/**
 * 本地演示 / 测试用 Mock 连接器。
 */
public class MockIntegrationConnector implements IntegrationConnector {

    private static final ObjectMapper JSON = new ObjectMapper();
    private final String id;
    private final Set<String> operations;

    public MockIntegrationConnector(String id, Set<String> operations) {
        this.id = id;
        this.operations = operations;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String type() {
        return "mock";
    }

    @Override
    public boolean supports(String operation) {
        return operations.contains(operation);
    }

    @Override
    public IntegrationResponse execute(String operation, IntegrationRequest request) {
        try {
            return switch (operation) {
                case "create" -> IntegrationResponse.ok(JSON.writeValueAsString(Map.of(
                        "ticketId", "TK-MOCK-001",
                        "status", "open",
                        "subject", request.body()
                )));
                case "query" -> IntegrationResponse.ok(JSON.writeValueAsString(Map.of(
                        "ticketId", request.pathParams().getOrDefault("ticketId", "TK-MOCK-001"),
                        "status", "in_progress"
                )));
                case "order_query" -> IntegrationResponse.ok(JSON.writeValueAsString(Map.of(
                        "orderId", request.pathParams().getOrDefault("orderId", "DEMO-001"),
                        "status", "已发货",
                        "carrier", "演示物流"
                )));
                default -> IntegrationResponse.error(404, "unsupported mock operation: " + operation);
            };
        } catch (Exception e) {
            return IntegrationResponse.error(500, e.getMessage());
        }
    }
}
