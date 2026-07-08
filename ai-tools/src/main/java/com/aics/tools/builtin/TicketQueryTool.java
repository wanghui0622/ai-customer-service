package com.aics.tools.builtin;

import com.aics.integrations.adapter.TicketIntegrationAdapter;
import com.aics.integrations.domain.ticket.TicketDto;
import com.aics.tools.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TicketQueryTool implements Tool {

    public static final String NAME = "ticket_query";
    private static final Pattern TICKET_ID = Pattern.compile("(TK[-_]?\\w+|ticket[-_]?\\d+)", Pattern.CASE_INSENSITIVE);
    private static final ObjectMapper JSON = new ObjectMapper();

    private final TicketIntegrationAdapter ticketIntegrationAdapter;

    public TicketQueryTool(TicketIntegrationAdapter ticketIntegrationAdapter) {
        this.ticketIntegrationAdapter = ticketIntegrationAdapter;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "查询工单状态。";
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
            String ticketId = extractTicketId(input);
            TicketDto ticket = ticketIntegrationAdapter.get(ticketId);
            return JSON.writeValueAsString(Map.of(
                    "tool", NAME,
                    "ticketId", ticket.ticketId(),
                    "status", ticket.status(),
                    "subject", ticket.subject()
            ));
        } catch (Exception e) {
            return JSON.createObjectNode()
                    .put("tool", NAME)
                    .put("error", e.getMessage())
                    .toString();
        }
    }

    private static String extractTicketId(String input) {
        Matcher matcher = TICKET_ID.matcher(input == null ? "" : input);
        if (matcher.find()) {
            return matcher.group(1).replace('_', '-');
        }
        return "TK-MOCK-001";
    }
}
