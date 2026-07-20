package org.upyog.adapter.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.upyog.adapter.api.AdapterClientImpl;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.controller.IngestionTestController;
import org.upyog.adapter.loader.impl.HttpLoader;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.pt.extractor.PtModuleExtractor;
import org.upyog.adapter.pt.transformer.PTTransformer;
import org.upyog.adapter.registry.ExtractorRegistry;
import org.upyog.adapter.registry.TransformerRegistry;
import org.upyog.adapter.repository.IngestionSummaryRepository;
import org.upyog.adapter.service.DailyIngestionService;
import org.upyog.adapter.service.OAuthTokenService;
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
    private RestTemplate restTemplate;

    @Mock
    private OAuthTokenService oAuthTokenService;

    @Mock
    private AdapterProducer producer;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    private SchemaMappingConfig schemaMappingConfig;
    private PtModuleExtractor ptExtractor;
    private ExtractorRegistry extractorRegistry;
    private PTTransformer ptTransformer;
    private TransformerRegistry transformerRegistry;
    private CommonValidator commonValidator;
    private HttpLoader httpLoader;
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
        setField(ptExtractor, "namedParameterJdbcTemplate", namedParameterJdbcTemplate);
        setField(ptExtractor, "schemaMappingConfig", schemaMappingConfig);
        setField(ptExtractor, "objectMapper", objectMapper);
        setField(ptExtractor, "ulb", "pg.citya");
        setField(ptExtractor, "ward", "Block 4");
        setField(ptExtractor, "region", "Test");
        setField(ptExtractor, "state", "Punjab");
        setField(ptExtractor, "dbTenantId", "pg");

        extractorRegistry = new ExtractorRegistry(List.of(ptExtractor));

        // 3. Setup Transformer
        ptTransformer = new PTTransformer();
        transformerRegistry = new TransformerRegistry(List.of(ptTransformer));

        // 4. Setup CommonValidator
        commonValidator = new CommonValidator();

        // 5. Setup HttpLoader
        httpLoader = new HttpLoader();
        setField(httpLoader, "restTemplate", restTemplate);
        setField(httpLoader, "oAuthTokenService", oAuthTokenService);
        setField(httpLoader, "producer", producer);
        setField(httpLoader, "dashboardIngestUrl", targetApiUrl);
        setField(httpLoader, "gson", new Gson());
        setField(httpLoader, "objectMapper", objectMapper);

        // 6. Setup AdapterClientImpl
        adapterClient = new AdapterClientImpl(transformerRegistry, httpLoader, commonValidator);

        // 7. Setup DailyIngestionService
        dailyIngestionService = new DailyIngestionService();
        setField(dailyIngestionService, "adapterClient", adapterClient);
        setField(dailyIngestionService, "extractorRegistry", extractorRegistry);
        setField(dailyIngestionService, "schemaMappingConfig", schemaMappingConfig);
        setField(dailyIngestionService, "summaryRepository", summaryRepository);
        setField(dailyIngestionService, "tenantId", "pg");
        setField(dailyIngestionService, "defaultStartDateStr", "2026-06-01");

        // 8. Setup Controller
        ingestionTestController = new IngestionTestController();
        setField(ingestionTestController, "service", dailyIngestionService);
    }

    @Test
    @DisplayName("Complete Flow: User passes date -> Extract -> Transform -> Validate -> Hit API -> Save Ingestion State")
    void testCompleteIngestionFlow_WithUserPassedDate() throws Exception {
        // GIVEN: User passes a specific target date
        LocalDate targetDate = LocalDate.of(2026, 7, 15);

        // 1. Mock DB query responses for the extraction phase
        Map<String, Object> combinedMetricsResult = new LinkedHashMap<>();
        combinedMetricsResult.put("assessments", 50);
        combinedMetricsResult.put("todaystotalapplications", 120);
        combinedMetricsResult.put("todaysclosedapplications", 90);
        combinedMetricsResult.put("noofpropertiespaidtoday", 35);
        combinedMetricsResult.put("todaysapprovedapplications", 85);
        combinedMetricsResult.put("todaysapprovedapplicationswithinsla", 80);
        combinedMetricsResult.put("avgdaysforapplicationapproval", 3);
        combinedMetricsResult.put("propertiesregisteredjson", "[{\"name\":\"2025-26\",\"value\":1500}]");
        combinedMetricsResult.put("assessedpropertiesjson", "[{\"name\":\"RESIDENTIAL\",\"value\":1000},{\"name\":\"COMMERCIAL\",\"value\":500}]");

        when(namedParameterJdbcTemplate.queryForMap(eq("SELECT assessments, todaystotalapplications FROM pt_metrics WHERE tenantId = :tenantId"), Mockito.<Map<String, ?>>any()))
                .thenReturn(combinedMetricsResult);

        List<Map<String, Object>> collectionMetricsResult = new ArrayList<>();
        Map<String, Object> colRow = new HashMap<>();
        colRow.put("usage_category", "RESIDENTIAL");
        colRow.put("paymentmode", "ONLINE");
        colRow.put("payment_id", "PAY-101");
        colRow.put("taxheadcode", "PT_TAX");
        colRow.put("tax_head_amount", 15000.0);
        collectionMetricsResult.add(colRow);

        when(namedParameterJdbcTemplate.queryForList(eq("SELECT usage_category, paymentmode, taxheadcode, tax_head_amount FROM pt_collection WHERE tenantId = :tenantId"), Mockito.<Map<String, ?>>any()))
                .thenReturn(collectionMetricsResult);

        // 2. Mock OAuthTokenService response for the API loading phase
        when(oAuthTokenService.getToken()).thenReturn("test-access-token-999");
        when(oAuthTokenService.getUserInfo()).thenReturn(null);

        // 3. Mock external National Dashboard Ingestion API HTTP response
        String apiResponseBody = "{\"status\":\"SUCCESS\",\"code\":200,\"message\":\"Data ingested successfully\"}";
        ResponseEntity<String> httpResponse = new ResponseEntity<>(apiResponseBody, HttpStatus.OK);

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.postForEntity(eq(targetApiUrl), httpEntityCaptor.capture(), eq(String.class)))
                .thenReturn(httpResponse);

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
        HttpEntity<String> capturedEntity = httpEntityCaptor.getValue();
        assertThat(capturedEntity.getHeaders().getContentType().toString()).contains("application/json");

        String sentJsonPayload = capturedEntity.getBody();
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
        verify(producer).push(eq("save-adapter-ingestion-detail"), any(Map.class));
    }

    @Test
    @DisplayName("Complete Flow via REST Controller: User passes date param -> Flow runs -> Returns HTTP 200 OK")
    void testCompleteIngestionFlow_ViaRestController() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 20);

        Map<String, Object> combinedMetricsResult = new LinkedHashMap<>();
        combinedMetricsResult.put("assessments", 10);
        combinedMetricsResult.put("todaystotalapplications", 25);
        when(namedParameterJdbcTemplate.queryForMap(any(String.class), Mockito.<Map<String, ?>>any())).thenReturn(combinedMetricsResult);
        when(namedParameterJdbcTemplate.queryForList(any(String.class), Mockito.<Map<String, ?>>any())).thenReturn(Collections.emptyList());

        when(oAuthTokenService.getToken()).thenReturn("token-abc");
        when(restTemplate.postForEntity(eq(targetApiUrl), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"status\":\"ACCEPTED\"}", HttpStatus.OK));

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

        when(namedParameterJdbcTemplate.queryForMap(any(String.class), Mockito.<Map<String, ?>>any())).thenReturn(new LinkedHashMap<>());
        when(namedParameterJdbcTemplate.queryForList(any(String.class), Mockito.<Map<String, ?>>any())).thenReturn(Collections.emptyList());
        when(oAuthTokenService.getToken()).thenReturn("token-123");

        // Mock API throwing exception (e.g. HTTP 500 Server Error)
        when(restTemplate.postForEntity(eq(targetApiUrl), any(), eq(String.class)))
                .thenThrow(new RuntimeException("National Dashboard API Service Unavailable (500)"));

        // WHEN: Executing flow for targetDate
        List<IngestionResult> results = dailyIngestionService.ingestDailyData(targetDate);

        // THEN: Verify status is FAILURE and last successful date was NOT updated
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("FAILURE");
        assertThat(results.get(0).getFailureReason()).contains("National Dashboard API Service Unavailable");

        verify(summaryRepository, Mockito.never()).saveOrUpdateLastSuccessfulDate(any(), any(), any());
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
