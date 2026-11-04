package org.upyog.externalaudit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.masking.SensitivePayloadMasker;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.model.ExternalApiAuditDetailWrapper;
import org.upyog.externalaudit.model.ExternalApiErrorDetails;
import org.upyog.externalaudit.producer.ExternalApiAuditProducer;
import org.upyog.externalaudit.producer.ExternalApiAuditPublisher;
import org.egov.tracer.constants.TracerConstants;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Explicit executor for third-party / external integrations.
 * <p>
 * Call sites wrap a {@link Supplier} around the inbound handler or outbound HTTP call.
 * The logger publishes two Kafka events on {@code external.api.audit.detail.topic}:
 * {@code INITIATED} before the supplier runs, then {@code SUCCESS} or {@code FAILED}.
 * egov-persister upserts those events into a single {@code ug_external_api_message_detail}
 * row keyed by {@code correlation_id}.
 * </p>
 * <p>
 * Kafka publish is asynchronous and fail-open: broker errors are logged and never
 * replace the original business exception. Do not use this for internal UPYOG APIs.
 * </p>
 *
 * @see ExternalApiAuditDetail
 * @see ExternalApiAuditProperties
 */
@Slf4j
@Component
@ConditionalOnBean(ExternalApiAuditProducer.class)
public class ExternalApiAuditLogger {

    private final ExternalApiAuditPublisher producer;
    private final ExternalApiAuditProperties properties;
    private final ObjectMapper objectMapper;
    private final SensitivePayloadMasker sensitivePayloadMasker;

    public ExternalApiAuditLogger(ExternalApiAuditPublisher producer,
            ExternalApiAuditProperties properties,
            ObjectMapper objectMapper,
            SensitivePayloadMasker sensitivePayloadMasker) {
        this.producer = producer;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.sensitivePayloadMasker = sensitivePayloadMasker;
    }

    /**
     * Audits an inbound call from an external system into UPYOG ({@code direction=INBOUND}).
     *
     * @param detail existing production audit model; a new audit UUID is generated when {@code correlationId} is blank
     * @param apiCall business handler; its return value is stored as the response payload
     * @return the supplier result
     */
    public <T> T logInboundApi(ExternalApiAuditDetail detail, Supplier<T> apiCall) {
        detail.setDirection(ExternalApiAuditConstants.DIRECTION_INBOUND);
        return execute(detail, apiCall);
    }

    /**
     * Audits an outbound call from UPYOG to a third party ({@code direction=OUTBOUND}).
     *
     * @param detail existing production audit model; reuse {@code correlationId} and increment {@code retryCount} for in-cycle retries
     * @param apiCall Feign / RestTemplate / SDK invocation
     * @return the supplier result
     */
    public <T> T logAndExecute(ExternalApiAuditDetail detail, Supplier<T> apiCall) {
        detail.setDirection(ExternalApiAuditConstants.DIRECTION_OUTBOUND);
        return execute(detail, apiCall);
    }

    /**
     * Inbound convenience overload. {@code correlationId} is stored as {@code originatingCorrelationId}.
     */
    public <T> T logInboundApi(String correlationId, String tenantId, String externalApiName,
            Object requestPayload, Supplier<T> apiCall) {
        return logInboundApi(ExternalApiAuditDetail.builder()
                .originatingCorrelationId(correlationId)
                .tenantId(tenantId)
                .externalApiName(externalApiName)
                .requestPayload(requestPayload)
                .build(), apiCall);
    }

    /**
     * Outbound convenience overload matching the ingest-style positional arguments.
     */
    public <T> T logAndExecute(String correlationId, String tenantId, String externalApiName,
            Object requestPayload, Supplier<T> apiCall) {
        return logAndExecute(ExternalApiAuditDetail.builder()
                .originatingCorrelationId(correlationId)
                .tenantId(tenantId)
                .externalApiName(externalApiName)
                .requestPayload(requestPayload)
                .build(), apiCall);
    }

