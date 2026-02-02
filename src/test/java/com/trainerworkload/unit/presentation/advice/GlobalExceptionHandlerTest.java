package com.trainerworkload.unit.presentation.advice;


import com.trainerworkload.domain.exception.EntityNotFoundException;
import com.trainerworkload.application.event.ActionType;
import com.trainerworkload.presentation.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import tools.jackson.databind.exc.InvalidFormatException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleEntityNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("User with id=5 not found");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody().get("error"));
        assertEquals("User with id=5 not found", response.getBody().get("message"));
    }

    @Test
    void shouldHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("year", "int");
        ResponseEntity<Map<String, String>> response = handler.handleMissingParams(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Missing required request parameter", body.get("error"));
        assertEquals("year", body.get("message"));
    }

    @Test
    void shouldHandleValidationErrors() {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "testObject");
        br.addError(new FieldError("testObject", "firstName",
                "First name cannot be blank"));

        br.addError(new FieldError("testObject", "date",
                "Training date cannot be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, br);
        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);
        Map<String, String> body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", body.get("error"));
        assertEquals("First name cannot be blank", body.get("firstName"));
        assertEquals("Training date cannot be blank", body.get("date"));
    }

    @Test
    void shouldHandleInvalidEnumValue() {
        InvalidFormatException cause = new InvalidFormatException(null, "Bad enum", "INVALID",
                ActionType.class);

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid", cause, null);
        ResponseEntity<Map<String, String>> response = handler.handleInvalidFormat(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid action type", response.getBody().get("error"));
    }

    @Test
    void shouldHandleInvalidJsonFormat() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Bad JSON", null);
        ResponseEntity<Map<String, String>> response = handler.handleInvalidFormat(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid format of data", response.getBody().get("error"));
    }

    @Test
    void shouldHandleUnexpectedException() {
        Exception ex = new RuntimeException("Boom");
        assertDoesNotThrow(() -> handler.handleUnexpected(ex));
    }
}

