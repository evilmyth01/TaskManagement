package com.authService.authService.dto;

import com.authService.authService.enums.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        Role role
) {
}
