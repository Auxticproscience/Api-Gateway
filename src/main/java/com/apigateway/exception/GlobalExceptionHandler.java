package com.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


@Component
@Order(-1)
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Gateway error occurred: ", ex);

        var response = exchange.getResponse();
        var request = exchange.getRequest();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if(ex instanceof ResponseStatusException) {
            status = HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
        } else if (ex instanceof ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else if (ex instanceof TimeoutException) {
            status = HttpStatus.REQUEST_TIMEOUT;
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", getErrorMessage(ex, status));
        errorResponse.put("path", request.getPath().value());

        try {
            byte [] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("Error creating JSON response", e);
            return response.setComplete();
        }
    }

    private Object getErrorMessage(Throwable ex, HttpStatus status) {
        switch (status) {
            case SERVICE_UNAVAILABLE:
                return "Servicio temporalmente no disponible. Intente más tarde";
            case REQUEST_TIMEOUT:
                return "Tiempo de espera agotado. El servicio esta tardando más de lo habitual";
            case NOT_FOUND:
                return "Recurso no encontrado";
            case UNAUTHORIZED:
                return "No autorizado. Token JWT requerido.";
            case FORBIDDEN:
                return "Acceso denegado. Permisos insuficientes.";
            default:
                return ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";
        }
    }
}