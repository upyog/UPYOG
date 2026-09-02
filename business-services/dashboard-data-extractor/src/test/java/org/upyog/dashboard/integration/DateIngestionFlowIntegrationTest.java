package org.upyog.dashboard.integration;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.client.DashboardFeignClient;
import org.upyog.dashboard.model.DashboardPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.dashboard.pt.model.RawPtMetric;
import org.upyog.dashboard.pt.model.RawPtCollection;
import org.upyog.dashboard.api.DashboardClientImpl;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.controller.IngestionTestController;
import org.upyog.dashboard.extractor.impl.PtModuleExtractor;
import org.upyog.dashboard.loader.impl.DashboardDataLoaderImpl;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.producer.DashboardProducer;

import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;
import org.upyog.dashboard.service.AuditService;
import org.upyog.dashboard.service.DailyIngestionService;
import org.upyog.dashboard.service.OAuthTokenService;
import org.upyog.dashboard.transformer.impl.PTTransformer;
import org.upyog.dashboard.util.TestUtils;
import org.upyog.dashboard.validator.CommonValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * Integration test verifying the end-to-end ingestion flow:
 * <ol>
 *   <li>User passes a target date (e.g. {@code LocalDate.of(2026, 7, 15)}).</li>
 *   <li><strong>Extraction:</strong> {@link PtModuleExtractor} queries the DB for raw metrics corresponding to the target date.</li>
 *   <li><strong>Transformation:</strong> {@link PTTransformer} transforms raw data into {@link org.upyog.dashboard.model.DashboardPayload}.</li>
 *   <li><strong>Validation:</strong> {@link CommonValidator} checks payload integrity.</li>
 *   <li><strong>API Hit (Load):</strong> {@link HttpLoader} constructs {@link org.upyog.dashboard.model.NationalDashboardIngestRequest} with OAuth token and POSTs to the National Dashboard ingest API.</li>
 *   <li><strong>State tracking:</strong> {@link DailyIngestionService} updates {@link IngestionSummaryRepository} with the target date.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
class DateIngestionFlowIntegrationTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private DashboardFeignClient dashboardFeignClient;

    @Mock
    private OAuthTokenService oAuthTokenService;

    @Mock
    private DashboardProducer producer;

    @Mock
    private AuditService auditService;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    private SchemaMappingConfig schemaMappingConfig;
    private PtModuleExtractor ptExtractor;
    private ExtractorRegistry extractorRegistry;
    private PTTransformer ptTransformer;
    private TransformerRegistry transformerRegistry;
    private CommonValidator commonValidator;
    private DashboardDataLoaderImpl httpLoader;
    private DashboardClientImpl dashboardClient;
    private DailyIngestionService dailyIngestionService;
    private IngestionTestController ingestionTestController;
    private ObjectMapper objectMapper;

    private final String targetApiUrl = "http://localhost:8080/national-dashboard/ingest";

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        // 1. Configure SchemaMappingConfig with PT query configurations
        schemaMappingConfig = new SchemaMappingConfig(Mockito.mock(org.springframework.core.io.ResourceLoader.class));
        schemaMappingConfig.setEnabledModules(List.of(Module.PT));

        SchemaMappingConfig.ModuleQueries ptQueries = new SchemaMappingConfig.ModuleQueries();
        ptQueries.setCombinedMetricsQuery("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId");
        ptQueries.setCollectionMetricsQuery("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId");

        Map<Module, SchemaMappingConfig.ModuleQueries> mappings = new HashMap<>();
        mappings.put(Module.PT, ptQueries);
        schemaMappingConfig.setMappings(mappings);

        // 2. Setup Extractor
        DashboardProperties ptDashboardProperties = Mockito.mock(DashboardProperties.class);
        lenient().when(ptDashboardProperties.getMetricUlb()).thenReturn("pg");
        lenient().when(ptDashboardProperties.getDbMaxAttempts()).thenReturn(3);
        lenient().when(ptDashboardProperties.getDbBaseDelayMs()).thenReturn(100L);
        lenient().when(ptDashboardProperties.getDbMaxDelayMs()).thenReturn(500L);
        lenient().when(ptDashboardProperties.getMetricState()).thenReturn("pg");
        lenient().when(ptDashboardProperties.getTenantId()).thenReturn("pg");

        org.upyog.dashboard.util.HierarchyParser hp = new org.upyog.dashboard.util.HierarchyParser("Block 4", "Test");
        org.upyog.dashboard.util.DatabaseQueryExecutor queryExecutor = new org.upyog.dashboard.util.DatabaseQueryExecutor(namedParameterJdbcTemplate, ptDashboardProperties);
        ptExtractor = new PtModuleExtractor(queryExecutor, schemaMappingConfig, ptDashboardProperties, hp);
        ptExtractor.init();

        extractorRegistry = new ExtractorRegistry(List.of(ptExtractor));

        // 3. Setup Transformer
        ptTransformer = new PTTransformer();
        TestUtils.setField(ptTransformer, "objectMapper", objectMapper);
        transformerRegistry = new TransformerRegistry(List.of(ptTransformer));

        // 4. Setup CommonValidator
        commonValidator = new CommonValidator();

        // 5. Setup HttpLoader
        DashboardProperties dashboardProperties = Mockito.mock(DashboardProperties.class);
        lenient().when(dashboardProperties.getDashboardIngestUrl()).thenReturn(targetApiUrl);
        lenient().when(dashboardProperties.getIngestMaxAttempts()).thenReturn(3);
        lenient().when(dashboardProperties.getIngestBaseDelayMs()).thenReturn(1L);
        lenient().when(dashboardProperties.getIngestMaxDelayMs()).thenReturn(2L);
        lenient().when(dashboardProperties.getTenantId()).thenReturn("pg");
        lenient().when(dashboardProperties.getMetricState()).thenReturn("pg");
        lenient().when(dashboardProperties.getMetricUlb()).thenReturn("pg.citya");
        lenient().when(dashboardProperties.getDefaultStartDateStr()).thenReturn("2026-06-01");
        lenient().when(dashboardProperties.getPtUsageCategories()).thenReturn(List.of("RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"));
        lenient().when(dashboardProperties.getPtTaxHeads()).thenReturn(List.of("PT_TAX"));
        lenient().when(dashboardProperties.getPtCessHeads()).thenReturn(List.of("PT_FIRE_CESS", "PT_CANCER_CESS"));
        lenient().when(dashboardProperties.getPtRebateHeads()).thenReturn(List.of("PT_TIME_REBATE", "PT_ADHOC_REBATE"));
        lenient().when(dashboardProperties.getPtPenaltyHeads()).thenReturn(List.of("PT_TIME_PENALTY", "PT_ADHOC_PENALTY"));
        lenient().when(dashboardProperties.getPtInterestHeads()).thenReturn(List.of("PT_TIME_INTEREST"));
        lenient().when(dashboardProperties.getPtDigitalPaymentModes()).thenReturn(List.of("ONLINE", "CARD"));

        TestUtils.setField(ptTransformer, "dashboardProperties", dashboardProperties);

        httpLoader = new DashboardDataLoaderImpl();
        TestUtils.setField(httpLoader, "dashboardFeignClient", dashboardFeignClient);
        TestUtils.setField(httpLoader, "oAuthTokenService", oAuthTokenService);
        TestUtils.setField(httpLoader, "auditService", auditService);
        TestUtils.setField(httpLoader, "gson", new Gson());
        TestUtils.setField(httpLoader, "objectMapper", objectMapper);
        TestUtils.setField(httpLoader, "dashboardProperties", dashboardProperties);

        // 6. Setup DashboardClientImpl
        org.upyog.dashboard.loader.DashboardDataLoaderFactory dataLoaderFactory = 
                new org.upyog.dashboard.loader.DashboardDataLoaderFactory(httpLoader, httpLoader, dashboardProperties);
        dashboardClient = new DashboardClientImpl(transformerRegistry, dataLoaderFactory, commonValidator);

        // 7. Setup DailyIngestionService
        dailyIngestionService = new DailyIngestionService(dashboardClient, extractorRegistry, schemaMappingConfig, summaryRepository, dashboardProperties, objectMapper);
        dailyIngestionService.init();

        // 8. Setup Controller
        ingestionTestController = new IngestionTestController(dailyIngestionService);
    }

    @Test
    @DisplayName("Complete Flow: User passes date -> Extract -> Transform -> Validate -> Hit API -> Save Ingestion State")
    void testCompleteIngestionFlow_WithUserPassedDate() throws Exception {
        // GIVEN: User passes a specific target date
        LocalDate targetDate = LocalDate.of(2026, 7, 15);

        // 1. Mock DB query responses for the extraction phase
        RawPtMetric metric = RawPtMetric.builder()
                .tenantid("pg.citya.Test.Block 4")
                .assessments(50)
                .todaysTotalApplications(120)
                .todaysClosedApplications(90)
                .noOfPropertiesPaidToday(35)
                .todaysApprovedApplications(85)
                .todaysApprovedApplicationsWithinSLA(80)
                .avgDaysForApplicationApproval(3)
                .propertiesRegisteredJson("[{\"name\":\"2025-26\",\"value\":1500}]")
                .assessedPropertiesJson("[{\"name\":\"RESIDENTIAL\",\"value\":1000},{\"name\":\"COMMERCIAL\",\"value\":500}]")
                .build();

        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(
                eq("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId"), 
                any(SqlParameterSource.class),
                any(RowMapper.class)))
                .thenReturn(List.of(metric));

        RawPtCollection collection = RawPtCollection.builder()
                .tenantid("pg.citya.Test.Block 4")
                .usageCategory("RESIDENTIAL")
                .paymentMode("ONLINE")
                .paymentId("PAY-101")
                .taxHeadCode("PT_TAX")
                .taxHeadAmount(15000.0)
                .build();

        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(
                eq("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId"), 
                any(SqlParameterSource.class),
                any(RowMapper.class)))
                .thenReturn(List.of(collection));

        // 2. Mock OAuthTokenService response for the API loading phase
        when(oAuthTokenService.getToken()).thenReturn("test-access-token-999");
        when(oAuthTokenService.getUserInfo()).thenReturn(null);

        // 3. Mock external National Dashboard Ingestion API HTTP response
        String apiResponseBody = "{\"status\":\"SUCCESS\",\"code\":200,\"message\":\"Data ingested successfully\"}";

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        when(dashboardFeignClient.ingestMetrics(eq(java.net.URI.create(targetApiUrl)), payloadCaptor.capture()))
                .thenReturn(apiResponseBody);

        // WHEN: Executing the complete ingestion flow for the target date
        List<IngestionResult> results = dailyIngestionService.ingestDailyData(targetDate);

        // THEN: Verify full flow execution & results
        assertThat(results).isNotNull().hasSize(1);
        IngestionResult result = results.get(0);
        assertThat(result.getIngestionStatus()).isEqualTo(DashboardExtractorConstants.STATUS_SUCCESS);
        assertThat(result.getResponseData()).isEqualTo(apiResponseBody);

        // Verify state tracking was updated for the passed date
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        // Verify HTTP POST request payload sent to the API
        String sentJsonPayload = payloadCaptor.getValue();
        JsonNode rootNode = objectMapper.readTree(sentJsonPayload);

        // Check RequestInfo header
        assertThat(rootNode.get("RequestInfo").get("authToken").asText()).isEqualTo("test-access-token-999");
        assertThat(rootNode.get("RequestInfo").get("apiId").asText()).isEqualTo("Rainmaker");

        // Check transformed data array
        JsonNode dataNode = rootNode.get("Data").get(0);
        assertThat(dataNode.get("date").asText()).isEqualTo("15-07-2026");
        assertThat(dataNode.get("module").asText()).isEqualTo("PT");
        assertThat(dataNode.get(DashboardExtractorConstants.KEY_ULB).asText()).isEqualTo("pg.citya");

        // Check extracted metrics within payload
        JsonNode metricsNode = dataNode.get("metrics");
        assertThat(metricsNode.get("assessments").asInt()).isEqualTo(50);
        assertThat(metricsNode.get("todaysTotalApplications").asInt()).isEqualTo(120);

        // Verify Kafka audit record push
        verify(auditService).pushIngestionRecord(any(DashboardPayload.class), any(String.class), any(String.class), eq(DashboardExtractorConstants.STATUS_SUCCESS));
    }

    @Test
    @DisplayName("Complete Flow via REST Controller: User passes date param -> Flow runs -> Returns HTTP 200 OK")
    void testCompleteIngestionFlow_ViaRestController() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 20);

        RawPtMetric metric = RawPtMetric.builder()
                .tenantid("pg.citya.Test.Block 4")
                .assessments(10)
                .todaysTotalApplications(25)
                .build();
        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(eq("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId"), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(List.of(metric));
        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(eq("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId"), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(Collections.emptyList());

        when(oAuthTokenService.getToken()).thenReturn("token-abc");
        when(dashboardFeignClient.ingestMetrics(eq(java.net.URI.create(targetApiUrl)), Mockito.anyString()))
                .thenReturn("{\"status\":\"ACCEPTED\"}");

        // WHEN: Triggering controller endpoint with targetDate
        ResponseEntity<List<IngestionResult>> controllerResponse = ingestionTestController.pushData(targetDate);

        // THEN:
        assertThat(controllerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(controllerResponse.getBody()).hasSize(1);
        assertThat(controllerResponse.getBody().get(0).getIngestionStatus()).isEqualTo(DashboardExtractorConstants.STATUS_SUCCESS);

        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);
    }

    @Test
    @DisplayName("Complete Flow Failure Handling: API error returns FAILURE and does not update tracker")
    void testCompleteIngestionFlow_ApiFailure_DoesNotUpdateTracker() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 18);

        RawPtMetric metric = RawPtMetric.builder()
                .tenantid("pg.citya.Test.Block 4")
                .assessments(50)
                .build();
        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(eq("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId"), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(List.of(metric));
        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(eq("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId"), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(Collections.emptyList());
        when(oAuthTokenService.getToken()).thenReturn("token-123");

        // Mock API throwing exception (e.g. HTTP 500 Server Error)
        when(dashboardFeignClient.ingestMetrics(eq(java.net.URI.create(targetApiUrl)), Mockito.anyString()))
                .thenThrow(new RuntimeException("National Dashboard API Service Unavailable (500)"));

        // WHEN: Executing flow for targetDate
        List<IngestionResult> results = dailyIngestionService.ingestDailyData(targetDate);

        // THEN: Verify status is FAILURE and last successful date was NOT updated
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo(DashboardExtractorConstants.STATUS_FAILURE);
        assertThat(results.get(0).getFailureReason()).contains("National Dashboard API Service Unavailable");

        verify(summaryRepository, Mockito.never()).saveOrUpdateLastSuccessfulDate(any(), any(), any());
    }

    
}
