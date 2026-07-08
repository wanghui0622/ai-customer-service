package com.aics.integrations.spi;

import java.util.Map;

public record IntegrationOperation(String method, String path) {
    public IntegrationOperation {
        method = method == null ? "GET" : method.trim().toUpperCase();
        path = path == null ? "/" : path.trim();
    }
}
