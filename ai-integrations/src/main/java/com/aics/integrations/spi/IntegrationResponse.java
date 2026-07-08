package com.aics.integrations.spi;

public record IntegrationResponse(int status, String body, boolean success) {
    public static IntegrationResponse ok(String body) {
        return new IntegrationResponse(200, body == null ? "" : body, true);
    }

    public static IntegrationResponse error(int status, String body) {
        return new IntegrationResponse(status, body == null ? "" : body, false);
    }
}
