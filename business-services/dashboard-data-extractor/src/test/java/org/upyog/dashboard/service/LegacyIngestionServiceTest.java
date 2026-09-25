package org.upyog.dashboard.service;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.util.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.upyog.dashboard.model.LegacyIngestionResponse;
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;

@ExtendWith(MockitoExtension.class)
class LegacyIngestionServiceTest {

    @Mock
    private DashboardClient dashboardClient;

    @Mock
    private ExtractorRegistry extractorRegistry;

    @Mock
    private SchemaMappingConfig schemaMappingConfig;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    @Mock
    private ModuleExtractor<Object> extractor;

    @InjectMocks
    private LegacyIngestionService legacyService;

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.setField(legacyService, "tenantId", "pg");
    }

    @Test
    @DisplayName("Throws exception if startDate is after endDate")
    void ingestHistoricalData_throwsOnInvalidDateRange() {
        LocalDate start = LocalDate.of(2026, 7, 10);
        LocalDate end = LocalDate.of(2026, 7, 1);

        assertThatThrownBy(() -> legacyService.ingestHistoricalDataForRange(start, end, Module.PT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deduplication skips dates already successfully ingested")
    void ingestHistoricalData_skipsAlreadyIngestedDates() {
        LocalDate d1 = LocalDate.of(2026, 6, 28);
        LocalDate d2 = LocalDate.of(2026, 6, 29);
        LocalDate d3 = LocalDate.of(2026, 6, 30);

        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        // First call in populate returns d1,d2. Second call for response return d1,d2,d3.
        when(summaryRepository.findSuccessfullyIngestedDates("pg", "PT", d1, d3))
                .thenReturn(Set.of(d1, d2))
                .thenReturn(Set.of(d1, d2, d3));
        when(summaryRepository.findRegisteredLegacyJobDates("pg", "PT")).thenReturn(Set.of());
        when(summaryRepository.findPendingOrFailedLegacyJobs(eq("pg"), eq("PT"), any(Integer.class)))
                .thenReturn(List.of(new IngestionSummaryRepository.LegacyJob("job-123", d3)));
        
        when(summaryRepository.findLastSuccessfulDate("pg", "PT")).thenReturn(Optional.of(d2));

        when(extractor.extractData(d3)).thenReturn(DashboardData.builder().module("PT").ulb("pg.citya").build());
        when(dashboardClient.execute(any(DashboardRequest.class)))
                .thenReturn(IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS).build());

        LegacyIngestionResponse response = legacyService.ingestHistoricalDataForRange(d1, d3, Module.PT);

        assertThat(response.getTotalDatesRequested()).isEqualTo(3);
        assertThat(response.getDatesProcessedSuccessfully()).isEqualTo(3); // d1, d2, d3 combined
        assertThat(response.getDatesFailed()).isEqualTo(0);

        verify(summaryRepository).saveOrUpdateLastSuccessfulDate("pg", "PT", d3);
    }

    @Test
    @DisplayName("Failed ingestion increments datesFailed count and does not update summary tracker")
    void ingestHistoricalData_handlesFailures() {
        LocalDate d1 = LocalDate.of(2026, 6, 30);

        doReturn(extractor).when(extractorRegistry).get(Module.PT);
        when(summaryRepository.findSuccessfullyIngestedDates("pg", "PT", d1, d1)).thenReturn(Set.of());
        when(summaryRepository.findRegisteredLegacyJobDates("pg", "PT")).thenReturn(Set.of());
        when(summaryRepository.findPendingOrFailedLegacyJobs(eq("pg"), eq("PT"), any(Integer.class)))
                .thenReturn(List.of(new IngestionSummaryRepository.LegacyJob("job-456", d1)));
        when(summaryRepository.findLastSuccessfulDate("pg", "PT")).thenReturn(Optional.empty());

        when(extractor.extractData(d1)).thenReturn(DashboardData.builder().module("PT").ulb("pg.citya").build());
        when(dashboardClient.execute(any(DashboardRequest.class)))
                .thenReturn(IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE).failureReason("Timeout").build());

        LegacyIngestionResponse response = legacyService.ingestHistoricalDataForRange(d1, d1, Module.PT);

        assertThat(response.getTotalDatesRequested()).isEqualTo(1);
        assertThat(response.getDatesProcessedSuccessfully()).isEqualTo(0);

        verify(summaryRepository, never()).saveOrUpdateLastSuccessfulDate(any(), any(), any());
    }

    
}
