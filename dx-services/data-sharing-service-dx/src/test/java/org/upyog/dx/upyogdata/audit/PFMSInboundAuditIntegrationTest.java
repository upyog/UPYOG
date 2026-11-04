package org.upyog.dx.upyogdata.audit;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.dx.upyogdata.audit.support.ExternalApiAuditTableHarness;
import org.upyog.dx.upyogdata.controller.PFMSController;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;
import org.upyog.dx.upyogdata.pfms.models.PFMSData;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransaction;
import org.upyog.dx.upyogdata.pfms.service.PFMSForwardingService;
import org.upyog.dx.upyogdata.pfms.service.PFMSService;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PFMSInboundAuditIntegrationTest {

    private static ExternalApiAuditTableHarness harness;

    private PFMSService pfmsService;
    private PFMSController controller;

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
        pfmsService = mock(PFMSService.class);
        controller = new PFMSController(pfmsService, mock(PFMSForwardingService.class),
                harness.newLogger(true));
    }

    @Test
    void stateCreateSuccess_writesInboundPfmsRow() {
        when(pfmsService.createTransaction(any())).thenReturn(Map.of("status", "ACCEPTED"));

        ResponseEntity<?> response = controller.createTransaction(sampleRequest("state-corr-1"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, harness.messageCount());
        assertEquals(0, harness.errorCount());

        String correlationId = harness.onlyCorrelationId();
        Map<String, Object> message = harness.message(correlationId);
        assertEquals("PFMS", message.get("tenant_id"));
        assertEquals(ExternalApiAuditConstants.API_STATE_PFMS_TRANSACTION_CREATE, message.get("external_api_name"));
        assertEquals(ExternalApiAuditConstants.DIRECTION_INBOUND, message.get("direction"));
        assertEquals(ExternalApiAuditConstants.STATUS_SUCCESS, message.get("status"));
        assertEquals(200, ((Number) message.get("http_status_code")).intValue());
        assertNotEquals("state-corr-1", correlationId);

        String requestPayload = String.valueOf(harness.raw(correlationId).get("request_payload"));
        String responsePayload = String.valueOf(harness.raw(correlationId).get("response_payload"));
        assertTrue(requestPayload.contains("state-corr-1"));
        assertTrue(requestPayload.contains("/data-sharing-service-dx/v1/transactions/_create"));
        assertTrue(requestPayload.contains("\"endpoint\""));
        assertTrue(requestPayload.contains("\"method\""));
        assertTrue(requestPayload.contains("POST"));
        assertTrue(requestPayload.contains("********"));
        assertFalse(requestPayload.contains("state-secret-token"));
        assertTrue(responsePayload.contains("ACCEPTED"));
    }

    @Test
    void stateCreateValidationFailure_writesFailedRowAndReturns400() {
        when(pfmsService.createTransaction(any())).thenThrow(new IllegalArgumentException("Data cannot be empty"));

        ResponseEntity<?> response = controller.createTransaction(sampleRequest("state-corr-bad"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, harness.messageCount());
        assertEquals(1, harness.errorCount());

        String correlationId = harness.onlyCorrelationId();
        assertEquals(ExternalApiAuditConstants.STATUS_FAILED, harness.message(correlationId).get("status"));
        assertEquals("INTEGRATION_CALL_FAILED", harness.errors(correlationId).get(0).get("error_code"));
        assertEquals("Data cannot be empty", harness.errors(correlationId).get(0).get("error_message"));
    }

    @Test
    void twoStateCreates_writeTwoRows() {
        when(pfmsService.createTransaction(any())).thenReturn("SUCCESS");

        controller.createTransaction(sampleRequest("same-origin"));
        controller.createTransaction(sampleRequest("same-origin"));

        assertEquals(2, harness.messageCount());
        List<Map<String, Object>> rows = harness.messagesByApi(
                ExternalApiAuditConstants.API_STATE_PFMS_TRANSACTION_CREATE);
        assertNotEquals(rows.get(0).get("correlation_id"), rows.get(1).get("correlation_id"));
    }

    @Test
    void payloadCaptureDisabled_doesNotStoreBodies() {
        controller = new PFMSController(pfmsService, mock(PFMSForwardingService.class),
                harness.newLogger(false));
        when(pfmsService.createTransaction(any())).thenReturn("SUCCESS");

        controller.createTransaction(sampleRequest("origin-off"));

        String requestPayload = String.valueOf(harness.raw(harness.onlyCorrelationId()).get("request_payload"));
        assertTrue(requestPayload.contains("payloadCaptured"));
        assertFalse(requestPayload.contains("state-secret-token"));
        assertFalse(requestPayload.contains("VCH-1"));
    }

    @Test
    void kafkaDown_doesNotFailInboundCreate() {
        ExternalApiAuditLogger logger = harness.newLogger(true, harness.throwingPublisher());
        controller = new PFMSController(pfmsService, mock(PFMSForwardingService.class), logger);
        when(pfmsService.createTransaction(any())).thenReturn("SUCCESS");

        ResponseEntity<?> response = controller.createTransaction(sampleRequest("origin-kafka"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SUCCESS", response.getBody());
        assertEquals(0, harness.messageCount());
    }

    private PFMSCreateTransactionRequest sampleRequest(String originatingCorrelationId) {
        PFMSTransaction transaction = new PFMSTransaction();
        transaction.setVoucherNumber("VCH-1");
        transaction.setUlbCodeOrPFMSAgencyCode("ULB1");
        transaction.setVoucherDate(LocalDate.of(2026, 10, 5));
        transaction.setVoucherType("Journal Voucher");
        transaction.setFinancialYear("2026");
        transaction.setAccountHeadCode(List.of("AH1"));
        transaction.setFunctionCode(List.of("FN1"));
        transaction.setSchemeCode(List.of("SC1"));
        transaction.setUlbBankAccountNumber("123456789012");
        transaction.setBeneficiaryAccountNumber("998877665544");

        PFMSData data = new PFMSData();
        data.setUlb("pb.amritsar");
        data.setTransactions(List.of(transaction));

        RequestInfo requestInfo = RequestInfo.builder()
                .correlationId(originatingCorrelationId)
                .authToken("state-secret-token")
                .build();
        return new PFMSCreateTransactionRequest(requestInfo, List.of(data));
    }
}
