package org.upyog.dashboard.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.api.DashboardIngestionClient;
import org.upyog.dashboard.api.FileStoreClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.extractor.LegacyBatchExtractor;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.LegacyIngestionResponse;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Orchestrator handling the heavy-duty manual legacy batch ingestion processes.
 * <p>
 * This service manages memory-safe streaming of large historical datasets, dynamically routing them
 * either directly to the internal API or uploading physical chunked files to the egov-filestore based on configuration.
 * It strictly enforces concurrency via ShedLock to prevent memory exhaustion from overlapping batch requests.
 * </p>
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyBatchIngestionOrchestrator {

    private final LegacyBatchExtractor batchExtractor;
    private final SXSSFExcelGeneratorService excelGeneratorService;
    private final DashboardIngestionClient ingestionClient;
    private final FileStoreClient fileStoreClient;
    private final DashboardProperties dashboardProperties;
    private final LockProvider lockProvider;

    @Value("${dashboard-data.legacy.batch-size:500}")
    private int batchSize;

    @Value("${dashboard-data.legacy.keep-excel-file:true}")
    private boolean keepExcelFile;

    /**
     * Executes legacy extraction into a single Excel file by streaming DB batches incrementally,
     * then posts the generated Excel file to the legacy API endpoint.
     *
     * @param request legacy batch request containing startDate, endDate, and moduleName
     * @return LegacyIngestionResponse summarizing execution outcome
     */
    public LegacyIngestionResponse processLegacyBatchIngest(LegacyBatchIngestRequest request) {
        String tenantId = dashboardProperties.getTenantId();
        LocalDate start = LocalDate.parse(request.getStartDate());
        LocalDate end = LocalDate.parse(request.getEndDate());
        String moduleName = request.getModuleName();
        String jobId = "JOB-" + moduleName.toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 8);

        log.info("Processing legacy batch ingestion job {} for module {} (date range: {} to {}, tenantId: {}, batchChunkSize: {})",
                jobId, moduleName, start, end, tenantId, batchSize);

        String lockName = "manual_batch_extraction_" + moduleName.toUpperCase();
        LockConfiguration lockConfig = new LockConfiguration(
                Instant.now(), 
                lockName, 
                Duration.ofHours(3), 
                Duration.ofMinutes(1)
        );

        Optional<SimpleLock> lock = lockProvider.lock(lockConfig);
        if (lock.isEmpty()) {
            log.warn("Job {} aborted: A batch extraction job is already running for module {}", jobId, moduleName);
            return LegacyIngestionResponse.builder()
                    .totalDatesRequested(0)
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                            .ingestionStatus("FAILURE")
                            .failureReason("A batch extraction job is currently in progress for module " + moduleName + ". Please wait for it to complete.")
                            .build()))
                    .build();
        }

        File generatedExcelFile = null;

        try (SXSSFExcelGeneratorService.StreamingExcelSession session = excelGeneratorService.createStreamingSession(moduleName)) {

            // Step 1: Extractor queries DB date-by-date and streams rows directly to single Excel session
            long totalExtracted = batchExtractor.extractInBatches(start, end, tenantId, batchSize, batchRecords -> {
                log.info("Streaming DB batch chunk of {} records to Excel file session...", batchRecords.size());
                session.appendBatchRecords(batchRecords);
            });

            if (totalExtracted == 0) {
                log.info("No records found for legacy extraction job {}. Skipping Excel generation.", jobId);
                return LegacyIngestionResponse.builder()
                        .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                        .datesSkipped(0)
                        .datesProcessedSuccessfully(0)
                        .datesFailed(0)
                        .skippedDates(List.of())
                        .processedResults(List.of(IngestionResult.builder()
                                .ingestionStatus("SUCCESS")
                                .failureReason("No records found for specified date range")
                                .build()))
                        .build();
            }

            // Step 2: Finalize single combined Excel file
            generatedExcelFile = session.finishWorkbook();

            // Step 3: Send generated Excel file to appropriate endpoint
            IngestionResult ingestionResult;
            if ("FILESTORE".equalsIgnoreCase(dashboardProperties.getUploadMode())) {
                String fileStoreId = fileStoreClient.uploadFile(generatedExcelFile, tenantId, moduleName);
                if (fileStoreId != null) {
                    ingestionResult = IngestionResult.builder()
                            .ingestionStatus("SUCCESS")
                            .responseData("{\"fileStoreId\": \"" + fileStoreId + "\"}")
                            .build();
                } else {
                    ingestionResult = IngestionResult.builder()
                            .ingestionStatus("FAILURE")
                            .failureReason("Failed to upload Excel file to egov-filestore")
                            .build();
                }
            } else {
                ingestionResult = ingestionClient.uploadLegacyExcelFile(generatedExcelFile, moduleName, tenantId);
            }

            boolean isSuccess = "SUCCESS".equalsIgnoreCase(ingestionResult.getIngestionStatus());

            return LegacyIngestionResponse.builder()
                    .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                    .datesSkipped(0)
                    .datesProcessedSuccessfully(isSuccess ? 1 : 0)
                    .datesFailed(isSuccess ? 0 : 1)
                    .skippedDates(List.of())
                    .processedResults(List.of(ingestionResult))
                    .build();

        } catch (Exception exception) {
            log.error("Error executing streaming legacy batch ingestion job {}: {}", jobId, exception.getMessage(), exception);
            return LegacyIngestionResponse.builder()
                    .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                            .ingestionStatus("FAILURE")
                            .failureReason(exception.getMessage())
                            .build()))
                    .build();
        } finally {
            if (generatedExcelFile != null && generatedExcelFile.exists()) {
                if (keepExcelFile) {
                    log.info("PRESERVED single legacy Excel file at: {}", generatedExcelFile.getAbsolutePath());
                } else {
                    boolean deleted = generatedExcelFile.delete();
                    log.info("Temporary Excel file deletion status for job {}: {}", jobId, deleted);
                }
            }
            lock.get().unlock();
            log.info("Released ShedLock for module {}", moduleName);
        }
    }
}
