package com.aics.reactivechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 可选共享密钥（Header {@code X-API-Key}）；未配置时不生效。
 * 可与后续 OAuth2/JWT 并存时调整顺序与规则。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OptionalApiKeyWebFilter implements WebFilter {

    @Value("${aics.security.api-key:}")
    private String expectedKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (expectedKey == null || expectedKey.isBlank()) {
            return chain.filter(exchange);
        }
        String provided = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        if (expectedKey.equals(provided)) {
            return chain.filter(exchange);
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
