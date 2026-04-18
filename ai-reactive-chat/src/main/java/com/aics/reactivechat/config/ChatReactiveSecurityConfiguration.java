package com.aics.reactivechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * WebFlux 安全：默认放行（本地与内网联调）；生产可改为 OAuth2 Resource Server + JWT（需自行引入
 * {@code spring-boot-starter-oauth2-resource-server} 并配置 issuer）。
 */
@Configuration
@EnableWebFluxSecurity
public class ChatReactiveSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus")
                        .permitAll()
                        .anyExchange()
                        .permitAll())
                .build();
    }
}
