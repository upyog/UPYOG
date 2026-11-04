package org.egov.nationaldashboardingest.audit;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.egov.nationaldashboardingest.audit.support.ExternalApiAuditTableHarness;
import org.egov.nationaldashboardingest.service.IngestService;
import org.egov.nationaldashboardingest.utils.ResponseInfoFactory;
import org.egov.nationaldashboardingest.web.controllers.MetricIngestController;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRequest;
import org.egov.nationaldashboardingest.web.models.IngestResponse;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetricIngestAuditIntegrationTest {

    private static ExternalApiAuditTableHarness harness;

    private IngestService ingestService;
    private MetricIngestController controller;

    @BeforeAll
    static void startDatabase() {
        harness = ExternalApiAuditTableHarness.start();
    }

    @AfterAll
    static void stopDatabase() {
        harness.close();
    }

    @BeforeEach
    void setUp() {
        harness.truncate();
        ingestService = mock(IngestService.class);
        controller = controller(harness.newLogger(true));
    }

    @Test
    void inboundMetricIngestSuccess_writesOneSuccessRowWithPayloads() {
        when(ingestService.ingestData(any())).thenReturn(List.of(101, 202));

        ResponseEntity<IngestResponse> response = controller.create(sampleRequest("origin-1", "pb.amritsar"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, harness.messageCount());
        assertEquals(1, harness.rawCount());
        assertEquals(0, harness.errorCount());

        String correlationId = harness.onlyCorrelationId();
        Map<String, Object> message = harness.message(correlationId);
        assertEquals("pb.amritsar", message.get("tenant_id"));
        assertEquals(ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST, message.get("external_api_name"));
        assertEquals(ExternalApiAuditConstants.DIRECTION_INBOUND, message.get("direction"));
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, message.get("status"));
        assertEquals(200, ((Number) message.get("http_status_code")).intValue());
        assertEquals(0, ((Number) message.get("retry_count")).intValue());
        assertNotEquals("origin-1", correlationId);

        Map<String, Object> raw = harness.raw(correlationId);
        String requestPayload = String.valueOf(raw.get("request_payload"));
        String responsePayload = String.valueOf(raw.get("response_payload"));
        assertTrue(requestPayload.contains("origin-1"));
        assertTrue(requestPayload.contains("/national-dashboard/metric/_ingest"));
        assertTrue(requestPayload.contains("\"endpoint\""));
        assertTrue(requestPayload.contains("\"httpMethod\"") || requestPayload.contains("\"method\""));
        assertTrue(requestPayload.contains("POST"));
        assertTrue(requestPayload.contains("********"));
        assertFalse(requestPayload.contains("super-secret-token"));
        assertTrue(responsePayload.contains("101"));
        assertTrue(responsePayload.contains("202"));
    }

    @Test
    void inboundMetricIngestFailure_writesFailedRowAndErrorDetail() {
        when(ingestService.ingestData(any())).thenThrow(new CustomException("INGEST_FAILED", "elasticsearch unavailable"));

        CustomException thrown = assertThrows(CustomException.class,
                () -> controller.create(sampleRequest("origin-fail", "pb.jalandhar")));

        assertEquals("INGEST_FAILED", thrown.getCode());
        assertEquals(1, harness.messageCount());
        assertEquals(1, harness.errorCount());

        String correlationId = harness.onlyCorrelationId();
        Map<String, Object> message = harness.message(correlationId);
        assertEquals(ExternalApiAuditConstants.STATUS_FAILED, message.get("status"));
        assertEquals("pb.jalandhar", message.get("tenant_id"));

        Map<String, Object> error = harness.errors(correlationId).get(0);
        assertEquals("INGEST_FAILED", error.get("error_code"));
        assertEquals(ExternalApiAuditConstants.ERROR_TYPE_VALIDATION, error.get("error_type"));
        assertEquals("elasticsearch unavailable", error.get("error_message"));
    }

    @Test
    void inboundClientError_classifiesAsClientFailure() {
        when(ingestService.ingestData(any())).thenThrow(HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", null, "invalid module".getBytes(), null));

        assertThrows(HttpClientErrorException.class,
                () -> controller.create(sampleRequest("origin-4xx", "pb.ludhiana")));

        String correlationId = harness.onlyCorrelationId();
        Map<String, Object> message = harness.message(correlationId);
        assertEquals(ExternalApiAuditConstants.STATUS_FAILED, message.get("status"));
        assertEquals(400, ((Number) message.get("http_status_code")).intValue());
        Map<String, Object> error = harness.errors(correlationId).get(0);
        assertEquals("HTTP_CLIENT_ERROR", error.get("error_code"));
        assertEquals(ExternalApiAuditConstants.ERROR_TYPE_CLIENT, error.get("error_type"));
        assertEquals("invalid module", error.get("error_message"));
    }

    @Test
    void twoSeparateIngestCalls_writeTwoRowsEvenWithSameOriginatingId() {
        when(ingestService.ingestData(any())).thenReturn(List.of(1));

        controller.create(sampleRequest("shared-origin", "pb.amritsar"));
        controller.create(sampleRequest("shared-origin", "pb.amritsar"));

        assertEquals(2, harness.messageCount());
        assertEquals(2, harness.rawCount());
        List<Map<String, Object>> rows = harness.messagesByApi(
                ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST);
        assertNotEquals(rows.get(0).get("correlation_id"), rows.get(1).get("correlation_id"));
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, rows.get(0).get("status"));
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, rows.get(1).get("status"));
    }

    @Test
    void payloadCaptureDisabled_storesEnvelopeWithoutBody() {
        controller = controller(harness.newLogger(false));
        when(ingestService.ingestData(any())).thenReturn(List.of(9));

        controller.create(sampleRequest("origin-off", "pb.patiala"));

        String requestPayload = String.valueOf(harness.raw(harness.onlyCorrelationId()).get("request_payload"));
        assertTrue(requestPayload.contains("\"payloadCaptured\":false")
                || requestPayload.contains("\"payloadCaptured\": false"));
        assertFalse(requestPayload.contains("pb.patiala"));
        assertFalse(requestPayload.contains("super-secret-token"));
    }

    @Test
    void kafkaDown_doesNotFailBusinessCallAndWritesNoRows() {
        controller = controller(harness.newLogger(true, harness.throwingPublisher()));
        when(ingestService.ingestData(any())).thenReturn(List.of(7));

        ResponseEntity<IngestResponse> response = controller.create(sampleRequest("origin-kafka", "pb.mohali"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(7), response.getBody().getResponseHash());
        assertEquals(0, harness.messageCount());
        assertEquals(0, harness.rawCount());
        assertEquals(0, harness.errorCount());
    }

    private MetricIngestController controller(ExternalApiAuditLogger logger) {
        MetricIngestController metricIngestController = new MetricIngestController();
        ReflectionTestUtils.setField(metricIngestController, "ingestService", ingestService);
        ReflectionTestUtils.setField(metricIngestController, "responseInfoFactory", new ResponseInfoFactory());
        ReflectionTestUtils.setField(metricIngestController, "integrationAuditLogger", logger);
        return metricIngestController;
    }

    private IngestRequest sampleRequest(String originatingCorrelationId, String ulb) {
        HashMap<String, Object> metrics = new HashMap<>();
        metrics.put("transactions", 12);
        Data data = Data.builder()
                .date("05-10-2026")
                .module("PT")
                .ward("W1")
                .ulb(ulb)
                .region("North")
                .state("Punjab")
                .metrics(metrics)
                .build();
        RequestInfo requestInfo = RequestInfo.builder()
                .correlationId(originatingCorrelationId)
                .authToken("super-secret-token")
                .userInfo(User.builder().tenantId("pb").uuid("user-1").build())
                .build();
        return IngestRequest.builder()
                .requestInfo(requestInfo)
                .ingestData(List.of(data))
                .build();
    }
}
