package com.aics.tools.builtin;

import com.aics.integrations.adapter.TicketIntegrationAdapter;
import com.aics.integrations.domain.ticket.CreateTicketRequest;
import com.aics.integrations.domain.ticket.TicketDto;
import com.aics.tools.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TicketCreateTool implements Tool {

    public static final String NAME = "ticket_create";
    private static final ObjectMapper JSON = new ObjectMapper();

    private final TicketIntegrationAdapter ticketIntegrationAdapter;

    public TicketCreateTool(TicketIntegrationAdapter ticketIntegrationAdapter) {
        this.ticketIntegrationAdapter = ticketIntegrationAdapter;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "为用户创建售后/投诉工单。";
    }

    @Override
    public String inputSchemaJson() {
        return """
                {"type":"object","properties":{"query":{"type":"string"}},"required":["query"]}
                """.trim();
    }

    @Override
    public String execute(String input) {
        try {
            String text = input == null ? "" : input.trim();
            String priority = text.contains("投诉") || text.contains("紧急") ? "high" : "normal";
            TicketDto ticket = ticketIntegrationAdapter.create(new CreateTicketRequest(
                    "用户反馈",
                    text,
                    priority
            ));
            return JSON.writeValueAsString(Map.of(
                    "tool", NAME,
                    "ticketId", ticket.ticketId(),
                    "status", ticket.status(),
                    "priority", priority
            ));
        } catch (Exception e) {
            return JSON.createObjectNode()
                    .put("tool", NAME)
                    .put("error", e.getMessage())
                    .toString();
        }
    }
}