    /**
     * Runs {@code apiCall} between INITIATED and SUCCESS/FAILED Kafka publishes.
     * Re-throws the original exception after the FAILED event is queued.
     */
    public <T> T execute(ExternalApiAuditDetail detail, Supplier<T> apiCall) {
        String correlationId = resolveAuditCorrelationId(detail);
        detail.setCorrelationId(correlationId);
        applyDefaults(detail);

        long requestTime = System.currentTimeMillis();
        publishRequestEvent(detail, requestTime);

        T response = null;
        Exception caughtException = null;
        try {
            response = apiCall.get();
            return response;
        } catch (Exception exception) {
            caughtException = exception;
            throw exception;
        } finally {
            publishResponseEvent(detail, requestTime, response, caughtException);
        }
    }

    private void applyDefaults(ExternalApiAuditDetail detail) {
        if (detail.getRetryCount() == null) {
            detail.setRetryCount(0);
        }
        if (isBlank(detail.getTenantId())) {
            detail.setTenantId(ExternalApiAuditConstants.DEFAULT_TENANT);
        }
        if (isBlank(detail.getOriginatingCorrelationId())) {
            String mdcCorrelationId = MDC.get(TracerConstants.CORRELATION_ID_MDC);
            if (!isBlank(mdcCorrelationId)) {
                detail.setOriginatingCorrelationId(mdcCorrelationId);
            }
        }
    }

    /**
     * Reuses the caller-supplied id (in-cycle retry) or allocates a new UUID (new logical request).
     */
    private String resolveAuditCorrelationId(ExternalApiAuditDetail detail) {
        if (detail.getCorrelationId() != null && !detail.getCorrelationId().isBlank()) {
            return detail.getCorrelationId();
        }
        return UUID.randomUUID().toString();
    }

    private void publishRequestEvent(ExternalApiAuditDetail detail, long requestTime) {
        long currentTime = System.currentTimeMillis();
        PayloadResult payloadResult = preparePayload(buildAuditablePayload(detail, detail.getRequestPayload()));

        ExternalApiAuditDetail requestEvent = copyMetadata(detail)
                .id(detail.getCorrelationId())
                .rawDetailId(UUID.randomUUID().toString())
                .requestTime(requestTime)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .createdTime(currentTime)
                .lastModifiedTime(currentTime)
                .requestPayload(payloadResult.payload())
                .payloadSizeBytes(payloadResult.sizeBytes())
                .build();

        publishSafely(requestEvent);
    }

    private void publishResponseEvent(ExternalApiAuditDetail detail, long requestTime, Object response,
            Exception caughtException) {
        long responseTime = System.currentTimeMillis();
        long durationMs = responseTime - requestTime;
        ResponseAuditDetails auditDetails = buildResponseAuditDetails(detail.getCorrelationId(), response, caughtException);
        PayloadResult requestPayloadResult = preparePayload(buildAuditablePayload(detail, detail.getRequestPayload()));
        PayloadResult responsePayloadResult = preparePayload(buildAuditablePayload(detail, auditDetails.responsePayload()));

        ExternalApiAuditDetail responseEvent = copyMetadata(detail)
                .id(detail.getCorrelationId())
                .requestTime(requestTime)
                .createdTime(requestTime)
                .status(auditDetails.status())
                .httpStatusCode(auditDetails.httpStatusCode())
                .responseTime(responseTime)
                .durationMs(durationMs)
                .lastModifiedTime(responseTime)
                .requestPayload(requestPayloadResult.payload())
                .responsePayload(responsePayloadResult.payload())
                .payloadSizeBytes(responsePayloadResult.sizeBytes())
                .errorDetails(auditDetails.errorDetails())
                .build();

        publishSafely(responseEvent);
    }

    private ExternalApiAuditDetail.ExternalApiAuditDetailBuilder copyMetadata(ExternalApiAuditDetail detail) {
        return ExternalApiAuditDetail.builder()
                .correlationId(detail.getCorrelationId())
                .tenantId(detail.getTenantId())
                .externalApiName(detail.getExternalApiName())
                .direction(detail.getDirection())
                .endpoint(detail.getEndpoint())
                .method(detail.getMethod())
                .originatingCorrelationId(detail.getOriginatingCorrelationId())
                .businessReferenceId(detail.getBusinessReferenceId())
                .retryCount(detail.getRetryCount());
    }

    /**
     * Fail-open: Kafka / serialization errors are logged and must not fail the business call.
     */
    private void publishSafely(ExternalApiAuditDetail event) {
        try {
            producer.publishAsync(properties.getDetailTopic(),
                    ExternalApiAuditDetailWrapper.builder().apiAuditDetail(event).build());
        } catch (Exception exception) {
            log.error("Failed to publish integration audit event to topic {}: {}",
                    properties.getDetailTopic(), exception.getMessage());
        }
    }

