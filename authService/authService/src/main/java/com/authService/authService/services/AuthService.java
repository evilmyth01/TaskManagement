package com.authService.authService.services;

import com.authService.authService.dto.AuthResponse;
import com.authService.authService.dto.LoginRequest;
import com.authService.authService.dto.SignupRequest;
import com.authService.authService.dto.TokenValidationResponse;
import com.authService.authService.entities.User;
import com.authService.authService.enums.Role;
import com.authService.authService.exceptions.InvalidCredentialsException;
import com.authService.authService.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    JwtProvider jwtProvider;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public AuthResponse signup(SignupRequest req) {

        if(userRepository.existsByEmail(req.email())) {
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .email(req.email())
                .name(req.name())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role() != null ? req.role() : Role.USER)
                .build();

        User saved = userRepository.save(user);
        String token = jwtProvider.generateToken(saved);

        return toAuthResponse(saved, token);
    }

    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        return toAuthResponse(user, jwtProvider.generateToken(user));
    }

    public TokenValidationResponse validate(String token) {
        if (!jwtProvider.validateToken(token))
            throw new JwtException("Token invalid or expired");

        return new TokenValidationResponse(
                jwtProvider.extractUserId(token),
                jwtProvider.extractEmail(token),
                jwtProvider.extractRole(token),
                true
        );
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }


    public Boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public String getUserMail(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        return user.getEmail();
    }
}
