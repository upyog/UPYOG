package org.upyog.externalaudit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.masking.SensitivePayloadMasker;
import org.upyog.externalaudit.model.ExternalApiAuditDetailWrapper;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.producer.ExternalApiAuditPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalApiAuditLoggerTest {

    private RecordingPublisher publisher;
    private ExternalApiAuditProperties properties;
    private ExternalApiAuditLogger logger;

    @BeforeEach
    void setUp() {
        publisher = new RecordingPublisher();
        properties = new ExternalApiAuditProperties();
        properties.setDetailTopic("external-api-audit-details");
        properties.setCapturePayloadEnabled(true);
        logger = new ExternalApiAuditLogger(publisher, properties, new ObjectMapper(),
                new SensitivePayloadMasker(new ObjectMapper(), properties));
    }

    @Test
    void execute_shouldPublishMatchingCorrelationIdsOnSuccess() {
        String body = logger.logAndExecute(ExternalApiAuditDetail.builder()
                .tenantId("PFMS")
                .externalApiName(ExternalApiAuditConstants.API_PFMS_DATA_PUSH)
                .requestPayload(Map.of("voucherNumber", "V1"))
                .originatingCorrelationId("business-corr")
                .endpoint("http://pfms.example/data")
                .method("POST")
                .build(), () -> "ok");

        assertEquals(2, publisher.events.size());
        ExternalApiAuditDetailWrapper requestWrapper = (ExternalApiAuditDetailWrapper) publisher.events.get(0);
        ExternalApiAuditDetailWrapper responseWrapper = (ExternalApiAuditDetailWrapper) publisher.events.get(1);

        assertEquals("PFMS", requestWrapper.getApiAuditDetail().getTenantId());
        assertEquals("http://pfms.example/data", requestWrapper.getApiAuditDetail().getEndpoint());
        assertEquals("POST", requestWrapper.getApiAuditDetail().getMethod());
        assertEquals(ExternalApiAuditConstants.API_PFMS_DATA_PUSH,
                requestWrapper.getApiAuditDetail().getExternalApiName());
        assertEquals(ExternalApiAuditConstants.DIRECTION_OUTBOUND,
                requestWrapper.getApiAuditDetail().getDirection());
        assertEquals(requestWrapper.getApiAuditDetail().getCorrelationId(),
                responseWrapper.getApiAuditDetail().getCorrelationId());
        assertNotEquals("business-corr", requestWrapper.getApiAuditDetail().getCorrelationId());
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, responseWrapper.getApiAuditDetail().getStatus());
        assertEquals(200, responseWrapper.getApiAuditDetail().getHttpStatusCode());
        assertEquals("ok", body);

        @SuppressWarnings("unchecked")
        Map<String, Object> requestEnvelope =
                (Map<String, Object>) requestWrapper.getApiAuditDetail().getRequestPayload();
        assertEquals("http://pfms.example/data", requestEnvelope.get(ExternalApiAuditConstants.ENVELOPE_ENDPOINT));
        assertEquals("POST", requestEnvelope.get(ExternalApiAuditConstants.ENVELOPE_METHOD));

        @SuppressWarnings("unchecked")
        Map<String, Object> responseEnvelope =
                (Map<String, Object>) responseWrapper.getApiAuditDetail().getResponsePayload();
        assertEquals("http://pfms.example/data", responseEnvelope.get(ExternalApiAuditConstants.ENVELOPE_ENDPOINT));
        assertEquals("POST", responseEnvelope.get(ExternalApiAuditConstants.ENVELOPE_METHOD));
    }

    @Test
    void logInboundApi_shouldRethrowAndPublishFailure() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", null, "invalid request".getBytes(), null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> logger.logInboundApi(
                "origin-id",
                "pb",
                ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST,
                Map.of("request", "payload"),
                () -> {
                    throw exception;
                }));

        assertEquals(exception, thrown);
        assertEquals(2, publisher.events.size());
        ExternalApiAuditDetailWrapper responseWrapper = (ExternalApiAuditDetailWrapper) publisher.events.get(1);
        assertEquals(ExternalApiAuditConstants.STATUS_FAILED, responseWrapper.getApiAuditDetail().getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseWrapper.getApiAuditDetail().getHttpStatusCode());
        assertNotNull(responseWrapper.getApiAuditDetail().getErrorDetails());
        assertEquals(ExternalApiAuditConstants.ERROR_TYPE_CLIENT,
                responseWrapper.getApiAuditDetail().getErrorDetails().getErrorType());
    }

    @Test
    void execute_shouldNotFailBusinessCallWhenKafkaPublishFails() {
        publisher.failure = new RuntimeException("kafka down");

        String result = logger.logAndExecute(ExternalApiAuditDetail.builder()
                .tenantId("PFMS")
                .externalApiName(ExternalApiAuditConstants.API_PFMS_AUTH)
                .requestPayload(Map.of("Password", "secret"))
                .build(), () -> "token");

        assertEquals("token", result);
    }

    @Test
    void execute_shouldOmitPayloadWhenCaptureDisabled() {
        properties.setCapturePayloadEnabled(false);

        logger.logAndExecute(ExternalApiAuditDetail.builder()
                .tenantId("PFMS")
                .externalApiName(ExternalApiAuditConstants.API_PFMS_AUTH)
                .requestPayload(Map.of("Password", "secret"))
                .build(), () -> Map.of("AccessToken", "abc"));

        ExternalApiAuditDetailWrapper requestWrapper = (ExternalApiAuditDetailWrapper) publisher.events.get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> envelope = (Map<String, Object>) requestWrapper.getApiAuditDetail().getRequestPayload();
        assertEquals(false, envelope.get("payloadCaptured"));
        assertTrue(!envelope.containsKey("payload"));
        assertTrue(envelope.containsKey(ExternalApiAuditConstants.ENVELOPE_ENDPOINT));
        assertTrue(envelope.containsKey(ExternalApiAuditConstants.ENVELOPE_METHOD));
    }

    @Test
    void execute_shouldReuseCorrelationIdOnRetry() {
        String auditId = "11111111-1111-1111-1111-111111111111";

        logger.logAndExecute(ExternalApiAuditDetail.builder()
                .correlationId(auditId)
                .retryCount(1)
                .tenantId("PFMS")
                .externalApiName(ExternalApiAuditConstants.API_PFMS_DATA_PUSH)
                .requestPayload(Map.of("voucherNumber", "V1"))
                .build(), () -> "ok");

        ExternalApiAuditDetailWrapper responseWrapper = (ExternalApiAuditDetailWrapper) publisher.events.get(1);
        assertEquals(auditId, responseWrapper.getApiAuditDetail().getCorrelationId());
        assertEquals(1, responseWrapper.getApiAuditDetail().getRetryCount());
    }

    private static class RecordingPublisher implements ExternalApiAuditPublisher {
        private final List<Object> events = new ArrayList<>();
        private RuntimeException failure;

        @Override
        public void publishAsync(String topic, Object event) {
            if (failure != null) {
                throw failure;
            }
            events.add(event);
        }
    }
}
