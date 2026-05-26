package com.analyticsService.Analytics.Service.advice;

import com.analyticsService.Analytics.Service.exceptions.ResourceNotFoundException;
import com.analyticsService.Analytics.Service.exceptions.UserNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentValidation(Exception exception) {
        ApiError error = ApiError.builder().status(HttpStatus.BAD_REQUEST).message(exception.getLocalizedMessage()).build();
        return buildErrorResponseEntity(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(Exception exception) {
        ApiError error = ApiError.builder().status(HttpStatus.NOT_FOUND).message(exception.getMessage()).build();
        return buildErrorResponseEntity(error);
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentials(HttpClientErrorException ex) {

        ProblemDetail detail = ex.getResponseBodyAs(ProblemDetail.class);
        ApiError error = ApiError.builder().status(HttpStatus.UNAUTHORIZED).message(ex.getMessage()).build();
        return buildErrorResponseEntity(error);

    }

    @ExceptionHandler(UserNotExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotExists(HttpClientErrorException ex) {

        ProblemDetail detail = ex.getResponseBodyAs(ProblemDetail.class);
        ApiError error = ApiError.builder().status(HttpStatus.BAD_REQUEST).message(ex.getMessage()).build();
        return buildErrorResponseEntity(error);

    }



    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError error) {
        return new ResponseEntity<>(new ApiResponse<>(error), error.getStatus());
    }
}
