package org.upyog.dx.upyogdata.controller;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.upyog.dx.upyogdata.pfms.exception.PfmsDataMappingException;
import org.upyog.dx.upyogdata.pfms.exception.PfmsKafkaPublishException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handling for PFMS REST APIs and shared infrastructure errors surfaced to HTTP clients.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean validation failures on {@code @Valid} request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        log.warn("PFMS request validation failed: {}", errors);
        return ResponseEntity.badRequest().body(Map.of("message", errors));
    }

    /**
     * Malformed JSON or unsupported field types (for example invalid date formats).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        log.warn("PFMS request body could not be read: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid request body: " + ex.getMostSpecificCause().getMessage()));
    }

    /**
     * Business rule violations such as duplicate vouchers during ingest.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("PFMS business validation failed: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    /**
     * Kafka publish failure after ingest validation and enrichment.
     */
    @ExceptionHandler(PfmsKafkaPublishException.class)
    public ResponseEntity<?> handleKafkaPublishFailure(PfmsKafkaPublishException ex) {
        log.error("PFMS Kafka publish failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Database mapping errors when reading PFMS rows (typically scheduler/forwarding path).
     */
    @ExceptionHandler(PfmsDataMappingException.class)
    public ResponseEntity<?> handleDataMappingFailure(PfmsDataMappingException ex) {
        log.error("PFMS data mapping failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to read PFMS transaction data"));
    }

    /**
     * JDBC or repository failures during duplicate checks or status updates.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(DataAccessException ex) {
        log.error("PFMS database error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Database error while processing PFMS request"));
    }

    /**
     * PFMS Auth or Data API errors from Feign clients on manual endpoints that call outbound APIs.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException ex) {
        log.error("PFMS external API error | status={} message={}", ex.status(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", "PFMS API error: " + ex.getMessage()));
    }

    /**
     * Fallback for unexpected runtime failures not mapped above.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        log.error("PFMS unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
    }
}
