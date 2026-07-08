package com.aics.integrations.spi;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IntegrationRegistry {

    private final Map<String, IntegrationConnector> byId = new ConcurrentHashMap<>();

    public IntegrationRegistry(Collection<IntegrationConnector> connectors) {
        if (connectors != null) {
            for (IntegrationConnector connector : connectors) {
                IntegrationConnector prev = byId.putIfAbsent(connector.id(), connector);
                if (prev != null) {
                    throw new IllegalStateException("Duplicate integration connector: " + connector.id());
                }
            }
        }
    }

    public Optional<IntegrationConnector> get(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public IntegrationConnector require(String id) {
        return get(id).orElseThrow(() -> new IllegalStateException("Integration connector not found: " + id));
    }
}
