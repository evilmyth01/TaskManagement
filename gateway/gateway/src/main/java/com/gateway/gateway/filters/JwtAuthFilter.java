package com.gateway.gateway.filters;

import com.gateway.gateway.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    JwtUtil jwtUtil;

    private static final List<String> WHITELIST = List.of(
            "/api/v1/auth", "/actuator"
    );

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info(
                "PATH RECEIVED = {}",
                exchange.getRequest().getURI().getPath()
        );

        log.info(
                "PATH RECEIVED = {}",
                exchange.getRequest().getURI().getPath()
        );

        String path = exchange.getRequest().getURI().getPath();

        if (WHITELIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing Authorization Header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, "Invalid or expired token");
        }

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-User-Id", String.valueOf(jwtUtil.extractUserId(token)))
                .header("X-User-Email", String.valueOf(jwtUtil.extractEmail(token)))
                .header("X-User-Role", String.valueOf(jwtUtil.extractRole(token)))
                .build();

        return chain.filter(
                exchange
                        .mutate()
                        .request(request)
                        .build()
        );
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = """
                {
                    "error": "Unauthorized",
                    "message": "%s",
                    "status": "401"
                }
                """.formatted(message);

        byte[] bytes = body.getBytes();

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
