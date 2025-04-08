package org.upyog.sv.web.controllers;

import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.UnknownHostException;
import java.util.List;

/**
 * GlobalExceptionHandler class for handling exceptions across the application.
 * Provides consistent error responses for various exception scenarios.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Invalid JSON or Parsing Errors (HttpMessageNotReadableException).
     *
     * @param ex The HttpMessageNotReadableException.
     * @return ResponseEntity with ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        Error error = Error.builder()
                .code(400)
                .message("Malformed or incorrect JSON format")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().error(error).build());
    }

    /**
     * Handles MethodArgumentNotValidException for validation errors.
     *
     * @param ex The MethodArgumentNotValidException.
     * @return ResponseEntity with ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.isEmpty() ? 
            "Invalid input data" : 
            fieldErrors.get(0).getField() + " " + fieldErrors.get(0).getDefaultMessage();

        Error error = Error.builder()
                .code(400)
                .message(errorMessage)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().error(error).build());
    }

    /**
     * Handles External API 4xx Errors (Client Errors) (HttpClientErrorException).
     *
     * @param ex The HttpClientErrorException.
     * @return ResponseEntity with ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        Error error = Error.builder()
                .code(400)
                .message("Client error from external service: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().error(error).build());
    }

    /**
     * Handles External API 5xx Errors (Server Errors) (HttpServerErrorException).
     *
     * @param ex The HttpServerErrorException.
     * @return ResponseEntity with ErrorResponse and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex) {
        Error error = Error.builder()
                .code(500)
                .message("Server error from external service: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder().error(error).build());
    }

    /**
     * Handles Connection Issues (Timeouts, Network Issues) (ResourceAccessException).
     *
     * @param ex The ResourceAccessException.
     * @return ResponseEntity with ErrorResponse and SERVICE_UNAVAILABLE status.
     */
    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex) {
        Error error = Error.builder()
                .code(503)
                .message("Could not connect to external service. Please try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder().error(error).build());
    }

    /**
     * Handles Unknown Host (Service Down or Incorrect URL) (UnknownHostException).
     *
     * @param ex The UnknownHostException.
     * @return ResponseEntity with ErrorResponse and SERVICE_UNAVAILABLE status.
     */
    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleUnknownHostException(UnknownHostException ex) {
        Error error = Error.builder()
                .code(503)
                .message("External service is unreachable or DNS resolution failed.")
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder().error(error).build());
    }

    /**
     * Handles generic exceptions (Exception).
     *
     * @param ex The Exception.
     * @return ResponseEntity with ErrorResponse and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        Error error = Error.builder()
                .code(500)
                .message("An unexpected error occurred. Please contact support.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder().error(error).build());
    }
}