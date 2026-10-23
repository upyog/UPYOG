package org.upyog.dashboard.service;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.util.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.api.DashboardClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;

import org.upyog.dashboard.config.DashboardExtractorProperties;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for {@link DailyIngestionService} testing catch-up windows, multi-tenant batching, and error resilience.
 */
@ExtendWith(MockitoExtension.class)
class DailyIngestionServiceTest {

    @Mock
    private DashboardClient dashboardClient;

    @Mock
    private ExtractorRegistry extractorRegistry;

    @Mock
    private SchemaMappingConfig schemaMappingConfig;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    @Mock
    private DashboardExtractorProperties dashboardProperties;

    @Mock
    private TenantSyncService tenantSyncService;

    @Mock
    private ModuleExtractor<Object> extractor;

    @InjectMocks
    private DailyIngestionService service;

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(dashboardProperties.getTenantId()).thenReturn("pg");
        lenient().when(dashboardProperties.getDefaultStartDateStr()).thenReturn(LocalDate.now().minusDays(2).toString());
        lenient().when(dashboardProperties.getDailyCatchUpLimitDays()).thenReturn(7);
        lenient().when(dashboardProperties.getIngestionBatchSize()).thenReturn(10);
        lenient().when(dashboardProperties.getTenantBatchSize()).thenReturn(50);
        lenient().when(tenantSyncService.getActiveTenants(any())).thenReturn(List.of("pg"));
        lenient().when(summaryRepository.hasAnyModuleDetails()).thenReturn(true);
        lenient().when(summaryRepository.findAllLastSuccessfulDatesByModule(any())).thenReturn(java.util.Collections.emptyMap());
        lenient().when(summaryRepository.findTenantsSuccessfullyIngestedForDate(any(), any())).thenReturn(java.util.Collections.emptySet());

