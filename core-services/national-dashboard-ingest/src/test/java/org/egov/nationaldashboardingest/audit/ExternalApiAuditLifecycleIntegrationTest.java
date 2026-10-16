package org.egov.nationaldashboardingest.audit;

import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.job.ExternalApiAuditCleanupJob;
import org.upyog.externalaudit.job.ExternalApiAuditReconciliationJob;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.model.ExternalApiErrorDetails;
import org.egov.nationaldashboardingest.audit.support.ExternalApiAuditTableHarness;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalApiAuditLifecycleIntegrationTest {

    private static ExternalApiAuditTableHarness harness;

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
    }

    @Test
    void twoPhaseEvents_upsertToOneRow() {
        String correlationId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .retryCount(0)
                .requestPayload(Map.of("phase", "request"))
                .build());
        assertEquals(1, harness.messageCount());
        assertEquals(ExternalApiAuditConstants.STATUS_INITIATED, harness.message(correlationId).get("status"));

        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_SUCCESS)
                .httpStatusCode(200)
                .responseTime(now + 25)
                .durationMs(25L)
                .retryCount(0)
                .requestPayload(Map.of("phase", "request"))
                .responsePayload(Map.of("phase", "response"))
                .build());

        assertEquals(1, harness.messageCount());
        assertEquals(1, harness.rawCount());
        Map<String, Object> message = harness.message(correlationId);
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, message.get("status"));
        assertEquals(200, ((Number) message.get("http_status_code")).intValue());
        String responsePayload = String.valueOf(harness.raw(correlationId).get("response_payload"));
        assertEquals(true, responsePayload.contains("response"));
    }

    @Test
    void successStatusIsStickyUnlessRetryCountIncreases() {
        String correlationId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_SUCCESS)
                .httpStatusCode(200)
                .retryCount(0)
                .build());
        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .retryCount(0)
                .build());

        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, harness.message(correlationId).get("status"));
        assertEquals(200, ((Number) harness.message(correlationId).get("http_status_code")).intValue());
    }

    @Test
    void higherRetryCount_canOverwriteFailedStatus() {
        String correlationId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_FAILED)
                .httpStatusCode(401)
                .retryCount(0)
                .errorDetails(ExternalApiErrorDetails.builder()
                        .id(UUID.randomUUID().toString())
                        .correlationId(correlationId)
                        .errorCode("HTTP_CLIENT_ERROR")
                        .errorType(ExternalApiAuditConstants.ERROR_TYPE_CLIENT)
                        .errorMessage("Unauthorized")
                        .createdTime(now)
                        .build())
                .build());
        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .retryCount(1)
                .build());
        harness.persistDetail(baseDetail(correlationId, now)
                .status(ExternalApiAuditConstants.STATUS_SUCCESS)
                .httpStatusCode(200)
                .retryCount(1)
                .build());

        Map<String, Object> message = harness.message(correlationId);
        assertEquals(1, harness.messageCount());
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, message.get("status"));
        assertEquals(1, ((Number) message.get("retry_count")).intValue());
        assertEquals(1, harness.errorCount());
    }

    @Test
    void reconciliationJob_marksOnlyStaleInitiatedRowsTimedOut() {
        long now = System.currentTimeMillis();
        String staleId = UUID.randomUUID().toString();
        String freshId = UUID.randomUUID().toString();
        String successId = UUID.randomUUID().toString();

        harness.persistDetail(baseDetail(staleId, now - 700_000)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .build());
        harness.persistDetail(baseDetail(freshId, now - 10_000)
                .status(ExternalApiAuditConstants.STATUS_INITIATED)
                .build());
        harness.persistDetail(baseDetail(successId, now - 700_000)
                .status(ExternalApiAuditConstants.STATUS_SUCCESS)
                .httpStatusCode(200)
                .build());

        ExternalApiAuditProperties properties = new ExternalApiAuditProperties();
        properties.setStaleThresholdMs(600_000);
        ExternalApiAuditReconciliationJob job =
                new ExternalApiAuditReconciliationJob(harness.repository(), properties);
        job.reconcileStaleRequests();

        assertEquals(ExternalApiAuditConstants.STATUS_TIMED_OUT, harness.message(staleId).get("status"));
        assertEquals(ExternalApiAuditConstants.STATUS_INITIATED, harness.message(freshId).get("status"));
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, harness.message(successId).get("status"));
    }

    @Test
    void cleanupJob_deletesExpiredRowsFromAllThreeTables() {
        long now = System.currentTimeMillis();
        String oldId = UUID.randomUUID().toString();
        String newId = UUID.randomUUID().toString();
        harness.persistDetail(baseDetail(oldId, now - 40L * 24 * 60 * 60 * 1000)
                .status(ExternalApiAuditConstants.STATUS_FAILED)
                .errorDetails(ExternalApiErrorDetails.builder()
                        .id(UUID.randomUUID().toString())
                        .correlationId(oldId)
                        .errorCode("OLD")
                        .errorType(ExternalApiAuditConstants.ERROR_TYPE_SERVER)
                        .errorMessage("expired")
                        .createdTime(now - 40L * 24 * 60 * 60 * 1000)
                        .build())
                .build());
        harness.persistDetail(baseDetail(newId, now)
                .status(ExternalApiAuditConstants.STATUS_SUCCESS)
                .httpStatusCode(200)
                .build());

        ExternalApiAuditProperties properties = new ExternalApiAuditProperties();
        properties.getCleanup().setRetentionMs(30L * 24 * 60 * 60 * 1000);
        ExternalApiAuditCleanupJob job = new ExternalApiAuditCleanupJob(harness.repository(), properties);
        job.deleteExpiredRecords();

        assertEquals(1, harness.messageCount());
        assertEquals(1, harness.rawCount());
        assertEquals(0, harness.errorCount());
        assertEquals(newId, harness.onlyCorrelationId());
    }

    private ExternalApiAuditDetail.ExternalApiAuditDetailBuilder baseDetail(String correlationId, long time) {
        return ExternalApiAuditDetail.builder()
                .id(correlationId)
                .rawDetailId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .tenantId("pb.amritsar")
                .externalApiName(ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST)
                .direction(ExternalApiAuditConstants.DIRECTION_INBOUND)
                .requestTime(time)
                .createdTime(time)
                .lastModifiedTime(time)
                .retryCount(0)
                .requestPayload(Map.of("sample", true));
    }
}
