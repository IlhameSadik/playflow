package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleBusinessException() {

        BusinessException exception =
                new BusinessException("Transaction not found");

        var response =
                handler.handleBusinessException(exception);

        assertEquals(400, response.getStatusCode().value());
    }
@Test
void shouldHandleEmailAlreadyExistsException() {

    EmailAlreadyExistsException exception =
            new EmailAlreadyExistsException("Email already exists");

    var response =
            handler.handleEmailAlreadyExists(exception);

    assertEquals(409, response.getStatusCode().value());
}
@Test
void shouldHandleValidationErrors() {

    var target = new Object();

    var fieldError =
            new org.springframework.validation.FieldError(
                    "request",
                    "amount",
                    "must be greater than 0"
            );

    var bindingResult =
            new org.springframework.validation.BeanPropertyBindingResult(
                    target,
                    "request"
            );

    bindingResult.addError(fieldError);

    var exception =
            new MethodArgumentNotValidException(
                    null,
                    bindingResult
            );

    var response =
            handler.handleValidationErrors(exception);

    assertEquals(400, response.getStatusCode().value());
    assertEquals(
            "Validation failed",
            response.getBody().get("message")
    );
}
@Test
void shouldReturnBusinessExceptionMessage() {

    BusinessException exception =
            new BusinessException("Transaction not found");

    var response =
            handler.handleBusinessException(exception);

    assertEquals(
            "Transaction not found",
            response.getBody().get("message")
    );
}
}