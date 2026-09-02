package org.upyog.dashboard.api;

import org.upyog.dashboard.client.DashboardFeignClient;
import org.upyog.dashboard.client.UserFeignClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.loader.impl.DashboardDataLoaderImpl;
import org.upyog.dashboard.loader.DashboardDataLoaderFactory;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.dto.PTDTO;
import org.upyog.dashboard.transformer.impl.PTTransformer;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.service.AuditService;
import org.upyog.dashboard.service.OAuthTokenService;
import org.upyog.dashboard.validator.CommonValidator;
import org.upyog.dashboard.common.constants.Module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DashboardClientSimpleTest {

    @Mock
    private DashboardFeignClient dashboardFeignClient;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private AuditService auditService;

    @Mock
    private DashboardProperties dashboardProperties;

    private DashboardClientImpl dashboardClient;

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        PTTransformer transformer = new PTTransformer();
        setField(transformer, "objectMapper", new ObjectMapper());
        setField(transformer, "dashboardProperties", dashboardProperties);

        Mockito.lenient().when(dashboardProperties.getUsername()).thenReturn("testUser");
        Mockito.lenient().when(dashboardProperties.getTenantId()).thenReturn("pg");
        Mockito.lenient().when(dashboardProperties.getPassword()).thenReturn("eGov@123");
        Mockito.lenient().when(dashboardProperties.getUserType()).thenReturn("EMPLOYEE");
        Mockito.lenient().when(dashboardProperties.getDashboardIngestUrl()).thenReturn("http://localhost:8080/national-dashboard/ingest");
        Mockito.lenient().when(dashboardProperties.getEffectiveDailyUploadMode()).thenReturn("API");
        Mockito.lenient().when(dashboardProperties.getPtUsageCategories()).thenReturn(List.of("RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"));
        Mockito.lenient().when(dashboardProperties.getPtTaxHeads()).thenReturn(List.of("PT_TAX"));
        Mockito.lenient().when(dashboardProperties.getPtCessHeads()).thenReturn(List.of("PT_FIRE_CESS"));
        Mockito.lenient().when(dashboardProperties.getPtRebateHeads()).thenReturn(List.of("PT_TIME_REBATE"));
        Mockito.lenient().when(dashboardProperties.getPtPenaltyHeads()).thenReturn(List.of("PT_TIME_PENALTY"));
        Mockito.lenient().when(dashboardProperties.getPtInterestHeads()).thenReturn(List.of("PT_TIME_INTEREST"));
        Mockito.lenient().when(dashboardProperties.getPtDigitalPaymentModes()).thenReturn(List.of("ONLINE", "CARD"));

        OAuthTokenService oAuthTokenService = Mockito.mock(OAuthTokenService.class);
        Mockito.when(oAuthTokenService.getToken()).thenReturn("token-123");

        DashboardDataLoaderImpl httpLoader = new DashboardDataLoaderImpl();
        setField(httpLoader, "dashboardFeignClient", dashboardFeignClient);
        setField(httpLoader, "oAuthTokenService", oAuthTokenService);
        setField(httpLoader, "auditService", auditService);
        setField(httpLoader, "dashboardProperties", dashboardProperties);
        setField(httpLoader, "objectMapper", new ObjectMapper());
        setField(httpLoader, "gson", new com.google.gson.Gson());

        TransformerRegistry registry = new TransformerRegistry(Collections.singletonList(transformer));
        CommonValidator commonValidator = new CommonValidator();

        DashboardDataLoaderFactory factory = new DashboardDataLoaderFactory(httpLoader, httpLoader, dashboardProperties);

        dashboardClient = new DashboardClientImpl(registry, factory, commonValidator);
    }

    private DashboardRequest createDummyRequest() {
        PTDTO ptDto = PTDTO.builder()
                .date("15-07-2026")
                .module("PT")
                .ward("Block 4")
                .ulb("pg.citya")
                .region("Test")
                .state("PG")
                .combinedMetrics(PTAggregatedData.builder()
                        .assessments(48)
                        .todaysTotalApplications(145)
                        .build())
                .collectionMetrics(List.of())
                .build();

        return DashboardRequest.builder().module(Module.PT).rawData(ptDto).build();
    }

    @Test
    void testExecuteWithDummyData() {
        Mockito.when(dashboardFeignClient.ingestMetrics(Mockito.any(), Mockito.anyString()))
                .thenReturn("{\"status\": \"ok\"}");

        DashboardRequest request = createDummyRequest();

        IngestionResult result = dashboardClient.execute(request);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getIngestionStatus());
    }
}
