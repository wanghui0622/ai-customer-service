package com.aics.integrations.adapter;

import com.aics.integrations.domain.ticket.CreateTicketRequest;
import com.aics.integrations.domain.ticket.TicketDto;
import com.aics.integrations.spi.IntegrationRegistry;
import com.aics.integrations.spi.IntegrationRequest;
import com.aics.integrations.spi.IntegrationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TicketIntegrationAdapter {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final IntegrationRegistry registry;
    private final String connectorId;

    public TicketIntegrationAdapter(IntegrationRegistry registry,
                                    @Value("${aics.integrations.ticket-connector-id:ticket-service}") String connectorId) {
        this.registry = registry;
        this.connectorId = connectorId;
    }

    public TicketDto create(CreateTicketRequest request) {
        IntegrationResponse response = registry.require(connectorId).execute(
                "create",
                IntegrationRequest.ofBody(Map.of(
                        "subject", request.subject(),
                        "description", request.description(),
                        "priority", request.priority()
                )));
        if (!response.success()) {
            throw new IllegalStateException("ticket create failed: " + response.body());
        }
        return parseTicket(response.body());
    }

    public TicketDto get(String ticketId) {
        IntegrationResponse response = registry.require(connectorId).execute(
                "query",
                IntegrationRequest.ofPath("ticketId", ticketId));
        if (!response.success()) {
            throw new IllegalStateException("ticket query failed: " + response.body());
        }
        return parseTicket(response.body());
    }

    private TicketDto parseTicket(String body) {
        try {
            JsonNode node = JSON.readTree(body);
            return new TicketDto(
                    node.path("ticketId").asText(""),
                    node.path("status").asText(""),
                    node.path("subject").asText("")
            );
        } catch (Exception e) {
            throw new IllegalStateException("invalid ticket response: " + body, e);
        }
    }
}
