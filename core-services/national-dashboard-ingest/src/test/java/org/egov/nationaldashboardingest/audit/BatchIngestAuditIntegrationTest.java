package org.egov.nationaldashboardingest.audit;

import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.audit.support.ExternalApiAuditTableHarness;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.service.IngestService;
import org.egov.nationaldashboardingest.service.impl.BatchIngestionProcessorImpl;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRowData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BatchIngestAuditIntegrationTest {

    private static ExternalApiAuditTableHarness harness;

    private IngestService ingestService;
    private BatchIngestionProcessorImpl processor;

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
        processor = new BatchIngestionProcessorImpl();
        ReflectionTestUtils.setField(processor, "ingestService", ingestService);
        ReflectionTestUtils.setField(processor, "integrationAuditLogger",
                harness.newLogger(true));
        ReflectionTestUtils.setField(processor, "applicationProperties", new ApplicationProperties());
    }

    @Test
    void eachExcelRow_getsItsOwnAuditRow() {
        when(ingestService.ingestData(any())).thenReturn(List.of(1));

        List<Map<String, Object>> failures = new ArrayList<>();
        processor.processBatchWithFallback(List.of(
                row("pb.amritsar", "05-10-2026"),
                row("pb.jalandhar", "06-10-2026")), RequestInfo.builder().build(), failures);

        assertEquals(0, failures.size());
        assertEquals(2, harness.messageCount());
        List<Map<String, Object>> rows = harness.jdbc().queryForList("""
                SELECT correlation_id, tenant_id, status
                FROM ug_external_api_message_detail
                ORDER BY tenant_id
                """);
        assertEquals("pb.amritsar", rows.get(0).get("tenant_id"));
        assertEquals("pb.jalandhar", rows.get(1).get("tenant_id"));
        assertEquals("SUCCESS", rows.get(0).get("status"));
        assertEquals("SUCCESS", rows.get(1).get("status"));
        assertNotEquals(rows.get(0).get("correlation_id"), rows.get(1).get("correlation_id"));

        String requestPayload = String.valueOf(
                harness.raw(String.valueOf(rows.get(0).get("correlation_id"))).get("request_payload"));
        assertTrue(requestPayload.contains("/national-dashboard/bulk/v1/_init"));
        assertTrue(requestPayload.contains("\"method\""));
        assertTrue(requestPayload.contains("POST"));
    }

    @Test
    void mixedBatch_writesSuccessAndFailedRowsWithoutAbortingRemainingRows() {
        when(ingestService.ingestData(any()))
                .thenReturn(List.of(1))
                .thenThrow(new RuntimeException("row 2 exploded"))
                .thenReturn(List.of(3));

        List<Map<String, Object>> failures = new ArrayList<>();
        processor.processBatchWithFallback(List.of(
                row("pb.amritsar", "05-10-2026"),
                row("pb.jalandhar", "06-10-2026"),
                row("pb.ludhiana", "07-10-2026")), RequestInfo.builder().build(), failures);

        assertEquals(1, failures.size());
        assertEquals("pb.jalandhar", failures.get(0).get("ulb"));
        assertEquals(3, harness.messageCount());
        assertEquals(1, harness.errorCount());

        Map<String, String> statusByTenant = new HashMap<>();
        harness.jdbc().queryForList("SELECT tenant_id, status FROM ug_external_api_message_detail")
                .forEach(row -> statusByTenant.put((String) row.get("tenant_id"), (String) row.get("status")));
        assertEquals("SUCCESS", statusByTenant.get("pb.amritsar"));
        assertEquals("FAILED", statusByTenant.get("pb.jalandhar"));
        assertEquals("SUCCESS", statusByTenant.get("pb.ludhiana"));
    }

    private IngestRowData row(String ulb, String date) {
        HashMap<String, Object> metrics = new HashMap<>();
        metrics.put("transactions", 1);
        Data data = Data.builder()
                .date(date)
                .module("PT")
                .ward("W1")
                .ulb(ulb)
                .region("North")
                .state("Punjab")
                .metrics(metrics)
                .build();
        return IngestRowData.builder().data(data).build();
    }
}
