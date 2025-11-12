package com.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;


@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-login", r -> r
                        .path("/api/auth/login")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("lb://auth-service"))

                .route("auth-validate", r -> r
                        .path("/api/auth/validate")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("lb://auth-service"))

                .route("auth-health", r -> r
                        .path("/api/auth/health")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://auth-service"))

                // Swagger/OpenAPI routes
                .route("auth-swagger-docs", r -> r
                        .path("/auth-service/api-docs")
                        .uri("lb://auth-service/api-docs"))

                .route("auth-swagger-ui", r -> r
                        .path("/auth-service/swagger-ui/**")
                        .uri("lb://auth-service"))
                .build();
    }
}