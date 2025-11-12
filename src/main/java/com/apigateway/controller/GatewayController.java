package com.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    @Autowired
    private RouteLocator routeLocator;

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("API Gateway is running!"));
    }

    @GetMapping("/info")
    public Mono<ResponseEntity<Map<String, Object>>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "API Gateway");
        info.put("version", "1.0.0");
        info.put("status", "UP");
        info.put("port", 8080);
        info.put("routes", routeLocator.getRoutes().count().block());

        return Mono.just(ResponseEntity.ok(info));
    }

    @GetMapping("/routes")
    public Mono<ResponseEntity<Object>> routes() {
        return routeLocator.getRoutes()
                .collectList()
                .map(routes -> ResponseEntity.ok((Object) routes));
    }
}
