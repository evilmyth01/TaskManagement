package com.gateway.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest req = exchange.getRequest();
        String method = req.getMethod().name();
        String path = req.getURI().getPath();

        String userId = req.getHeaders().getFirst("X-User-Id");

        log.info(
                "Incoming request -> Method: {}, Path: {}, UserId: {}",
                method,
                path,
                userId
        );

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .then(
                        Mono.fromRunnable(()->{
                            long timeTaken = System.currentTimeMillis() - startTime;

                            int status = Objects.requireNonNull(exchange.getResponse()
                                            .getStatusCode())
                                    .value();

                            log.info(
                                    "Outgoing response -> Status: {}, timeTaken: {}",
                                    status,
                                    timeTaken
                            );

                        })
                );



    }

    @Override
    public int getOrder() {
        return 0;
    }
}