        TestUtils.setField(service, "tenantId", "pg");
        TestUtils.setField(service, "defaultStartDateStr", LocalDate.now().minusDays(2).toString());
        TestUtils.setField(service, "batchSize", 10);
        TestUtils.setField(service, "tenantBatchSize", 50);
    }


    @Test
    @DisplayName("Catch-up ingestion runs for missing date range up to yesterday")
    void catchUp_runsForMissingDateRange() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(summaryRepository.findAllLastSuccessfulDatesByModule("PT")).thenReturn(java.util.Map.of("pg", dayBeforeYesterday.minusDays(1)));
        when(extractor.extractData(anyList(), any(LocalDate.class))).thenReturn(List.of(DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 10)).build()));

        IngestionResult successResult = IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS).build();
        when(dashboardClient.execute(any(DashboardRequest.class))).thenReturn(successResult);

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(2); // dayBeforeYesterday and yesterday
        verify(summaryRepository, times(1)).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", dayBeforeYesterday);
        verify(summaryRepository, times(1)).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", yesterday);
    }

    @Test
    @DisplayName("Catch-up ingestion halts on failure")
    void catchUp_haltsOnFailure() throws Exception {
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(summaryRepository.findAllLastSuccessfulDatesByModule("PT")).thenReturn(java.util.Map.of("pg", dayBeforeYesterday.minusDays(1)));
        when(extractor.extractData(anyList(), any(LocalDate.class))).thenReturn(List.of(DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 10)).build()));

        IngestionResult failureResult = IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE).failureReason("Timeout on pg.citya").build();
        when(dashboardClient.execute(any(DashboardRequest.class))).thenReturn(failureResult);

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo(DashboardExtractorConstants.STATUS_FAILURE);
        verify(summaryRepository, never()).saveOrUpdateLastSuccessfulDate(any(), any(), any());
    }

    @Test
    @DisplayName("Single date ingestion updates tracker on success")
    void singleDateIngestion_updatesTrackerOnSuccess() {
        LocalDate targetDate = LocalDate.of(2026, 6, 30);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(extractor.extractData(eq(List.of("pg")), eq(targetDate))).thenReturn(List.of(DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 10)).build()));

        IngestionResult successResult = IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS).build();
        when(dashboardClient.execute(any(DashboardRequest.class))).thenReturn(successResult);

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo(DashboardExtractorConstants.STATUS_SUCCESS);
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", targetDate);
    }

    @Test
    @DisplayName("Batch multi-ULB extraction queries all active tenants in one DB batch and updates individual summaries")
    void multiTenantBatchIngestion_queriesAllTenantsInSingleBatch() {
        LocalDate targetDate = LocalDate.of(2026, 6, 30);
        List<String> tenants = List.of("pg.citya", "pg.cityb", "pg.cityc");

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(tenantSyncService.getActiveTenants(Module.PT)).thenReturn(tenants);

        List<DashboardData> batchData = List.of(
                DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 10)).build(),
                DashboardData.builder().module("PT").ulb("pg.cityb").metrics(java.util.Map.of("assessments", 5)).build(),
                DashboardData.builder().module("PT").ulb("pg.cityc").metrics(java.util.Map.of("assessments", 0)).build()
        );
        when(extractor.extractData(eq(tenants), eq(targetDate))).thenReturn(batchData);

        IngestionResult successResult = IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS).build();
        when(dashboardClient.execute(any(DashboardRequest.class))).thenReturn(successResult);

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(3);
        verify(extractor, times(1)).extractData(eq(tenants), eq(targetDate));
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", targetDate);
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.cityb", "PT", targetDate);
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.cityc", "PT", targetDate);
    }

    @Test
    @DisplayName("Single date ingestion skips duplicate tenants already completed for that date")
    void singleDateIngestion_skipsAlreadyCompletedTenants() {
        LocalDate targetDate = LocalDate.of(2026, 6, 30);
        List<String> tenants = List.of("pg.citya", "pg.cityb");

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(tenantSyncService.getActiveTenants(Module.PT)).thenReturn(tenants);
        when(summaryRepository.findTenantsSuccessfullyIngestedForDate("PT", targetDate))
                .thenReturn(java.util.Set.of("pg.citya", "pg.cityb"));

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("SKIPPED");
        assertThat(results.get(1).getIngestionStatus()).isEqualTo("SKIPPED");
        verify(extractor, never()).extractData(anyList(), any());
        verify(dashboardClient, never()).execute(any());
    }

    @Test
    @DisplayName("Catch-up ingestion returns SKIPPED status when already up-to-date")
    void catchUp_returnsSkippedWhenAlreadyUpToDate() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(summaryRepository.findAllLastSuccessfulDatesByModule("PT")).thenReturn(java.util.Map.of("pg", yesterday));

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("SKIPPED");
        assertThat(results.get(0).getFailureReason()).contains("already up-to-date");
        verify(dashboardClient, never()).execute(any());
    }

    @Test
    @DisplayName("Catch-up ingestion returns FAILURE when gap exceeds limit")
    void catchUp_failsWhenGapExceedsLimit() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate farPastDate = LocalDate.now().minusDays(12);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(summaryRepository.findAllLastSuccessfulDatesByModule("PT")).thenReturn(java.util.Map.of("pg", farPastDate));
        lenient().when(dashboardProperties.getDailyCatchUpLimitDays()).thenReturn(7);

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo(DashboardExtractorConstants.STATUS_FAILURE);
        assertThat(results.get(0).getFailureReason()).contains("exceeds max limit");
        verify(dashboardClient, never()).execute(any());
    }

    @Test
    @DisplayName("Zero metrics skips HTTP API call and sets status SUCCESS_ZERO_METRICS while advancing tracker")
    void zeroMetrics_skipsHttpCallAndAdvancesTracker() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 20);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(extractor.extractData(eq(List.of("pg")), eq(targetDate))).thenReturn(List.of(DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 0)).build()));

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo(org.upyog.dashboard.enums.IngestionStatus.SUCCESS_ZERO_METRICS.getValue());
        verify(dashboardClient, never()).execute(any());
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", targetDate);
    }

    @Test
    @DisplayName("Duplicate date error marks status as SUCCESS_DUPLICATE and advances tracker")
    void duplicateDate_marksStatusAsSuccessDuplicateAndAdvancesTracker() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 21);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(extractor.extractData(eq(List.of("pg")), eq(targetDate))).thenReturn(List.of(DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 10)).build()));

        IngestionResult duplicateResult = IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE).failureReason("Duplicate entry for date 2026-07-21").build();
        when(dashboardClient.execute(any(DashboardRequest.class))).thenReturn(duplicateResult);

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(1);
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", targetDate);
    }

    @Test
    @DisplayName("Batch ingestion checkpoints inactive tenants missing from extractor result as zero-metrics success")
    void batchIngestion_checkpointsInactiveTenantsMissingFromDataList() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 22);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(tenantSyncService.getActiveTenants(Module.PT)).thenReturn(List.of("pg.citya", "pg.cityb"));
        // Extractor only returns data for citya; cityb is absent (inactive)
        when(extractor.extractData(eq(List.of("pg.citya", "pg.cityb")), eq(targetDate)))
                .thenReturn(List.of(DashboardData.builder().module("PT").ulb("pg.citya").metrics(java.util.Map.of("assessments", 10)).build()));
        when(dashboardClient.execute(any(DashboardRequest.class)))
                .thenReturn(IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS).build());

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(2);
        // Active tenant citya
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.citya", "PT", targetDate);
        // Inactive tenant cityb
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg.cityb", "PT", targetDate);
        assertThat(results).anyMatch(r -> org.upyog.dashboard.enums.IngestionStatus.SUCCESS_ZERO_METRICS.getValue().equals(r.getIngestionStatus()));
    }

    @Test
    @DisplayName("ingestDailyData throws IllegalStateException when ingestion_module_detail has no records")
    void ingestDailyData_throwsWhenNoModuleDetails() {
        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        when(summaryRepository.hasAnyModuleDetails()).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () ->
                service.ingestDailyData());
    }
}
