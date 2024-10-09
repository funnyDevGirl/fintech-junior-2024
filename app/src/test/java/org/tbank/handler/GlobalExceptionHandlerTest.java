package org.tbank.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.tbank.exception.ResourceNotFoundException;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<String> response = handler.handleResourceNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Resource not found");
    }

    @Test
    void testHandleOtherExceptions() {
        Exception exception = new Exception("Something went wrong");

        ResponseEntity<String> response = handler.handleOtherExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal Server Error: Something went wrong");
    }
}
