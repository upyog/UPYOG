package org.egov.nationaldashboardingest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.utils.ExternalApiAuditConstants;
import org.egov.nationaldashboardingest.web.models.*;
import org.egov.tracer.constants.TracerConstants;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Publishes non-blocking, two-phase audit-log events for every inbound/outbound call this
 * service makes to or receives from an external system (e.g. National Dashboard state
 * submissions), so both the request and the eventual response/error can be reconstructed
 * later purely from Kafka-persisted rows.
 *
 * <h2>How it works</h2>
 * <ol>
 *   <li><b>Phase 1 - request.</b> Before the wrapped call executes, a {@code correlationId}
 *       is resolved (see {@link #resolveCorrelationId}) and an {@link ExternalApiRequestEvent}
 *       carrying the request payload, tenant, API name, direction, and a
 *       {@code STATUS_INITIATED} status is published to the "request initiated" topic.
 *       Downstream (e.g. egov-persister), this is inserted as a new row keyed on
 *       {@code correlationId}.</li>
 *   <li><b>Call.</b> The supplied {@link Supplier} is invoked to perform the actual work.
 *       Its result or thrown exception is captured but never swallowed - the original
 *       response is returned and the original exception is rethrown to the caller
 *       unchanged. This class only observes the call; it never alters its outcome.</li>
 *   <li><b>Phase 2 - response.</b> In a {@code finally} block (so it runs whether the call
 *       succeeded, returned an HTTP error, or threw), an {@link ExternalApiResponseEvent} is
 *       built - status (SUCCESS/FAILED), HTTP status code, duration, response payload or
 *       {@link ExternalApiErrorDetails} - and published to the "response received" topic,
 *       keyed on the same {@code correlationId} so the consumer can UPDATE the row created
 *       in phase 1 instead of inserting a duplicate.</li>
 * </ol>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li><b>Correlation id reuse.</b> Callers may pass an explicit {@code correlationId}
 *       (e.g. one already generated for an inbound request) so that audit rows line up with
 *       other logs/traces for the same request instead of minting an unrelated id. See
 *       {@link #resolveCorrelationId} for the exact precedence.</li>
 *   <li><b>Non-blocking / fail-open.</b> {@link #publishSafely} swallows and logs any
 *       failure to publish to Kafka. A broken audit pipeline must never break or delay the
 *       real external call it is observing.</li>
 *   <li><b>Payload size guard.</b> {@link #preparePayload} serializes each payload to
 *       measure its size; anything larger than {@code integrationAuditMaxPayloadBytes} is
 *       replaced with a small "truncated" marker object instead of being logged in full, to
 *       keep Kafka messages and downstream rows bounded.</li>
 *   <li><b>Error classification.</b> {@link #buildErrorDetails} maps common Spring
 *       exception types (client 4xx, server 5xx, network/timeout, service-call, validation)
 *       to a coarse {@code errorType} and {@code errorCode} so failures can be aggregated by
 *       category downstream.</li>
 *   <li><b>Stale requests are out of scope here.</b> If the process crashes or the call
 *       hangs indefinitely between phase 1 and phase 2, no response event is ever published
 *       and the row stays at {@code STATUS_INITIATED} forever. Detecting and flipping such
 *       rows to a timed-out state is the job of a separate scheduled reconciliation job, not
 *       this class.</li>
 * </ul>
 *
 * @see #logAndExecute(String, String, String, Object, Supplier) for outbound calls this service makes
 * @see #logInboundApi(String, String, String, Object, Supplier) for inbound calls this service receives
 */
@Component
@Slf4j
public class ExternalApiAuditLogger {

    @Autowired
    private Producer producer;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Wraps an inbound API call (a request this service receives and handles) with the
     * two-phase audit logging described in the class Javadoc, tagging the resulting events
     * with {@code DIRECTION_INBOUND}.
     *
     * @param correlationId an explicit correlation id to reuse for this call, or
     *        {@code null}/blank to have one resolved automatically - see
     *        {@link #resolveCorrelationId}
     * @param tenantId the tenant the request is associated with
     * @param externalApiName a stable identifier for which API this is, used to group and
     *        filter audit records (e.g. {@code "national-dashboard-submit"})
     * @param requestPayload the raw request payload to persist for audit purposes; may be
     *        truncated if it exceeds the configured size limit
     * @param apiCall the actual handling logic to execute and time; its return value passes
     *        through unchanged, and any exception it throws propagates to the caller after
     *        the phase-2 audit event has been published
     * @return whatever {@code apiCall} returns
     */
    public <T> ResponseEntity<T> logInboundApi(String correlationId, String tenantId, String externalApiName,
                                               Object requestPayload, Supplier<ResponseEntity<T>> apiCall) {
        return logApiCall(resolveCorrelationId(correlationId), tenantId, externalApiName, requestPayload,
                ExternalApiAuditConstants.DIRECTION_INBOUND, apiCall);
    }

    /**
     * Wraps an outbound API call (a call this service makes to an external system) with the
     * two-phase audit logging described in the class Javadoc, tagging the resulting events
     * with {@code DIRECTION_OUTBOUND}.
     *
     * @param correlationId an explicit correlation id to reuse for this call, or
     *        {@code null}/blank to have one resolved automatically - see
     *        {@link #resolveCorrelationId}
     * @param tenantId the tenant the call is being made on behalf of
     * @param externalApiName a stable identifier for which external API is being called,
     *        used to group and filter audit records
     * @param requestPayload the raw request payload to persist for audit purposes; may be
     *        truncated if it exceeds the configured size limit
     * @param apiCall the actual outbound call to execute and time; its return value passes
     *        through unchanged, and any exception it throws propagates to the caller after
     *        the phase-2 audit event has been published
     * @return whatever {@code apiCall} returns
     */
    public <T> ResponseEntity<T> logAndExecute(String correlationId, String tenantId, String externalApiName,
                                               Object requestPayload, Supplier<ResponseEntity<T>> apiCall) {
        return logApiCall(resolveCorrelationId(correlationId), tenantId, externalApiName, requestPayload,
                ExternalApiAuditConstants.DIRECTION_OUTBOUND, apiCall);
    }

    /**
     * Picks the correlation id to use for this audit trail, in priority order:
     * <ol>
     *   <li>the {@code correlationId} explicitly passed by the caller, if non-blank - lets a
     *       caller that already has one (e.g. from an inbound request it is handling) thread
     *       it through so the audit rows can be joined back to that request;</li>
     *   <li>otherwise, whatever is currently in the SLF4J {@link MDC} under
     *       {@link TracerConstants#CORRELATION_ID_MDC} - this is typically set upstream by
     *       tracer/filter infrastructure for the current request thread, so reusing it means
     *       audit rows share the same id as the regular application logs for that request;</li>
     *   <li>otherwise, a freshly generated random UUID, for calls with no request-scoped
     *       correlation id available (e.g. a scheduled/background job).</li>
     * </ol>
     */
    private String resolveCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.isBlank()) {
            return correlationId;
        }
        String mdcCorrelationId = MDC.get(TracerConstants.CORRELATION_ID_MDC);
        if (mdcCorrelationId != null && !mdcCorrelationId.isBlank()) {
            return mdcCorrelationId;
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Shared implementation behind {@link #logInboundApi} and {@link #logAndExecute}:
     * publishes the phase-1 request event using the already-resolved {@code correlationId},
     * invokes {@code apiCall}, and publishes the phase-2 response event in a {@code finally}
     * block regardless of outcome (success, HTTP error response, or thrown exception).
     */
    private <T> ResponseEntity<T> logApiCall(String correlationId, String tenantId, String externalApiName,
                                             Object requestPayload, String direction, Supplier<ResponseEntity<T>> apiCall) {
        long requestTime = System.currentTimeMillis();
        publishRequestEvent(correlationId, tenantId, externalApiName, direction, requestTime, requestPayload);

        ResponseEntity<T> response = null;
        Exception caughtException = null;
        try {
            response = apiCall.get();
            return response;
        } catch (Exception exception) {
            caughtException = exception;
            throw exception;
        } finally {
            publishResponseEvent(correlationId, requestTime, response, caughtException);
        }
    }

    /**
     * Builds and publishes the phase-1 {@link ExternalApiRequestEvent} for a single call,
     * capturing the request payload (subject to the size guard in {@link #preparePayload})
     * and marking the row {@code STATUS_INITIATED}.
     */
    private void publishRequestEvent(String correlationId, String tenantId, String externalApiName, String direction,
                                     long requestTime, Object requestPayload) {
        long currentTime = System.currentTimeMillis();
        PayloadResult payloadResult = preparePayload(requestPayload);

        ExternalApiRequestEvent requestEvent = ExternalApiRequestEvent.builder()
                .id(correlationId)
                .rawDetailId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .tenantId(tenantId)
                .externalApiName(externalApiName)
                .direction(direction)
                .requestTime(requestTime)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .retryCount(0)
                .createdTime(currentTime)
                .lastModifiedTime(currentTime)
                .requestPayload(payloadResult.payload())
                .payloadSizeBytes(payloadResult.sizeBytes())
                .build();

        publishSafely(applicationProperties.getIntegrationRequestInitiatedTopic(),
                ExternalApiRequestEventWrapper.builder().apiRequestEvent(requestEvent).build());
    }

    /**
     * Builds and publishes the phase-2 {@link ExternalApiResponseEvent} for a single call,
     * computing {@code durationMs} from the phase-1 {@code requestTime}, and carrying either
     * the successful response payload or the classified {@link ExternalApiErrorDetails},
     * depending on whether {@code caughtException} is non-null.
     */
    private void publishResponseEvent(String correlationId, long requestTime, ResponseEntity<?> response,
                                      Exception caughtException) {
        long responseTime = System.currentTimeMillis();
        long durationMs = responseTime - requestTime;
        ResponseAuditDetails auditDetails = buildResponseAuditDetails(correlationId, response, caughtException);
        PayloadResult payloadResult = preparePayload(auditDetails.responsePayload());

        ExternalApiResponseEvent responseEvent = ExternalApiResponseEvent.builder()
                .correlationId(correlationId)
                .status(auditDetails.status())
                .httpStatusCode(auditDetails.httpStatusCode())
                .responseTime(responseTime)
                .durationMs(durationMs)
                .lastModifiedTime(responseTime)
                .responsePayload(payloadResult.payload())
                .payloadSizeBytes(payloadResult.sizeBytes())
                .errorDetails(auditDetails.errorDetails())
                .build();

        publishSafely(applicationProperties.getIntegrationResponseReceivedTopic(),
                ExternalApiResponseEventWrapper.builder().apiResponseEvent(responseEvent).build());
    }

    /**
     * Derives the outcome of a call - status, HTTP status code, response body to log, and
     * error details if applicable - from either a successful {@link ResponseEntity} or a
     * caught exception. Exactly one of {@code response} / {@code caughtException} is expected
     * to be meaningful for a given call.
     */
    private ResponseAuditDetails buildResponseAuditDetails(String correlationId, ResponseEntity<?> response,
                                                           Exception caughtException) {
        if (caughtException == null) {
            return new ResponseAuditDetails(
                    ExternalApiAuditConstants.STATUS_SUCCESS,
                    response != null ? response.getStatusCode().value() : null,
                    response != null ? response.getBody() : null,
                    null);
        }

        ExternalApiErrorDetails errorDetails = buildErrorDetails(caughtException);
        if (errorDetails != null) {
            errorDetails.setCorrelationId(correlationId);
        }
        Integer httpStatusCode = extractHttpStatusCode(caughtException);
        return new ResponseAuditDetails(
                ExternalApiAuditConstants.STATUS_FAILED,
                httpStatusCode,
                extractErrorResponseBody(caughtException),
                errorDetails);
    }

    /**
     * Classifies a thrown exception into a coarse {@code errorType}
     * (client/server/network/timeout/validation) with a corresponding {@code errorCode} and
     * {@code errorMessage}, based on common Spring {@code RestTemplate} and DIGIT/egov
     * exception types. Falls back to a generic {@code INTEGRATION_CALL_FAILED} / server-type
     * classification for anything unrecognized.
     */
    private ExternalApiErrorDetails buildErrorDetails(Exception exception) {
        String errorCode = "INTEGRATION_CALL_FAILED";
        String errorType = ExternalApiAuditConstants.ERROR_TYPE_SERVER;
        String errorMessage = exception.getMessage();

        if (exception instanceof HttpClientErrorException clientErrorException) {
            // 4xx from the external system - treat as a client-side error.
            errorType = ExternalApiAuditConstants.ERROR_TYPE_CLIENT;
            errorCode = "HTTP_CLIENT_ERROR";
            errorMessage = clientErrorException.getResponseBodyAsString();
        } else if (exception instanceof HttpServerErrorException serverErrorException) {
            // 5xx from the external system - treat as a server-side error.
            errorType = ExternalApiAuditConstants.ERROR_TYPE_SERVER;
            errorCode = "HTTP_SERVER_ERROR";
            errorMessage = serverErrorException.getResponseBodyAsString();
        } else if (exception instanceof ResourceAccessException) {
            // Connection-level failure - disambiguate timeout vs. other network errors.
            errorType = isTimeoutException(exception)
                    ? ExternalApiAuditConstants.ERROR_TYPE_TIMEOUT
                    : ExternalApiAuditConstants.ERROR_TYPE_NETWORK;
            errorCode = isTimeoutException(exception) ? "REQUEST_TIMEOUT" : "NETWORK_ERROR";
        } else if (exception instanceof ServiceCallException) {
            // DIGIT/egov convention for a failed downstream service call.
            errorType = ExternalApiAuditConstants.ERROR_TYPE_CLIENT;
            errorCode = "SERVICE_CALL_ERROR";
        } else if (exception instanceof CustomException customException) {
            // Application-level validation failure - carries its own code/message.
            errorType = ExternalApiAuditConstants.ERROR_TYPE_VALIDATION;
            errorCode = customException.getCode();
            errorMessage = customException.getMessage();
        }
        // Anything else falls through to the generic INTEGRATION_CALL_FAILED / SERVER
        // classification set above.

        return ExternalApiErrorDetails.builder()
                .id(UUID.randomUUID().toString())
                .correlationId(null)
                .errorCode(errorCode)
                .errorType(errorType)
                .errorMessage(errorMessage)
                .createdTime(System.currentTimeMillis())
                .build();
    }

    /**
     * Walks the exception's cause chain looking for a {@link SocketTimeoutException},
     * falling back to a case-insensitive substring check on the message, to distinguish a
     * timeout from other network errors wrapped in a {@link ResourceAccessException}.
     */
    private boolean isTimeoutException(Exception exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        // Fallback for cases where the timeout isn't surfaced as a SocketTimeoutException.
        return exception.getMessage() != null && exception.getMessage().toLowerCase().contains("timed out");
    }

    /**
     * Extracts the HTTP status code from a client or server HTTP exception, or {@code null}
     * if the exception does not carry an HTTP status (e.g. a network/timeout error).
     */
    private Integer extractHttpStatusCode(Exception exception) {
        if (exception instanceof HttpClientErrorException clientErrorException) {
            return clientErrorException.getStatusCode().value();
        }
        if (exception instanceof HttpServerErrorException serverErrorException) {
            return serverErrorException.getStatusCode().value();
        }
        // Custom/application exception
        if (exception != null) {
            return 4094;
        }
        return null;
    }

    /**
     * Extracts a best-effort representation of the error response body to log, preferring
     * the raw response body string for HTTP exceptions and falling back to the exception
     * message otherwise.
     */
    private Object extractErrorResponseBody(Exception exception) {
        if (exception instanceof HttpClientErrorException clientErrorException) {
            return clientErrorException.getResponseBodyAsString();
        }
        if (exception instanceof HttpServerErrorException serverErrorException) {
            return serverErrorException.getResponseBodyAsString();
        }
        if (exception instanceof ServiceCallException serviceCallException) {
            return serviceCallException.getMessage();
        }
        return exception.getMessage();
    }

    /**
     * Serializes {@code payload} to measure its size, returning it unchanged if within
     * {@code integrationAuditMaxPayloadBytes}. Oversized payloads are replaced with a small
     * marker object noting the original size, so a single large body never bloats the audit
     * log. If serialization itself fails, the original payload is passed through as-is with
     * a reported size of {@code 0} and a warning is logged.
     *
     * @return a {@link PayloadResult} pairing the (possibly truncated) payload with its
     *         measured size in bytes
     */
    private PayloadResult preparePayload(Object payload) {
        if (payload == null) {
            return new PayloadResult(null, 0L);
        }

        try {
            byte[] payloadBytes = objectMapper.writeValueAsBytes(payload);
            long payloadSizeBytes = payloadBytes.length;
            if (payloadSizeBytes <= applicationProperties.getIntegrationAuditMaxPayloadBytes()) {
                return new PayloadResult(payload, payloadSizeBytes);
            }

            // Payload exceeds the configured limit - log a small marker instead of the body.
            Map<String, Object> truncatedPayload = new HashMap<>();
            truncatedPayload.put("truncated", true);
            truncatedPayload.put("originalSizeBytes", payloadSizeBytes);
            truncatedPayload.put("message", ExternalApiAuditConstants.PAYLOAD_TRUNCATED_MESSAGE);
            return new PayloadResult(truncatedPayload, payloadSizeBytes);
        } catch (Exception exception) {
            // Never let a serialization failure break audit logging or the caller's flow.
            log.warn("Unable to serialize integration audit payload: {}", exception.getMessage());
            return new PayloadResult(payload, 0L);
        }
    }

    /**
     * Publishes {@code event} to {@code topic}, catching and logging (rather than
     * propagating) any failure. Audit logging must never cause the real API call it is
     * observing to fail or be delayed - this is the fail-open boundary that guarantees that.
     */
    private void publishSafely(String topic, Object event) {
        try {
            log.info("Publishing integration audit event to topic {}: {}", topic, event);
            producer.push(topic, event);
        } catch (Exception exception) {
            log.error("Failed to publish integration audit event to topic {}: {}", topic, exception.getMessage());
        }
    }

    /** Outcome of a single call, ready to populate an {@link ExternalApiResponseEvent}. */
    private record ResponseAuditDetails(String status, Integer httpStatusCode, Object responsePayload,
                                        ExternalApiErrorDetails errorDetails) {
    }

    /**
     * A payload paired with its serialized size in bytes, after the size guard in
     * {@link #preparePayload} has been applied.
     */
    private record PayloadResult(Object payload, Long sizeBytes) {
    }
}