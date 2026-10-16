package org.upyog.dx.upyogdata.controller;

import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;
import org.upyog.dx.upyogdata.pfms.service.PFMSForwardingService;
import org.upyog.dx.upyogdata.pfms.service.PFMSService;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PFMSControllerTest {

    @Mock
    private PFMSService pfmsService;

    @Mock
    private PFMSForwardingService pfmsForwardingService;

    @Mock
    private ExternalApiAuditLogger auditLogger;

    @InjectMocks
    private PFMSController pfmsController;

    @Test
    void createTransaction_shouldAuditInboundStateCall() {
        PFMSCreateTransactionRequest request = new PFMSCreateTransactionRequest();
        when(auditLogger.logInboundApi(any(), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(pfmsService.createTransaction(request)).thenReturn("SUCCESS");

        ResponseEntity<?> response = pfmsController.createTransaction(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SUCCESS", response.getBody());
        ArgumentCaptor<ExternalApiAuditDetail> detailCaptor = ArgumentCaptor.forClass(ExternalApiAuditDetail.class);
        verify(auditLogger).logInboundApi(detailCaptor.capture(), any());
        assertEquals("PFMS", detailCaptor.getValue().getTenantId());
        assertEquals("state-pfms-transaction-create", detailCaptor.getValue().getExternalApiName());
        assertEquals("POST", detailCaptor.getValue().getMethod());
    }
}
