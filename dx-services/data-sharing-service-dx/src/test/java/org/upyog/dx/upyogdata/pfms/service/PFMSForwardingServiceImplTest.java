package org.upyog.dx.upyogdata.pfms.service;

import feign.FeignException;
import feign.Request;
import org.upyog.dx.upyogdata.config.PFMSProperties;
import org.upyog.dx.upyogdata.constants.Constants;
import org.upyog.dx.upyogdata.pfms.client.PFMSApiClient;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionEntity;
import org.upyog.dx.upyogdata.pfms.repository.PFMSRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PFMSForwardingServiceImplTest {

    @Mock
    private PFMSRepository pfmsRepository;

    @Mock
    private PFMSApiClient pfmsApiClient;

    private PFMSForwardingServiceImpl forwardingService;

    @BeforeEach
    void setUp() {
        PFMSProperties properties = new PFMSProperties();
        properties.getScheduler().setBatchSize(50);
        forwardingService = new PFMSForwardingServiceImpl(pfmsRepository, pfmsApiClient, properties);
    }

    @Test
    void shouldReuseAuditCorrelationIdWhenRetryingUnauthorizedPush() {
        PfmsTransactionEntity transaction = PfmsTransactionEntity.builder()
                .id("txn-1")
                .voucherNumber("V1")
                .build();
        when(pfmsRepository.fetchInitiatedTransactionIds(50)).thenReturn(List.of("txn-1"));
        when(pfmsRepository.fetchTransactionById("txn-1")).thenReturn(transaction);
        when(pfmsApiClient.fetchAccessToken()).thenReturn("token-1", "token-2");
        when(pfmsApiClient.pushTransaction(eq(transaction), eq("token-1"), anyString(), eq(0)))
                .thenThrow(unauthorized());
        when(pfmsApiClient.pushTransaction(eq(transaction), eq("token-2"), anyString(), eq(1)))
                .thenReturn("SUCCESS");

        forwardingService.forwardInitiatedTransactions(Constants.MANUAL);

        verify(pfmsApiClient, times(2)).fetchAccessToken();
        verify(pfmsApiClient).pushTransaction(eq(transaction), eq("token-1"), anyString(), eq(0));
        verify(pfmsApiClient).pushTransaction(eq(transaction), eq("token-2"), anyString(), eq(1));
        verify(pfmsRepository).updateTransactionStatus(eq("txn-1"), eq("SUCCESS"), any());
    }

    private FeignException unauthorized() {
        Request request = Request.create(Request.HttpMethod.POST, "http://pfms/data",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        return FeignException.errorStatus("pushTransaction",
                feign.Response.builder()
                        .status(401)
                        .reason("Unauthorized")
                        .request(request)
                        .headers(Collections.emptyMap())
                        .build());
    }
}
