package org.upyog.cdwm.web.controllers;

import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
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
     * @return ErrorResponse with BAD_REQUEST status.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleJsonParseException(HttpMessageNotReadableException ex) {
        return buildErrorResponse("INVALID_JSON", "Malformed or incorrect JSON format", HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles External API 4xx Errors (Client Errors) (HttpClientErrorException).
     *
     * @param ex The HttpClientErrorException.
     * @return ErrorResponse with BAD_REQUEST status.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.class)
    public ErrorResponse handleHttpClientErrorException(HttpClientErrorException ex) {
        return buildErrorResponse("EXTERNAL_API_ERROR", "Client error from external service: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles External API 5xx Errors (Server Errors) (HttpServerErrorException).
     *
     * @param ex The HttpServerErrorException.
     * @return ErrorResponse with INTERNAL_SERVER_ERROR status.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpServerErrorException.class)
    public ErrorResponse handleHttpServerErrorException(HttpServerErrorException ex) {
        return buildErrorResponse("EXTERNAL_API_FAILURE", "Server error from external service: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles Connection Issues (Timeouts, Network Issues) (ResourceAccessException).
     *
     * @param ex The ResourceAccessException.
     * @return ErrorResponse with SERVICE_UNAVAILABLE status.
     */
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ResourceAccessException.class)
    public ErrorResponse handleResourceAccessException(ResourceAccessException ex) {
        return buildErrorResponse("EXTERNAL_SERVICE_UNREACHABLE", "Could not connect to external service. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles Unknown Host (Service Down or Incorrect URL) (UnknownHostException).
     *
     * @param ex The UnknownHostException.
     * @return ErrorResponse with SERVICE_UNAVAILABLE status.
     */
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(UnknownHostException.class)
    public ErrorResponse handleUnknownHostException(UnknownHostException ex) {
        return buildErrorResponse("SERVICE_UNAVAILABLE", "External service is unreachable or DNS resolution failed.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles MethodArgumentNotValidException for validation errors.
     *
     * @param ex The MethodArgumentNotValidException.
     * @return ResponseEntity with ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = "Invalid input data";
        if (!fieldErrors.isEmpty()) {
            errorMessage = fieldErrors.get(0).getDefaultMessage();
        }

        Error error = Error.builder().message(errorMessage).build();
        ErrorResponse errorResponse = ErrorResponse.builder().error(error).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions (Exception).
     *
     * @param ex The Exception.
     * @return ResponseEntity with ErrorResponse and INTERNAL_SERVER_ERROR status.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = buildErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please contact support.", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to build ErrorResponse.
     *
     * @param code    The error code.
     * @param message The error message.
     * @param status  The HttpStatus.
     * @return ErrorResponse object.
     */
    private ErrorResponse buildErrorResponse(String code, String message, HttpStatus status) {
        Error error = new Error();
        error.setCode(status.value());
        error.setMessage(message);

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setStatus(status.toString());
        responseInfo.setTs(System.currentTimeMillis());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setResponseInfo(responseInfo);
        errorResponse.setError(error);

        return errorResponse;
    }
}