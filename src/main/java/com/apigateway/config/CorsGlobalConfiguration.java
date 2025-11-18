package com.intranet.gateway.config;

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

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public GlobalFilter corsGlobalFilter() {
        return new GlobalFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();

                String origin = request.getHeaders().getOrigin();

                // Permitir estos orígenes
                if (origin != null &&
                        (origin.equals("https://intranet-frontend-seven.vercel.app") ||
                                origin.equals("http://localhost:4200"))) {

                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                            "Origin, Content-Type, Accept, Authorization, X-Requested-With, " +
                                    "Access-Control-Request-Method, Access-Control-Request-Headers");
                    headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Content-Type");

                    // Manejar preflight OPTIONS
                    if (request.getMethod() == HttpMethod.OPTIONS) {
                        response.setStatusCode(HttpStatus.OK);
                        return Mono.empty();
                    }
                }

                return chain.filter(exchange);
            }
        };
    }

    @Bean
    public CorsOrderedFilter corsOrderedFilter() {
        return new CorsOrderedFilter();
    }
    public static class CorsOrderedFilter implements GlobalFilter, Ordered {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();

            if (request.getMethod() == HttpMethod.OPTIONS) {
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();
                String origin = request.getHeaders().getOrigin();

                if (origin != null &&
                        (origin.equals("https://intranet-frontend-seven.vercel.app") ||
                                origin.equals("http://localhost:4200"))) {

                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");

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