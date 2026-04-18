package com.aics.adminwebmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Servlet MVC 安全：默认放行；生产建议改为 JWT + 角色（{@code @PreAuthorize} 已预留开关）。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AdminSecurityConfiguration {

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus")
                        .permitAll()
                        .anyRequest()
                        .permitAll())
                .build();
    }
}
