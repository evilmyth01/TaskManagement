package com.gateway.gateway.dto;

public record User(
        String name,
        String email,
        String password
) {
}
