package org.upyog.dx.upyogdata.pfms.client;

import org.upyog.dx.upyogdata.config.PFMSProperties;
import org.upyog.dx.upyogdata.pfms.models.PFMSAuthRequest;
import org.upyog.dx.upyogdata.pfms.models.PFMSAuthResponse;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransaction;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PFMSApiClientTest {

    @Mock
    private PFMSAuthFeignClient authFeignClient;

    @Mock
    private PFMSDataFeignClient dataFeignClient;

    @Mock
    private ExternalApiAuditLogger auditLogger;

    private PFMSApiClient pfmsApiClient;

    @BeforeEach
    void setUp() {
        PFMSProperties properties = new PFMSProperties();
        properties.getAuth().setUsername("user");
        properties.getAuth().setPassword("secret");
        properties.getAuth().setUrl("http://pfms/auth");
        properties.getClient().setIp("127.0.0.1");
        properties.getData().setUrl("http://pfms/data");
        pfmsApiClient = new PFMSApiClient(authFeignClient, dataFeignClient, auditLogger, properties);
        when(auditLogger.logAndExecute(any(), any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(1);
            return supplier.get();
        });
    }

    @Test
    void fetchAccessToken_shouldWrapAuthCall() {
        PFMSAuthResponse authResponse = new PFMSAuthResponse();
        authResponse.setAccessToken("token");
        when(authFeignClient.authenticate(any(PFMSAuthRequest.class))).thenReturn(authResponse);

        String token = pfmsApiClient.fetchAccessToken();

        assertEquals("token", token);
        ArgumentCaptor<ExternalApiAuditDetail> detailCaptor = ArgumentCaptor.forClass(ExternalApiAuditDetail.class);
        verify(auditLogger).logAndExecute(detailCaptor.capture(), any());
        assertEquals("PFMS", detailCaptor.getValue().getTenantId());
        assertEquals("pfms-auth", detailCaptor.getValue().getExternalApiName());
        assertEquals("POST", detailCaptor.getValue().getMethod());
    }

    @Test
    void pushTransaction_shouldReuseAuditCorrelationIdOnRetry() {
        PFMSTransaction transaction = new PFMSTransaction();
        transaction.setId("txn-1");
        transaction.setCorrelationId("batch-1");
        transaction.setUlbCodeOrPFMSAgencyCode("ULB1");
        transaction.setVoucherNumber("V1");
        transaction.setVoucherDate(LocalDate.of(2026, 1, 1));
        transaction.setVoucherType("Journal Voucher");
        transaction.setFinancialYear("2026");
        transaction.setAccountHeadCode(List.of("1"));
        transaction.setFunctionCode(List.of("2"));
        transaction.setSchemeCode(List.of("3"));
        when(dataFeignClient.pushTransaction(any(), any())).thenReturn("SUCCESS");

        String result = pfmsApiClient.pushTransaction(transaction, "token", "audit-id", 1);

        assertEquals("SUCCESS", result);
        ArgumentCaptor<ExternalApiAuditDetail> detailCaptor = ArgumentCaptor.forClass(ExternalApiAuditDetail.class);
        verify(auditLogger).logAndExecute(detailCaptor.capture(), any());
        assertEquals("audit-id", detailCaptor.getValue().getCorrelationId());
        assertEquals(1, detailCaptor.getValue().getRetryCount());
        assertEquals("txn-1", detailCaptor.getValue().getBusinessReferenceId());
        assertEquals("batch-1", detailCaptor.getValue().getOriginatingCorrelationId());
    }
}
