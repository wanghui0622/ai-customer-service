package com.aics.integrations.domain.ticket;

public record CreateTicketRequest(String subject, String description, String priority) {
    public CreateTicketRequest {
        subject = subject == null ? "" : subject.trim();
        description = description == null ? "" : description.trim();
        priority = priority == null ? "normal" : priority.trim();
    }
}
