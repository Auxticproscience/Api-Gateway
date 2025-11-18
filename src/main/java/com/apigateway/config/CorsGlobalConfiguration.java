package com.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsGlobalConfiguration {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "https://intranet-frontend-seven.vercel.app",
            "http://localhost:4200"
    );

    @Bean
    public GlobalFilter corsFilter() {
        return new GlobalFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();

                String origin = request.getHeaders().getOrigin();
                HttpMethod method = request.getMethod();

                if (origin != null && ALLOWED_ORIGINS.contains(origin)) {

                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                    headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Content-Type");

                    if (HttpMethod.OPTIONS.equals(method)) {
                        response.setStatusCode(HttpStatus.OK);
                        return Mono.empty();
                    }
                }

                return chain.filter(exchange);
            }
        };
    }

    @Bean
    public PreflightCorsFilter preflightCorsFilter() {
        return new PreflightCorsFilter();
    }

    public static class PreflightCorsFilter implements GlobalFilter, Ordered {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();

            if (HttpMethod.OPTIONS.equals(request.getMethod())) {
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();
                String origin = request.getHeaders().getOrigin();

                if (origin != null && ALLOWED_ORIGINS.contains(origin)) {

                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                            "Origin, Content-Type, Accept, Authorization, X-Requested-With, " +
                                    "Access-Control-Request-Method, Access-Control-Request-Headers");

                    response.setStatusCode(HttpStatus.OK);

                    return Mono.empty();
                }
            }

            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}