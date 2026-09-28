package com.televet.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFound(ResourceNotFoundException error, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, error.getMessage(), request);
    }

    @ExceptionHandler({DuplicateResourceException.class, DataIntegrityViolationException.class})
    public ResponseEntity<?> conflict(RuntimeException error, HttpServletRequest request) {
        String message = error instanceof DuplicateResourceException ? error.getMessage() : "A record with this unique value already exists";
        return response(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<?> badRequest(Exception error, HttpServletRequest request) {
        String message = error instanceof MethodArgumentNotValidException validation
                ? validation.getBindingResult().getFieldErrors().stream().findFirst().map(field -> field.getDefaultMessage()).orElse("Invalid request")
                : error.getMessage();
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<?> response(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now(), "status", status.value(), "message", message, "path", request.getRequestURI()));
    }
}