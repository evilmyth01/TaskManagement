package com.authService.authService.dto;

import com.authService.authService.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "Invalid Email")
        String email,

        @NotBlank(message = "password is required")
        String password,

        Role role
) {
}
