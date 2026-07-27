package org.upyog.adapter.api;

import org.upyog.adapter.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.loader.impl.DashboardDataLoaderImpl;
import org.upyog.adapter.model.*;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.registry.TransformerRegistry;
import org.upyog.adapter.service.OAuthTokenService;
import org.upyog.adapter.transformer.ModuleTransformer;
import org.upyog.adapter.validator.CommonValidator;

import java.util.*;

class AdapterClientSimpleTest {

    private AdapterClientImpl adapterClient;

    @BeforeEach
    void setUp() throws Exception {
        org.upyog.adapter.client.UserFeignClient userFeignClient = Mockito.mock(org.upyog.adapter.client.UserFeignClient.class);
        org.upyog.adapter.client.DashboardFeignClient dashboardFeignClient = Mockito.mock(org.upyog.adapter.client.DashboardFeignClient.class);
        org.upyog.adapter.service.AuditService auditService = Mockito.mock(org.upyog.adapter.service.AuditService.class);
        ModuleTransformer<Object> transformer = Mockito.mock(ModuleTransformer.class);

        // 1. Build a dummy payload so downstream components have configuration context
        DashboardData dataForPayload = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4").ulb("pg.citya").region("Test")
                .state("PG").metrics(new LinkedHashMap<>()).build();

        DashboardPayload dummyPayload = DashboardPayload.builder().data(Collections.singletonList(dataForPayload))
                .build();

        // 2. Stub the mock transformer to return this valid payload structure
        Mockito.when(transformer.getModule()).thenReturn(Module.PT);
        Mockito.when(transformer.transform(Mockito.any())).thenReturn(dummyPayload);

        org.upyog.adapter.config.AdapterProperties adapterProperties = Mockito.mock(org.upyog.adapter.config.AdapterProperties.class);
        Mockito.lenient().when(adapterProperties.getIngestMaxAttempts()).thenReturn(3);
        Mockito.lenient().when(adapterProperties.getOauthMaxAttempts()).thenReturn(3);

        OAuthTokenService oAuthTokenService = new OAuthTokenService();
        TestUtils.setField(oAuthTokenService, "userFeignClient", userFeignClient);
        TestUtils.setField(oAuthTokenService, "adapterProperties", adapterProperties);

        DashboardDataLoaderImpl loader = new DashboardDataLoaderImpl();
        TestUtils.setField(loader, "dashboardFeignClient", dashboardFeignClient);
        TestUtils.setField(loader, "oAuthTokenService", oAuthTokenService);
        TestUtils.setField(loader, "auditService", auditService);
        TestUtils.setField(loader, "adapterProperties", adapterProperties);

        TransformerRegistry registry = new TransformerRegistry(Collections.singletonList(transformer));
        CommonValidator commonValidator = new CommonValidator();

        adapterClient = new AdapterClientImpl(registry, loader, commonValidator);
    }

    // Method 1: Makes the dummy data
    private AdapterRequest createDummyRequest() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("assessments", 48);
        metrics.put("todaysTotalApplications", 145);

        DashboardData data = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4").ulb("pg.citya").region("Test").state("PG")
                .metrics(metrics).build();

        return AdapterRequest.builder().module(Module.PT).rawData(List.of(data)).build();
    }

    // Method 2: Simply calls the execute method
    @Test
    void testExecuteWithDummyData() {
        AdapterRequest request = createDummyRequest();

        // Execution flow handles payload metadata matching properly now
        IngestionResult result = adapterClient.execute(request);
    }

    
}
