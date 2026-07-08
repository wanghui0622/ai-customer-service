package com.aics.integrations.connector;

import com.aics.integrations.config.IntegrationsProperties;
import com.aics.integrations.spi.IntegrationConnector;
import com.aics.integrations.spi.IntegrationOperation;
import com.aics.integrations.spi.IntegrationRequest;
import com.aics.integrations.spi.IntegrationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用 REST 连接器：通过 YAML 配置 operation 映射，零代码扩展外部 API。
 */
public class RestIntegrationConnector implements IntegrationConnector {

    private final String id;
    private final IntegrationsProperties.ConnectorProperties config;
    private final Duration timeout;
    private final WebClient webClient;
    private final Map<String, IntegrationOperation> operations = new HashMap<>();

    public RestIntegrationConnector(String id,
                                    IntegrationsProperties.ConnectorProperties config,
                                    Duration timeout,
                                    WebClient.Builder webClientBuilder) {
        this.id = id;
        this.config = config;
        this.timeout = timeout;
        this.webClient = webClientBuilder.baseUrl(config.getBaseUrl()).build();
        config.getOperations().forEach((name, op) ->
                operations.put(name, new IntegrationOperation(op.getMethod(), op.getPath())));
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String type() {
        return "rest";
    }

    @Override
    public boolean supports(String operation) {
        return operations.containsKey(operation);
    }

    @Override
    public IntegrationResponse execute(String operation, IntegrationRequest request) {
        IntegrationOperation op = operations.get(operation);
        if (op == null) {
            return IntegrationResponse.error(404, "unsupported operation: " + operation);
        }
        String path = resolvePath(op.path(), request.pathParams());
        try {
            WebClient.RequestBodySpec spec = webClient.method(
                            org.springframework.http.HttpMethod.valueOf(op.method()))
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path(path);
                        request.queryParams().forEach(builder::queryParam);
                        return builder.build();
                    })
                    .headers(this::applyAuth);
            String body;
            if (request.body() != null && !"GET".equalsIgnoreCase(op.method())) {
                body = spec.contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request.body())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(timeout);
            } else {
                body = spec.retrieve().bodyToMono(String.class).block(timeout);
            }
            return IntegrationResponse.ok(body == null ? "" : body);
        } catch (WebClientResponseException ex) {
            return IntegrationResponse.error(ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            return IntegrationResponse.error(500, ex.getMessage());
        }
    }

    private void applyAuth(HttpHeaders headers) {
        IntegrationsProperties.AuthProperties auth = config.getAuth();
        if ("bearer".equalsIgnoreCase(auth.getType()) && auth.getToken() != null && !auth.getToken().isBlank()) {
            headers.setBearerAuth(auth.getToken());
        }
    }

    private static String resolvePath(String template, Map<String, String> params) {
        String resolved = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resolved;
    }
}
