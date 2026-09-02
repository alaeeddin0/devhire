package com.example.devhire.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception) {
        return buildError(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException exception) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> validationErrors.put(
                error.getField(),
                error.getDefaultMessage()));

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Les données envoyées sont invalides.",
                validationErrors);
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            Map<String, String> validationErrors) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                validationErrors);

        return ResponseEntity.status(status).body(apiError);
    }
}
