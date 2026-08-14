package org.upyog.dashboard.api;

import org.upyog.dashboard.client.DashboardFeignClient;
import org.upyog.dashboard.client.UserFeignClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.service.AuditService;

import org.upyog.dashboard.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.loader.impl.DashboardDataLoaderImpl;
import org.upyog.dashboard.model.*;
import org.upyog.dashboard.producer.DashboardProducer;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.service.OAuthTokenService;
import org.upyog.dashboard.transformer.ModuleTransformer;
import org.upyog.dashboard.validator.CommonValidator;

import java.util.*;

class DashboardClientSimpleTest {

    private DashboardClientImpl dashboardClient;

    @BeforeEach
    void setUp() throws Exception {
        UserFeignClient userFeignClient = Mockito.mock(UserFeignClient.class);
        DashboardFeignClient dashboardFeignClient = Mockito.mock(DashboardFeignClient.class);
        AuditService auditService = Mockito.mock(AuditService.class);
        ModuleTransformer<Object> transformer = Mockito.mock(ModuleTransformer.class);

        // 1. Build a dummy payload so downstream components have configuration context
        DashboardData dataForPayload = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4").ulb("pg.citya").region("Test")
                .state("PG").metrics(new LinkedHashMap<>()).build();

        DashboardPayload dummyPayload = DashboardPayload.builder().data(Collections.singletonList(dataForPayload))
                .build();

        // 2. Stub the mock transformer to return this valid payload structure
        Mockito.when(transformer.getModule()).thenReturn(Module.PT);
        Mockito.when(transformer.transform(Mockito.any())).thenReturn(dummyPayload);

        DashboardProperties dashboardProperties = Mockito.mock(DashboardProperties.class);
        Mockito.lenient().when(dashboardProperties.getIngestMaxAttempts()).thenReturn(3);
        Mockito.lenient().when(dashboardProperties.getOauthMaxAttempts()).thenReturn(3);
        Mockito.lenient().when(dashboardProperties.getUsername()).thenReturn("NDS1");
        Mockito.lenient().when(dashboardProperties.getTenantId()).thenReturn("pg");
        Mockito.lenient().when(dashboardProperties.getPassword()).thenReturn("eGov@123");
        Mockito.lenient().when(dashboardProperties.getUserType()).thenReturn("EMPLOYEE");

        OAuthTokenService oAuthTokenService = new OAuthTokenService();
        TestUtils.setField(oAuthTokenService, "userFeignClient", userFeignClient);
        TestUtils.setField(oAuthTokenService, "dashboardProperties", dashboardProperties);

        DashboardDataLoaderImpl loader = new DashboardDataLoaderImpl();
        TestUtils.setField(loader, "dashboardFeignClient", dashboardFeignClient);
        TestUtils.setField(loader, "oAuthTokenService", oAuthTokenService);
        TestUtils.setField(loader, "auditService", auditService);
        TestUtils.setField(loader, "dashboardProperties", dashboardProperties);

        TransformerRegistry registry = new TransformerRegistry(Collections.singletonList(transformer));
        CommonValidator commonValidator = new CommonValidator();

        dashboardClient = new DashboardClientImpl(registry, loader, commonValidator);
    }

    // Method 1: Makes the dummy data
    private DashboardRequest createDummyRequest() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("assessments", 48);
        metrics.put("todaysTotalApplications", 145);

        DashboardData data = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4").ulb("pg.citya").region("Test").state("PG")
                .metrics(metrics).build();

        return DashboardRequest.builder().module(Module.PT).rawData(List.of(data)).build();
    }

    // Method 2: Simply calls the execute method
    @Test
    void testExecuteWithDummyData() {
        DashboardRequest request = createDummyRequest();

        // Execution flow handles payload metadata matching properly now
        IngestionResult result = dashboardClient.execute(request);
    }

}
