package org.upyog.dashboard.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.client.DashboardFeignClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.service.OAuthTokenService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test suite for {@link BulkIngestionInitServiceImpl}.
 * <p>
 * Validates proper assembly of UPYOG {@link org.upyog.dashboard.model.RequestInfo},
 * payload serialization into JSON, and dispatching to the configured Feign client.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class BulkIngestionInitServiceImplTest {

    @Mock
    private DashboardFeignClient dashboardFeignClient;

    @Mock
    private OAuthTokenService oAuthTokenService;

    @Mock
    private DashboardProperties dashboardProperties;

    private ObjectMapper objectMapper;
    private BulkIngestionInitServiceImpl service;

    /**
     * Initializes test dependencies, object mapper, and the service under test before each execution.
     */
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new BulkIngestionInitServiceImpl(
                dashboardFeignClient,
                oAuthTokenService,
                dashboardProperties,
                objectMapper
        );
    }

    /**
     * Tests that {@link BulkIngestionInitServiceImpl#initializeBulkIngestion} correctly extracts
     * auth tokens and user context, constructs a valid {@link org.upyog.dashboard.model.BulkIngestRequest} payload,
     * and dispatches an HTTP POST call via {@link DashboardFeignClient#ingestMetrics}.
     */
    @Test
    @DisplayName("initializeBulkIngestion constructs RequestInfo and details payload and calls feign client")
    void initializeBulkIngestion_success() {
        when(dashboardProperties.getBulkInitUrl())
                .thenReturn("http://localhost:8280/national-dashboard/bulk/v1/_init");
        when(dashboardProperties.getTenantId())
                .thenReturn("pg");
        when(oAuthTokenService.getToken())
                .thenReturn("mock-token");
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid("uuid-123");
        when(oAuthTokenService.getUserInfo())
                .thenReturn(userInfo);
        when(dashboardFeignClient.ingestMetrics(any(URI.class), any(String.class)))
                .thenReturn("{\"status\":\"SUCCESS\"}");

        String response = service.initializeBulkIngestion("UPYOG/pg/DASHBOARD/file.xlsx");

        assertThat(response).isEqualTo("{\"status\":\"SUCCESS\"}");

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(dashboardFeignClient).ingestMetrics(uriCaptor.capture(), bodyCaptor.capture());

        assertThat(uriCaptor.getValue().toString()).isEqualTo("http://localhost:8280/national-dashboard/bulk/v1/_init");
        assertThat(bodyCaptor.getValue()).contains("UPYOG/pg/DASHBOARD/file.xlsx");
        assertThat(bodyCaptor.getValue()).contains("\"stateCode\":\"pg\"");
        assertThat(bodyCaptor.getValue()).contains("mock-token");
    }

    @Test
    @DisplayName("initializeBulkIngestion with explicit delimiter includes delimiter field in details payload")
    void initializeBulkIngestion_withDelimiter_success() {
        when(dashboardProperties.getBulkInitUrl())
                .thenReturn("http://localhost:8280/national-dashboard/bulk/v1/_init");
        when(dashboardProperties.getTenantId())
                .thenReturn("pg");
        when(oAuthTokenService.getToken())
                .thenReturn("mock-token");
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid("uuid-123");
        when(oAuthTokenService.getUserInfo())
                .thenReturn(userInfo);
        when(dashboardFeignClient.ingestMetrics(any(URI.class), any(String.class)))
                .thenReturn("{\"status\":\"SUCCESS\"}");

        String response = service.initializeBulkIngestion("niuatt-internaltech/pg/PT/test_pt_bulk.psv", "|");

        assertThat(response).isEqualTo("{\"status\":\"SUCCESS\"}");

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(dashboardFeignClient).ingestMetrics(uriCaptor.capture(), bodyCaptor.capture());

        String jsonPayload = bodyCaptor.getValue();
        assertThat(jsonPayload).contains("niuatt-internaltech/pg/PT/test_pt_bulk.psv");
        assertThat(jsonPayload).contains("\"stateCode\":\"pg\"");
        assertThat(jsonPayload).contains("\"delimiter\":\"|\"");
    }
}
