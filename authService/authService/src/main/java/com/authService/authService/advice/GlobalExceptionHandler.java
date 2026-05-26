package com.authService.authService.advice;

import com.authService.authService.exceptions.InvalidCredentialsException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidMethodArgument(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "error", ex.getMessage(),
                                "body", ex.getBody(),
                                "message", Objects.requireNonNull(Objects.requireNonNull(ex.getFieldError()).getDefaultMessage())
                        )
                );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleInvalidJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "error", ex.getMessage(),
                                "message", ex.getLocalizedMessage()
                        )
                );
    }
}
