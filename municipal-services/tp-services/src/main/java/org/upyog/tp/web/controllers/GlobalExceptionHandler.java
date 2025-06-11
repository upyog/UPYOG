package org.upyog.tp.web.controllers;
import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.UnknownHostException;
import java.util.List;

/**
 * GlobalExceptionHandler class for handling exceptions across the request-service application.
 * Provides consistent error responses for various exception scenarios.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Invalid JSON or Parsing Errors (HttpMessageNotReadableException).
     *
     * @param ex The HttpMessageNotReadableException.
     * @return ErrorResponse with BAD_REQUEST status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(400)
                .message("Invalid JSON format: Invalid JSON")
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Validation Errors (MethodArgumentNotValidException).
     *
     * @param ex The MethodArgumentNotValidException.
     * @return ResponseEntity with ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(400)
                .message("Validation failed: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Custom Exceptions (CustomException).
     *
     * @param ex The CustomException.
     * @return ResponseEntity with ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(400)
                .message(ex.getMessage())
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles External API 4xx Errors (Client Errors) (HttpClientErrorException).
     *
     * @param ex The HttpClientErrorException.
     * @return ErrorResponse with BAD_REQUEST status.
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(ex.getRawStatusCode())
                .message("Client error from external service: " + ex.getRawStatusCode() + " BAD_REQUEST")
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    /**
     * Handles External API 5xx Errors (Server Errors) (HttpServerErrorException).
     *
     * @param ex The HttpServerErrorException.
     * @return ErrorResponse with INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(ex.getRawStatusCode())
                .message("Server error from external service: " + ex.getRawStatusCode() + " INTERNAL_SERVER_ERROR")
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    /**
     * Handles Connection Issues (Timeouts, Network Issues) (ResourceAccessException).
     *
     * @param ex The ResourceAccessException.
     * @return ErrorResponse with SERVICE_UNAVAILABLE status.
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(503)
                .message("Service unavailable: " + ex.getMessage())
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles Unknown Host (Service Down or Incorrect URL) (UnknownHostException).
     *
     * @param ex The UnknownHostException.
     * @return ErrorResponse with SERVICE_UNAVAILABLE status.
     */
    @ExceptionHandler({UnknownHostException.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleUnknownHostException(Exception ex) {
        if (ex instanceof UnknownHostException || (ex instanceof RuntimeException && ex.getCause() instanceof UnknownHostException)) {
            UnknownHostException unknownHostEx = (ex instanceof UnknownHostException) ?
                    (UnknownHostException) ex :
                    (UnknownHostException) ex.getCause();
            ErrorResponse response = new ErrorResponse();
            Error error = Error.builder()
                    .code(503)
                    .message("Service unavailable: Unknown host - " + unknownHostEx.getMessage())
                    .build();
            response.setError(error);
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Handle other runtime exceptions
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(500)
                .message("Internal Server Error: " + ex.getMessage())
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles generic exceptions (Exception).
     *
     * @param ex The Exception.
     * @return ResponseEntity with ErrorResponse and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse();
        Error error = Error.builder()
                .code(500)
                .message("Internal Server Error: " + ex.getMessage())
                .build();
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}