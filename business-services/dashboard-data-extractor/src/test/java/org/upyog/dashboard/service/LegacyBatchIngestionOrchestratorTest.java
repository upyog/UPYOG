package org.upyog.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.api.DashboardIngestionClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardExtractorProperties;
import org.upyog.dashboard.entity.DailyIngestionData;
import org.upyog.dashboard.extractor.LegacyBatchExtractor;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.LegacyIngestionResponse;
import org.upyog.dashboard.pt.dto.PTDTO;
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

/**
 * Unit tests for {@link LegacyBatchIngestionOrchestrator} testing async job execution and distributed locking.
 */
@ExtendWith(MockitoExtension.class)
class LegacyBatchIngestionOrchestratorTest {

    @Mock
    private LegacyBatchExtractor batchExtractor;

    @Mock
    private SXSSFExcelGeneratorService excelGeneratorService;

    @Mock
    private DashboardIngestionClient ingestionClient;

    @Mock
    private DashboardExtractorProperties dashboardProperties;

    @Mock
    private LockProvider lockProvider;

    @Mock
    private IngestionPersistenceService persistenceService;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    @Mock
    private ExtractorRegistry extractorRegistry;

    @Mock
    private ModuleExtractor<Object> ptExtractor;

    @Mock
    private TenantSyncService tenantSyncService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LegacyBatchIngestionOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        lenient().when(summaryRepository.hasAnyModuleDetails()).thenReturn(true);
        orchestrator = new LegacyBatchIngestionOrchestrator(
                batchExtractor,
                excelGeneratorService,
                ingestionClient,
                dashboardProperties,
                lockProvider,
                persistenceService,
                summaryRepository,
                extractorRegistry,
                objectMapper,
                tenantSyncService
        );
    }

    @Test
    @DisplayName("processLegacyBatchIngest saves date-wise status with SUCCESS, SUCCESS_ZERO_METRICS, and MISSED_DATE")
    @SuppressWarnings("unchecked")
    void processLegacyBatchIngest_persistsDateWiseStatuses() throws Exception {
        when(dashboardProperties.getTenantId()).thenReturn("pg.citya");
        when(dashboardProperties.getEffectiveLegacyUploadMode()).thenReturn("S3");
        when(dashboardProperties.getLegacyBatchSize()).thenReturn(500);

        SimpleLock mockLock = mock(SimpleLock.class);
        when(lockProvider.lock(any(LockConfiguration.class))).thenReturn(Optional.of(mockLock));

        when(summaryRepository.findOverlappingSuccessfulLegacyJobs(anyString(), anyString(), any(), any()))
                .thenReturn(List.of());

        SXSSFExcelGeneratorService.StreamingExcelSession session = mock(SXSSFExcelGeneratorService.StreamingExcelSession.class);
        when(excelGeneratorService.createStreamingSession(anyString(), anyString())).thenReturn(session);
        File tempFile = new File("test.xlsx");
        when(session.finishWorkbook()).thenReturn(tempFile);

        when(extractorRegistry.get(Module.PT)).thenReturn((ModuleExtractor) ptExtractor);

        // Record for 2025-01-01 has non-zero metrics -> ptExtractor.isZeroMetrics returns false
        PTDTO ptDtoDay1 = PTDTO.builder().date("2025-01-01").ulb("pg.citya").module("PT").build();
        when(ptExtractor.isZeroMetrics(ptDtoDay1)).thenReturn(false);

        // Record for 2025-01-02 has zero metrics -> ptExtractor.isZeroMetrics returns true
        PTDTO ptDtoDay2 = PTDTO.builder().date("2025-01-02").ulb("pg.citya").module("PT").build();
        when(ptExtractor.isZeroMetrics(ptDtoDay2)).thenReturn(true);

        // 2025-01-03 has no records extracted -> MISSED_DATE

        when(batchExtractor.extractInBatches(eq(Module.PT), eq(LocalDate.parse("2025-01-01")), eq(LocalDate.parse("2025-01-03")), anyList(), anyInt(), any()))
                .thenAnswer(invocation -> {
                    Consumer<List<Object>> consumer = invocation.getArgument(5);
                    consumer.accept(List.of(ptDtoDay1, ptDtoDay2));
                    return 2L;
                });

        IngestionResult s3Result = IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .responseData("{\"fileStoreId\": \"file-123\", \"bucket\": \"test-bucket\"}")
                .build();
        when(ingestionClient.ingest(eq(tempFile), eq("PT"), eq("pg.citya"), eq("S3"))).thenReturn(s3Result);

        LegacyBatchIngestRequest request = LegacyBatchIngestRequest.builder()
                .moduleName("PT")
                .startDate("2025-01-01")
                .endDate("2025-01-03")
                .build();

        LegacyIngestionResponse response = orchestrator.processLegacyBatchIngest(request);

        assertThat(response.getDatesProcessedSuccessfully()).isEqualTo(1);
        assertThat(response.getDatesFailed()).isEqualTo(0);

        // Verify that only the non-zero record (day 1) was appended to the Excel session, day 2 was skipped
        verify(session).appendBatchRecords(List.of(ptDtoDay1));

        ArgumentCaptor<List<DailyIngestionData>> captor = ArgumentCaptor.forClass(List.class);
        verify(persistenceService).saveIngestionDetailsBatch(captor.capture());

        List<DailyIngestionData> savedDetails = captor.getValue();
        assertThat(savedDetails).hasSize(3);

        DailyIngestionData day1 = savedDetails.stream().filter(a -> "01-01-2025".equals(a.getPushDate()) || "2025-01-01".equals(a.getPushDate())).findFirst().orElseThrow();
        assertThat(day1.getIngestionStatus()).isEqualTo("SUCCESS");

        DailyIngestionData day2 = savedDetails.stream().filter(a -> "02-01-2025".equals(a.getPushDate()) || "2025-01-02".equals(a.getPushDate())).findFirst().orElseThrow();
        assertThat(day2.getIngestionStatus()).isEqualTo("SUCCESS_ZERO_METRICS");

        DailyIngestionData day3 = savedDetails.stream().filter(a -> "03-01-2025".equals(a.getPushDate()) || "2025-01-03".equals(a.getPushDate())).findFirst().orElseThrow();
        assertThat(day3.getIngestionStatus()).isEqualTo("MISSED_DATE");
    }

    @Test
    @DisplayName("processLegacyBatchIngest handles multiple tenants across dates and aggregates per-date per-tenant records")
    @SuppressWarnings("unchecked")
    void processLegacyBatchIngest_multipleTenants() throws Exception {
        when(dashboardProperties.getTenantId()).thenReturn("pg");
        when(dashboardProperties.getEffectiveLegacyUploadMode()).thenReturn("S3");
        when(dashboardProperties.getLegacyBatchSize()).thenReturn(50);

        SimpleLock mockLock = mock(SimpleLock.class);
        when(lockProvider.lock(any(LockConfiguration.class))).thenReturn(Optional.of(mockLock));

        when(summaryRepository.findOverlappingSuccessfulLegacyJobs(anyString(), anyString(), any(), any()))
                .thenReturn(List.of());

        SXSSFExcelGeneratorService.StreamingExcelSession session = mock(SXSSFExcelGeneratorService.StreamingExcelSession.class);
        when(excelGeneratorService.createStreamingSession(anyString(), anyString())).thenReturn(session);
        File tempFile = new File("test-multitenant.xlsx");
        when(session.finishWorkbook()).thenReturn(tempFile);

        when(extractorRegistry.get(Module.PT)).thenReturn((ModuleExtractor) ptExtractor);

        PTDTO ptDtoCityA = PTDTO.builder().date("2025-01-01").ulb("pg.citya").module("PT").build();
        PTDTO ptDtoCityB = PTDTO.builder().date("2025-01-01").ulb("pg.cityb").module("PT").build();
        when(ptExtractor.isZeroMetrics(ptDtoCityA)).thenReturn(false);
        when(ptExtractor.isZeroMetrics(ptDtoCityB)).thenReturn(true);

        when(batchExtractor.extractInBatches(eq(Module.PT), eq(LocalDate.parse("2025-01-01")), eq(LocalDate.parse("2025-01-02")), anyList(), anyInt(), any()))
                .thenAnswer(invocation -> {
                    Consumer<List<Object>> consumer = invocation.getArgument(5);
                    consumer.accept(List.of(ptDtoCityA, ptDtoCityB));
                    return 2L;
                });

        IngestionResult s3Result = IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .responseData("{\"fileStoreId\": \"file-456\"}")
                .build();
        // jobTenantId for multi-tenant should be state level "pg"
        when(ingestionClient.ingest(eq(tempFile), eq("PT"), eq("pg"), eq("S3"))).thenReturn(s3Result);

        LegacyBatchIngestRequest request = LegacyBatchIngestRequest.builder()
                .moduleName("PT")
                .tenantIds(List.of("pg.citya", "pg.cityb"))
                .startDate("2025-01-01")
                .endDate("2025-01-02")
                .build();

        LegacyIngestionResponse response = orchestrator.processLegacyBatchIngest(request);

        assertThat(response.getDatesProcessedSuccessfully()).isEqualTo(1);
        assertThat(response.getDatesFailed()).isEqualTo(0);

        // Only non-zero record (CityA) streamed to Excel
        verify(session).appendBatchRecords(List.of(ptDtoCityA));

        ArgumentCaptor<List<DailyIngestionData>> captor = ArgumentCaptor.forClass(List.class);
        verify(persistenceService).saveIngestionDetailsBatch(captor.capture());

        List<DailyIngestionData> savedDetails = captor.getValue();
        // 2 dates x 2 tenants = 4 detail records
        assertThat(savedDetails).hasSize(4);

        DailyIngestionData cityADay1 = savedDetails.stream()
                .filter(d -> ("01-01-2025".equals(d.getPushDate()) || "2025-01-01".equals(d.getPushDate())) && "pg.citya".equals(d.getTenantId()))
                .findFirst().orElseThrow();
        assertThat(cityADay1.getIngestionStatus()).isEqualTo("SUCCESS");

        DailyIngestionData cityBDay1 = savedDetails.stream()
                .filter(d -> ("01-01-2025".equals(d.getPushDate()) || "2025-01-01".equals(d.getPushDate())) && "pg.cityb".equals(d.getTenantId()))
                .findFirst().orElseThrow();
        assertThat(cityBDay1.getIngestionStatus()).isEqualTo("SUCCESS_ZERO_METRICS");

        DailyIngestionData cityADay2 = savedDetails.stream()
                .filter(d -> ("02-01-2025".equals(d.getPushDate()) || "2025-01-02".equals(d.getPushDate())) && "pg.citya".equals(d.getTenantId()))
                .findFirst().orElseThrow();
        assertThat(cityADay2.getIngestionStatus()).isEqualTo("MISSED_DATE");

        DailyIngestionData cityBDay2 = savedDetails.stream()
                .filter(d -> ("02-01-2025".equals(d.getPushDate()) || "2025-01-02".equals(d.getPushDate())) && "pg.cityb".equals(d.getTenantId()))
                .findFirst().orElseThrow();
        assertThat(cityBDay2.getIngestionStatus()).isEqualTo("MISSED_DATE");
    }
}

