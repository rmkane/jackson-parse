package com.example.jacksonparse.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleException() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/person");
        when(request.getContentType()).thenReturn("application/json");

        Exception exception = new RuntimeException("Test error message");
        ResponseEntity<String> response = handler.handleException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error: Test error message"));
        assertTrue(response.getBody().contains("Content-Type: application/json"));
    }

    @Test
    void testHandleExceptionWithNullContentType() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/person");
        when(request.getContentType()).thenReturn(null);

        Exception exception = new IllegalArgumentException("Invalid input");
        ResponseEntity<String> response = handler.handleException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error: Invalid input"));
        assertTrue(response.getBody().contains("Content-Type: null"));
    }

    @Test
    void testHandleExceptionWithNullMessage() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getContentType()).thenReturn("application/xml");

        Exception exception = new NullPointerException();
        ResponseEntity<String> response = handler.handleException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error: null"));
    }
}

