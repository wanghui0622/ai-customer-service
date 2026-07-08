package com.aics.integrations.spi;

import java.util.Collections;
import java.util.Map;

public record IntegrationRequest(
        Map<String, String> pathParams,
        Map<String, String> queryParams,
        Object body
) {
    public IntegrationRequest {
        pathParams = pathParams == null ? Map.of() : Map.copyOf(pathParams);
        queryParams = queryParams == null ? Map.of() : Map.copyOf(queryParams);
    }

    public static IntegrationRequest empty() {
        return new IntegrationRequest(Collections.emptyMap(), Collections.emptyMap(), null);
    }

    public static IntegrationRequest ofPath(String key, String value) {
        return new IntegrationRequest(Map.of(key, value), Collections.emptyMap(), null);
    }

    public static IntegrationRequest ofBody(Object body) {
        return new IntegrationRequest(Collections.emptyMap(), Collections.emptyMap(), body);
    }
}
