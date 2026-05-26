package com.authService.authService.controllers;

import com.authService.authService.dto.AuthResponse;
import com.authService.authService.dto.LoginRequest;
import com.authService.authService.dto.SignupRequest;
import com.authService.authService.dto.TokenValidationResponse;
import com.authService.authService.services.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return ResponseEntity.ok(authService.validate(token));
    }

    @GetMapping("/{userId}/exists")
    public Boolean userExists(@PathVariable Long userId) {
        return authService.userExists(userId);
    }

    @GetMapping("/{userId}/email")
    public String userMail(@PathVariable Long userId) {
        return authService.getUserMail(userId);
    }

}
