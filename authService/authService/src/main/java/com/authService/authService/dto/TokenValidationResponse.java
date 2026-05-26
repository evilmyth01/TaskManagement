package com.authService.authService.dto;

public record TokenValidationResponse(
        Long userId,
        String email,
        String role,
        boolean valid
) {
}