    /**
     * Envelope stored in {@code ug_external_api_message_raw_detail.request_payload}
     * and {@code response_payload}, including {@code endpoint} and {@code method}.
     */
    private Object buildAuditablePayload(ExternalApiAuditDetail detail, Object rawPayload) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        putIfPresent(envelope, "originatingCorrelationId", detail.getOriginatingCorrelationId());
        putIfPresent(envelope, "businessReferenceId", detail.getBusinessReferenceId());
        envelope.put(ExternalApiAuditConstants.ENVELOPE_ENDPOINT, detail.getEndpoint());
        envelope.put(ExternalApiAuditConstants.ENVELOPE_METHOD, detail.getMethod());
        if (properties.isCapturePayloadEnabled()) {
            envelope.put("payload", sensitivePayloadMasker.mask(rawPayload));
        } else {
            envelope.put("payloadCaptured", false);
        }
        return envelope;
    }

    private void putIfPresent(Map<String, Object> envelope, String key, String value) {
        if (!isBlank(value)) {
            envelope.put(key, value);
        }
    }

    private ResponseAuditDetails buildResponseAuditDetails(String correlationId, Object response,
            Exception caughtException) {
        if (caughtException == null) {
            return new ResponseAuditDetails(
                    ExternalApiAuditConstants.STATUS_SUCCESS,
                    extractSuccessHttpStatus(response),
                    extractSuccessBody(response),
                    null);
        }

        ExternalApiErrorDetails errorDetails = buildErrorDetails(caughtException);
        errorDetails.setCorrelationId(correlationId);
        return new ResponseAuditDetails(
                ExternalApiAuditConstants.STATUS_FAILED,
                extractHttpStatusCode(caughtException),
                extractErrorResponseBody(caughtException),
                errorDetails);
    }

    private Integer extractSuccessHttpStatus(Object response) {
        if (response instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCode().value();
        }
        return response != null ? 200 : null;
    }

    private Object extractSuccessBody(Object response) {
        if (response instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getBody();
        }
        return response;
    }

    private ExternalApiErrorDetails buildErrorDetails(Exception exception) {
        String errorCode = "INTEGRATION_CALL_FAILED";
        String errorType = ExternalApiAuditConstants.ERROR_TYPE_SERVER;
        String errorMessage = exception.getMessage();

        if (exception instanceof HttpClientErrorException clientErrorException) {
            errorType = ExternalApiAuditConstants.ERROR_TYPE_CLIENT;
            errorCode = "HTTP_CLIENT_ERROR";
            errorMessage = clientErrorException.getResponseBodyAsString();
        } else if (exception instanceof HttpServerErrorException serverErrorException) {
            errorType = ExternalApiAuditConstants.ERROR_TYPE_SERVER;
            errorCode = "HTTP_SERVER_ERROR";
            errorMessage = serverErrorException.getResponseBodyAsString();
        } else if (exception instanceof ResourceAccessException) {
            errorType = isTimeoutException(exception)
                    ? ExternalApiAuditConstants.ERROR_TYPE_TIMEOUT
                    : ExternalApiAuditConstants.ERROR_TYPE_NETWORK;
            errorCode = isTimeoutException(exception) ? "REQUEST_TIMEOUT" : "NETWORK_ERROR";
        } else if (isFeignException(exception)) {
            Integer status = invokeIntegerMethod(exception, "status");
            if (status != null && status >= 400 && status < 500) {
                errorType = ExternalApiAuditConstants.ERROR_TYPE_CLIENT;
                errorCode = "HTTP_CLIENT_ERROR";
            } else if (status != null && status >= 500) {
                errorType = ExternalApiAuditConstants.ERROR_TYPE_SERVER;
                errorCode = "HTTP_SERVER_ERROR";
            }
            String feignBody = invokeStringMethod(exception, "contentUTF8");
            if (!isBlank(feignBody)) {
                errorMessage = feignBody;
            }
        } else if (exception instanceof ServiceCallException) {
            errorType = ExternalApiAuditConstants.ERROR_TYPE_CLIENT;
            errorCode = "SERVICE_CALL_ERROR";
        } else if (exception instanceof CustomException customException) {
            errorType = ExternalApiAuditConstants.ERROR_TYPE_VALIDATION;
            errorCode = customException.getCode();
            errorMessage = customException.getMessage();
        }

        return ExternalApiErrorDetails.builder()
                .id(UUID.randomUUID().toString())
                .correlationId(null)
                .errorCode(errorCode)
                .errorType(errorType)
                .errorMessage(errorMessage)
                .createdTime(System.currentTimeMillis())
                .build();
    }

    private boolean isTimeoutException(Exception exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return exception.getMessage() != null && exception.getMessage().toLowerCase().contains("timed out");
    }

    private Integer extractHttpStatusCode(Exception exception) {
        if (exception instanceof HttpClientErrorException clientErrorException) {
            return clientErrorException.getStatusCode().value();
        }
        if (exception instanceof HttpServerErrorException serverErrorException) {
            return serverErrorException.getStatusCode().value();
        }
        Integer feignStatus = invokeIntegerMethod(exception, "status");
        if (isFeignException(exception) && feignStatus != null) {
            return feignStatus;
        }
        return 4094;
    }

    private Object extractErrorResponseBody(Exception exception) {
        if (exception instanceof HttpClientErrorException clientErrorException) {
            return clientErrorException.getResponseBodyAsString();
        }
        if (exception instanceof HttpServerErrorException serverErrorException) {
            return serverErrorException.getResponseBodyAsString();
        }
        if (isFeignException(exception)) {
            String feignBody = invokeStringMethod(exception, "contentUTF8");
            if (!isBlank(feignBody)) {
                return feignBody;
            }
        }
        if (exception instanceof ServiceCallException serviceCallException) {
            return serviceCallException.getMessage();
        }
        return exception.getMessage();
    }

    /**
     * Detects Feign failures by class name so this library does not need a hard Feign compile dependency.
     */
    private boolean isFeignException(Exception exception) {
        Class<?> type = exception.getClass();
        while (type != null) {
            if ("feign.FeignException".equals(type.getName())) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    private Integer invokeIntegerMethod(Exception exception, String methodName) {
        try {
            Method method = exception.getClass().getMethod(methodName);
            Object value = method.invoke(exception);
            if (value instanceof Integer integerValue) {
                return integerValue;
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private String invokeStringMethod(Exception exception, String methodName) {
        try {
            Method method = exception.getClass().getMethod(methodName);
            Object value = method.invoke(exception);
            return value != null ? value.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private PayloadResult preparePayload(Object payload) {
        if (payload == null) {
            return new PayloadResult(null, 0L);
        }
        try {
            byte[] payloadBytes = objectMapper.writeValueAsBytes(payload);
            long payloadSizeBytes = payloadBytes.length;
            if (payloadSizeBytes <= properties.getMaxPayloadBytes()) {
                return new PayloadResult(payload, payloadSizeBytes);
            }
            Map<String, Object> truncatedPayload = new LinkedHashMap<>();
            if (payload instanceof Map<?, ?> original) {
                truncatedPayload.put(ExternalApiAuditConstants.ENVELOPE_ENDPOINT,
                        original.get(ExternalApiAuditConstants.ENVELOPE_ENDPOINT));
                truncatedPayload.put(ExternalApiAuditConstants.ENVELOPE_HTTP_METHOD,
                        original.get(ExternalApiAuditConstants.ENVELOPE_HTTP_METHOD));
                truncatedPayload.put(ExternalApiAuditConstants.ENVELOPE_METHOD,
                        original.get(ExternalApiAuditConstants.ENVELOPE_METHOD));
            }
            truncatedPayload.put("truncated", true);
            truncatedPayload.put("originalSizeBytes", payloadSizeBytes);
            truncatedPayload.put("message", ExternalApiAuditConstants.PAYLOAD_TRUNCATED_MESSAGE);
            return new PayloadResult(truncatedPayload, payloadSizeBytes);
        } catch (Exception exception) {
            log.warn("Unable to serialize integration audit payload: {}", exception.getMessage());
            return new PayloadResult(payload, 0L);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ResponseAuditDetails(String status, Integer httpStatusCode, Object responsePayload,
            ExternalApiErrorDetails errorDetails) {
    }

    private record PayloadResult(Object payload, Long sizeBytes) {
    }
}
