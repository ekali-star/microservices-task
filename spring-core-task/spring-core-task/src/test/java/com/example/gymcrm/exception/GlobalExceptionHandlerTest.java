package com.example.gymcrm.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // ── MissingRequestHeaderException ────────────────────────────────────────

    @Test
    void handleMissingRequestHeader_ShouldReturn400() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("Authorization");

        ResponseEntity<Map<String, String>> response = handler.handleMissingRequestHeader(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleMissingRequestHeader_ShouldIncludeHeaderNameInMessage() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("X-Custom-Header");

        ResponseEntity<Map<String, String>> response = handler.handleMissingRequestHeader(ex);

        assertThat(response.getBody()).containsEntry("message", "Required header 'X-Custom-Header' is missing");
    }

    @Test
    void handleMissingRequestHeader_ShouldIncludeStatus400InBody() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("Authorization");

        ResponseEntity<Map<String, String>> response = handler.handleMissingRequestHeader(ex);

        assertThat(response.getBody()).containsEntry("status", "400");
    }

    // ── HttpRequestMethodNotSupportedException ───────────────────────────────

    @Test
    void handleMethodNotSupported_ShouldReturn405() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");

        ResponseEntity<Map<String, String>> response = handler.handleMethodNotSupported(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void handleMethodNotSupported_ShouldIncludeMethodInMessage() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PATCH");

        ResponseEntity<Map<String, String>> response = handler.handleMethodNotSupported(ex);

        assertThat(response.getBody()).containsEntry("message",
                "HTTP method 'PATCH' is not supported for this endpoint");
    }

    @Test
    void handleMethodNotSupported_ShouldIncludeStatus405InBody() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PUT");

        ResponseEntity<Map<String, String>> response = handler.handleMethodNotSupported(ex);

        assertThat(response.getBody()).containsEntry("status", "405");
    }

    // ── MethodArgumentNotValidException ──────────────────────────────────────

    @Test
    void handleValidationExceptions_ShouldReturn400() {
        MethodArgumentNotValidException ex = buildValidationException("username", "must not be blank");

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleValidationExceptions_ShouldIncludeFieldErrors() {
        MethodArgumentNotValidException ex = buildValidationException("email", "must be a valid email");

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getBody()).containsEntry("email", "must be a valid email");
    }

    @Test
    void handleValidationExceptions_ShouldIncludeGenericMessageAndStatus() {
        MethodArgumentNotValidException ex = buildValidationException("name", "must not be null");

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getBody()).containsEntry("message", "Validation failed");
        assertThat(response.getBody()).containsEntry("status", "400");
    }

    @Test
    void handleValidationExceptions_ShouldIncludeMultipleFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "firstName", "must not be blank"));
        bindingResult.addError(new FieldError("target", "lastName", "must not be blank"));

        MethodParameter param = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getBody())
                .containsEntry("firstName", "must not be blank")
                .containsEntry("lastName", "must not be blank");
    }

    // ── MethodArgumentTypeMismatchException ──────────────────────────────────

    @Test
    void handleTypeMismatch_ShouldReturn400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");

        ResponseEntity<Map<String, String>> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleTypeMismatch_ShouldIncludeParameterNameInMessage() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("typeId");

        ResponseEntity<Map<String, String>> response = handler.handleTypeMismatch(ex);

        assertThat(response.getBody()).containsEntry("message", "Parameter 'typeId' has invalid value");
    }

    @Test
    void handleTypeMismatch_ShouldIncludeStatus400InBody() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");

        ResponseEntity<Map<String, String>> response = handler.handleTypeMismatch(ex);

        assertThat(response.getBody()).containsEntry("status", "400");
    }

    // ── MissingServletRequestParameterException ──────────────────────────────

    @Test
    void handleMissingParams_ShouldReturn400() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("from", "LocalDate");

        ResponseEntity<Map<String, String>> response = handler.handleMissingParams(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleMissingParams_ShouldIncludeParameterNameInMessage() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("traineeUsername", "String");

        ResponseEntity<Map<String, String>> response = handler.handleMissingParams(ex);

        assertThat(response.getBody())
                .containsEntry("message", "Required parameter 'traineeUsername' is missing");
    }

    @Test
    void handleMissingParams_ShouldIncludeStatus400InBody() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("page", "int");

        ResponseEntity<Map<String, String>> response = handler.handleMissingParams(ex);

        assertThat(response.getBody()).containsEntry("status", "400");
    }

    // ── Generic Exception ────────────────────────────────────────────────────

    @Test
    void handleGenericException_ShouldReturn500() {
        Exception ex = new Exception("something went wrong");

        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleGenericException_ShouldReturnGenericMessage() {
        Exception ex = new RuntimeException("db connection lost");

        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        assertThat(response.getBody()).containsEntry("message", "An unexpected error occurred");
    }

    @Test
    void handleGenericException_ShouldIncludeStatus500InBody() {
        Exception ex = new IllegalStateException("unexpected state");

        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        assertThat(response.getBody()).containsEntry("status", "500");
    }

    @Test
    void handleGenericException_ShouldNotExposeExceptionMessage() {
        Exception ex = new RuntimeException("sensitive internal detail");

        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        assertThat(response.getBody()).doesNotContainValue("sensitive internal detail");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private MethodArgumentNotValidException buildValidationException(String field, String message) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", field, message));
        MethodParameter param = mock(MethodParameter.class);
        return new MethodArgumentNotValidException(param, bindingResult);
    }
}