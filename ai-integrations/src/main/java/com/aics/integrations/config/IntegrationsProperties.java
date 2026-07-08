package com.aics.integrations.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "aics.integrations")
public class IntegrationsProperties {

    private Duration defaultTimeout = Duration.ofSeconds(5);
    private Map<String, ConnectorProperties> connectors = new LinkedHashMap<>();

    public Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(Duration defaultTimeout) {
        this.defaultTimeout = defaultTimeout != null ? defaultTimeout : Duration.ofSeconds(5);
    }

    public Map<String, ConnectorProperties> getConnectors() {
        return connectors;
    }

    public void setConnectors(Map<String, ConnectorProperties> connectors) {
        this.connectors = connectors != null ? connectors : new LinkedHashMap<>();
    }

    public static class ConnectorProperties {
        private String type = "rest";
        private String baseUrl = "";
        private AuthProperties auth = new AuthProperties();
        private Map<String, OperationProperties> operations = new LinkedHashMap<>();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public AuthProperties getAuth() {
            return auth;
        }

        public void setAuth(AuthProperties auth) {
            this.auth = auth != null ? auth : new AuthProperties();
        }

        public Map<String, OperationProperties> getOperations() {
            return operations;
        }

        public void setOperations(Map<String, OperationProperties> operations) {
            this.operations = operations != null ? operations : new LinkedHashMap<>();
        }
    }

    public static class AuthProperties {
        private String type = "none";
        private String token = "";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class OperationProperties {
        private String method = "GET";
        private String path = "/";

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
