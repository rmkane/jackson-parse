package com.example.jacksonparse.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e, HttpServletRequest request) {
        log.error("Error processing request: {} {} - {}", request.getMethod(), request.getRequestURI(), e.getMessage(),
                e);
        return ResponseEntity.badRequest()
                .body("Error: " + e.getMessage() + " (Content-Type: " + request.getContentType() + ")");
    }
}
