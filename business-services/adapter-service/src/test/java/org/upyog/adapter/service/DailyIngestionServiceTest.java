package org.upyog.adapter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.upyog.adapter.api.AdapterClient;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.extractor.ModuleExtractor;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.registry.ExtractorRegistry;
import org.upyog.adapter.repository.IngestionSummaryRepository;

@ExtendWith(MockitoExtension.class)
class DailyIngestionServiceTest {

    @Mock
    private AdapterClient adapterClient;

    @Mock
    private ExtractorRegistry extractorRegistry;

    @Mock
    private SchemaMappingConfig schemaMappingConfig;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    @Mock
    private ModuleExtractor extractor;

    @InjectMocks
    private DailyIngestionService service;

    @BeforeEach
    void setUp() throws Exception {
        setField(service, "tenantId", "pg");
        setField(service, "defaultStartDateStr", LocalDate.now().minusDays(2).toString());
    }

    @Test
    @DisplayName("Catch-up ingestion runs for missing date range up to yesterday")
    void catchUp_runsForMissingDateRange() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        when(extractorRegistry.get(Module.PT)).thenReturn(extractor);
        when(summaryRepository.findLastSuccessfulDate("pg", "PT")).thenReturn(Optional.of(dayBeforeYesterday.minusDays(1)));
        when(extractor.extractData(any())).thenReturn(DashboardData.builder().module("PT").ulb("pg.citya").build());

        IngestionResult successResult = IngestionResult.builder().ingestionStatus("SUCCESS").build();
        when(adapterClient.execute(any(AdapterRequest.class))).thenReturn(successResult);

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(2); // dayBeforeYesterday and yesterday
        verify(summaryRepository, times(1)).saveOrUpdateLastSuccessfulDate("pg", "PT", dayBeforeYesterday);
        verify(summaryRepository, times(1)).saveOrUpdateLastSuccessfulDate("pg", "PT", yesterday);
    }

    @Test
    @DisplayName("Catch-up ingestion halts on failure")
    void catchUp_haltsOnFailure() throws Exception {
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        when(extractorRegistry.get(Module.PT)).thenReturn(extractor);
        when(summaryRepository.findLastSuccessfulDate("pg", "PT")).thenReturn(Optional.of(dayBeforeYesterday.minusDays(1)));
        when(extractor.extractData(any())).thenReturn(DashboardData.builder().module("PT").ulb("pg.citya").build());

        IngestionResult failureResult = IngestionResult.builder().ingestionStatus("FAILURE").failureReason("Timeout").build();
        when(adapterClient.execute(any(AdapterRequest.class))).thenReturn(failureResult);

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("FAILURE");
        verify(summaryRepository, never()).saveOrUpdateLastSuccessfulDate(any(), any(), any());
    }

    @Test
    @DisplayName("Single date ingestion updates tracker on success")
    void singleDateIngestion_updatesTrackerOnSuccess() {
        LocalDate targetDate = LocalDate.of(2026, 6, 30);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        when(extractorRegistry.get(Module.PT)).thenReturn(extractor);
        when(extractor.extractData(targetDate)).thenReturn(DashboardData.builder().module("PT").ulb("pg.citya").build());

        IngestionResult successResult = IngestionResult.builder().ingestionStatus("SUCCESS").build();
        when(adapterClient.execute(any(AdapterRequest.class))).thenReturn(successResult);

        List<IngestionResult> results = service.ingestDailyData(targetDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("SUCCESS");
        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);
    }

    @Test
    @DisplayName("Catch-up ingestion returns SKIPPED status when already up-to-date")
    void catchUp_returnsSkippedWhenAlreadyUpToDate() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PT));
        when(extractorRegistry.get(Module.PT)).thenReturn(extractor);
        when(summaryRepository.findLastSuccessfulDate("pg", "PT")).thenReturn(Optional.of(yesterday));

        List<IngestionResult> results = service.ingestDailyData();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIngestionStatus()).isEqualTo("SKIPPED");
        assertThat(results.get(0).getFailureReason()).contains("already up-to-date");
        verify(adapterClient, never()).execute(any());
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
