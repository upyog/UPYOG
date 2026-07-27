package org.upyog.adapter.integration;

import org.upyog.adapter.client.DashboardFeignClient;
import org.upyog.adapter.model.DashboardPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.upyog.adapter.api.AdapterClientImpl;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.AdapterProperties;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.controller.IngestionTestController;
import org.upyog.adapter.extractor.impl.PtModuleExtractor;
import org.upyog.adapter.loader.impl.DashboardDataLoaderImpl;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.pt.dto.PTAggregatedData;
import org.upyog.adapter.pt.dto.PTCollectionDTO;
import org.upyog.adapter.pt.mapper.PTRowmapper;
import org.upyog.adapter.registry.ExtractorRegistry;
import org.upyog.adapter.registry.TransformerRegistry;
import org.upyog.adapter.repository.IngestionSummaryRepository;
import org.upyog.adapter.service.AuditService;
import org.upyog.adapter.service.DailyIngestionService;
import org.upyog.adapter.service.OAuthTokenService;
import org.upyog.adapter.transformer.impl.PTTransformer;
import org.upyog.adapter.util.TestUtils;
import org.upyog.adapter.validator.CommonValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * Integration test verifying the end-to-end ingestion flow:
 * <ol>
 *   <li>User passes a target date (e.g. {@code LocalDate.of(2026, 7, 15)}).</li>
 *   <li><strong>Extraction:</strong> {@link PtModuleExtractor} queries the DB for raw metrics corresponding to the target date.</li>
 *   <li><strong>Transformation:</strong> {@link PTTransformer} transforms raw data into {@link org.upyog.adapter.model.DashboardPayload}.</li>
 *   <li><strong>Validation:</strong> {@link CommonValidator} checks payload integrity.</li>
 *   <li><strong>API Hit (Load):</strong> {@link HttpLoader} constructs {@link org.upyog.adapter.model.NationalDashboardIngestRequest} with OAuth token and POSTs to the National Dashboard ingest API.</li>
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
    private AdapterProducer producer;

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
    private AdapterClientImpl adapterClient;
    private DailyIngestionService dailyIngestionService;
    private IngestionTestController ingestionTestController;
    private ObjectMapper objectMapper;

    private final String targetApiUrl = "http://localhost:8080/national-dashboard/ingest";

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        // 1. Configure SchemaMappingConfig with PT query configurations
        schemaMappingConfig = new SchemaMappingConfig();
        schemaMappingConfig.setEnabledModules(List.of(Module.PT));

        SchemaMappingConfig.ModuleQueries ptQueries = new SchemaMappingConfig.ModuleQueries();
        ptQueries.setCombinedMetricsQuery("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId");
        ptQueries.setCollectionMetricsQuery("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId");

        Map<Module, SchemaMappingConfig.ModuleQueries> mappings = new HashMap<>();
        mappings.put(Module.PT, ptQueries);
        schemaMappingConfig.setMappings(mappings);

        // 2. Setup Extractor
        ptExtractor = new PtModuleExtractor();
        TestUtils.setField(ptExtractor, "namedParameterJdbcTemplate", namedParameterJdbcTemplate);
        TestUtils.setField(ptExtractor, "schemaMappingConfig", schemaMappingConfig);
        TestUtils.setField(ptExtractor, "ulb", "pg.citya");
        TestUtils.setField(ptExtractor, "ward", "Block 4");
        TestUtils.setField(ptExtractor, "region", "Test");
        TestUtils.setField(ptExtractor, "state", "Punjab");
        TestUtils.setField(ptExtractor, "dbTenantId", "pg");

        extractorRegistry = new ExtractorRegistry(List.of(ptExtractor));

        // 3. Setup Transformer
        ptTransformer = new PTTransformer();
        TestUtils.setField(ptTransformer, "objectMapper", objectMapper);
        transformerRegistry = new TransformerRegistry(List.of(ptTransformer));

        // 4. Setup CommonValidator
        commonValidator = new CommonValidator();

        // 5. Setup HttpLoader
        AdapterProperties adapterProperties = Mockito.mock(AdapterProperties.class);
        lenient().when(adapterProperties.getDashboardIngestUrl()).thenReturn(targetApiUrl);
        lenient().when(adapterProperties.getIngestMaxAttempts()).thenReturn(3);
        lenient().when(adapterProperties.getIngestBaseDelayMs()).thenReturn(1L);
        lenient().when(adapterProperties.getIngestMaxDelayMs()).thenReturn(2L);
        lenient().when(adapterProperties.getTenantId()).thenReturn("pg");
        lenient().when(adapterProperties.getDefaultStartDateStr()).thenReturn("2026-06-01");
        lenient().when(adapterProperties.getPtUsageCategories()).thenReturn(List.of("RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"));
        lenient().when(adapterProperties.getPtTaxHeads()).thenReturn(List.of("PT_TAX"));
        lenient().when(adapterProperties.getPtCessHeads()).thenReturn(List.of("PT_FIRE_CESS", "PT_CANCER_CESS"));
        lenient().when(adapterProperties.getPtRebateHeads()).thenReturn(List.of("PT_TIME_REBATE", "PT_ADHOC_REBATE"));
        lenient().when(adapterProperties.getPtPenaltyHeads()).thenReturn(List.of("PT_TIME_PENALTY", "PT_ADHOC_PENALTY"));
        lenient().when(adapterProperties.getPtInterestHeads()).thenReturn(List.of("PT_TIME_INTEREST"));
        lenient().when(adapterProperties.getPtDigitalPaymentModes()).thenReturn(List.of("ONLINE", "CARD"));

        TestUtils.setField(ptTransformer, "adapterProperties", adapterProperties);

        httpLoader = new DashboardDataLoaderImpl();
        TestUtils.setField(httpLoader, "dashboardFeignClient", dashboardFeignClient);
        TestUtils.setField(httpLoader, "oAuthTokenService", oAuthTokenService);
        TestUtils.setField(httpLoader, "auditService", auditService);
        TestUtils.setField(httpLoader, "gson", new Gson());
        TestUtils.setField(httpLoader, "objectMapper", objectMapper);
        TestUtils.setField(httpLoader, "adapterProperties", adapterProperties);

        // 6. Setup AdapterClientImpl
        adapterClient = new AdapterClientImpl(transformerRegistry, httpLoader, commonValidator);

        // 7. Setup DailyIngestionService
        dailyIngestionService = new DailyIngestionService();
        TestUtils.setField(dailyIngestionService, "adapterClient", adapterClient);
        TestUtils.setField(dailyIngestionService, "extractorRegistry", extractorRegistry);
        TestUtils.setField(dailyIngestionService, "schemaMappingConfig", schemaMappingConfig);
        TestUtils.setField(dailyIngestionService, "summaryRepository", summaryRepository);
        TestUtils.setField(dailyIngestionService, "tenantId", "pg");
        TestUtils.setField(dailyIngestionService, "defaultStartDateStr", "2026-06-01");
        TestUtils.setField(dailyIngestionService, "adapterProperties", adapterProperties);

        // 8. Setup Controller
        ingestionTestController = new IngestionTestController();
        TestUtils.setField(ingestionTestController, "service", dailyIngestionService);
    }

    @Test
    @DisplayName("Complete Flow: User passes date -> Extract -> Transform -> Validate -> Hit API -> Save Ingestion State")
    void testCompleteIngestionFlow_WithUserPassedDate() throws Exception {
        // GIVEN: User passes a specific target date
        LocalDate targetDate = LocalDate.of(2026, 7, 15);

        // 1. Mock DB query responses for the extraction phase
        PTAggregatedData combinedMetricsResult = PTAggregatedData.builder()
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

        when(namedParameterJdbcTemplate.queryForObject(
                eq("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId"), 
                Mockito.<Map<String, ?>>any(), 
                eq(PTRowmapper.COMBINED_ROW_MAPPER)))
                .thenReturn(combinedMetricsResult);

        List<PTCollectionDTO> collectionMetricsResult = new ArrayList<>();
        PTCollectionDTO colRow = PTCollectionDTO.builder()
                .usageCategory("RESIDENTIAL")
                .paymentMode("ONLINE")
                .paymentId("PAY-101")
                .taxHeadCode("PT_TAX")
                .taxHeadAmount(15000.0)
                .build();
        collectionMetricsResult.add(colRow);

        when(namedParameterJdbcTemplate.query(
                eq("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId"), 
                Mockito.<Map<String, ?>>any(), 
                eq(PTRowmapper.COLLECTION_ROW_MAPPER)))
                .thenReturn(collectionMetricsResult);

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
        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
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
        assertThat(dataNode.get("ulb").asText()).isEqualTo("pg.citya");

        // Check extracted metrics within payload
        JsonNode metricsNode = dataNode.get("metrics");
        assertThat(metricsNode.get("assessments").asInt()).isEqualTo(50);
        assertThat(metricsNode.get("todaysTotalApplications").asInt()).isEqualTo(120);

        // Verify Kafka audit record push
        verify(auditService).pushIngestionRecord(any(DashboardPayload.class), any(String.class), any(String.class), eq("SUCCESS"));
    }

    @Test
    @DisplayName("Complete Flow via REST Controller: User passes date param -> Flow runs -> Returns HTTP 200 OK")
    void testCompleteIngestionFlow_ViaRestController() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 20);

        PTAggregatedData combinedMetricsResult = PTAggregatedData.builder()
                .assessments(10)
                .todaysTotalApplications(25)
                .build();
        when(namedParameterJdbcTemplate.queryForObject(any(String.class), Mockito.<Map<String, ?>>any(), any(org.springframework.jdbc.core.RowMapper.class))).thenReturn(combinedMetricsResult);
        when(namedParameterJdbcTemplate.query(any(String.class), Mockito.<Map<String, ?>>any(), any(org.springframework.jdbc.core.RowMapper.class))).thenReturn(Collections.emptyList());

        when(oAuthTokenService.getToken()).thenReturn("token-abc");
        when(dashboardFeignClient.ingestMetrics(eq(java.net.URI.create(targetApiUrl)), Mockito.anyString()))
                .thenReturn("{\"status\":\"ACCEPTED\"}");

        // WHEN: Triggering controller endpoint with targetDate
        ResponseEntity<List<IngestionResult>> controllerResponse = ingestionTestController.pushData(targetDate);

        // THEN:
        assertThat(controllerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(controllerResponse.getBody()).hasSize(1);
        assertThat(controllerResponse.getBody().get(0).getIngestionStatus()).isEqualTo("SUCCESS");

        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);
    }

    @Test
    @DisplayName("Complete Flow Failure Handling: API error returns FAILURE and does not update tracker")
    void testCompleteIngestionFlow_ApiFailure_DoesNotUpdateTracker() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 18);

        when(namedParameterJdbcTemplate.queryForObject(any(String.class), Mockito.<Map<String, ?>>any(), any(org.springframework.jdbc.core.RowMapper.class))).thenReturn(new PTAggregatedData());
        when(namedParameterJdbcTemplate.query(any(String.class), Mockito.<Map<String, ?>>any(), any(org.springframework.jdbc.core.RowMapper.class))).thenReturn(Collections.emptyList());
        when(oAuthTokenService.getToken()).thenReturn("token-123");

        // Mock API throwing exception (e.g. HTTP 500 Server Error)
        when(dashboardFeignClient.ingestMetrics(eq(java.net.URI.create(targetApiUrl)), Mockito.anyString()))
                .thenThrow(new RuntimeException("National Dashboard API Service Unavailable (500)"));

        // WHEN: Executing flow for targetDate
        List<IngestionResult> results = dailyIngestionService.ingestDailyData(targetDate);

        // THEN: Verify status is FAILURE and last successful date was NOT updated
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("FAILURE");
        assertThat(results.get(0).getFailureReason()).contains("National Dashboard API Service Unavailable");

        verify(summaryRepository, Mockito.never()).saveOrUpdateLastSuccessfulDate(any(), any(), any());
    }

    
}
