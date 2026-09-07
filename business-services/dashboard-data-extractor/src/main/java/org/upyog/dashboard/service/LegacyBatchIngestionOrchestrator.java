package org.upyog.dashboard.service;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.repository.IngestionSummaryRepository;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.api.DashboardIngestionClient;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.common.constants.Module;
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
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.entity.DailyIngestionData;
import org.upyog.dashboard.enums.IngestionStatus;
import org.upyog.dashboard.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Orchestrator handling the heavy-duty manual legacy batch ingestion processes.
 * <p>
 * This service manages memory-safe streaming of large historical datasets,
 * dynamically routing them either directly to the internal API or uploading
 * physical chunked files to the egov-filestore based on configuration. It
 * strictly enforces concurrency via ShedLock to prevent memory exhaustion from
 * overlapping batch requests.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyBatchIngestionOrchestrator {

    private final LegacyBatchExtractor batchExtractor;
    private final SXSSFExcelGeneratorService excelGeneratorService;
    private final DashboardIngestionClient ingestionClient;
    private final DashboardProperties dashboardProperties;
    private final LockProvider lockProvider;
    private final IngestionPersistenceService persistenceService;
    private final IngestionSummaryRepository summaryRepository;
    private final ExtractorRegistry extractorRegistry;
    private final ObjectMapper objectMapper;

    @Value("${dashboard-data.legacy.batch-size:500}")
    private int batchSize;

    @Value("${dashboard-data.legacy.keep-excel-file:true}")
    private boolean keepExcelFile;

    /**
     * Executes legacy extraction into a single Excel file by streaming DB
     * batches incrementally, then posts the generated Excel file to the legacy
     * API endpoint.
     *
     * @param request legacy batch request containing startDate, endDate, and
     * moduleName
     * @return LegacyIngestionResponse summarizing execution outcome
     */
    public LegacyIngestionResponse processLegacyBatchIngest(LegacyBatchIngestRequest request) {
        String tenantId = dashboardProperties.getTenantId();
        LocalDate start = LocalDate.parse(request.getStartDate());
        LocalDate end = LocalDate.parse(request.getEndDate());
        String moduleName = request.getModuleName();

        if (start.isAfter(end)) {
            String errorMsg = "Invalid date range: startDate (" + start + ") cannot be after endDate (" + end + ")";
            log.warn(errorMsg);
            return LegacyIngestionResponse.builder()
                    .totalDatesRequested(0)
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                            .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
                            .failureReason(errorMsg)
                            .build()))
                    .build();
        }

        // Check for already successfully ingested overlapping legacy records
        List<IngestionSummaryRepository.LegacyJob> overlappingJobs = summaryRepository
                .findOverlappingSuccessfulLegacyJobs(tenantId, moduleName, start, end);

        if (!overlappingJobs.isEmpty()) {
            String overlapMsg = String.format("Request aborted: Legacy data for module '%s' and date range [%s to %s] overlaps with %d already successfully ingested record(s).",
                    moduleName, start, end, overlappingJobs.size());
            log.warn(overlapMsg);
            return LegacyIngestionResponse.builder()
                    .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                    .datesSkipped(overlappingJobs.size())
                    .datesProcessedSuccessfully(0)
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                            .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
                            .failureReason(overlapMsg)
                            .build()))
                    .build();
        }

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
                            .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
                            .failureReason("A batch extraction job is currently in progress for module " + moduleName + ". Please wait for it to complete.")
                            .build()))
                    .build();
        }

        // Register initial legacy job entry with full range and execution date
        persistenceService.createLegacyJob(jobId, tenantId, moduleName, LocalDate.now(), start, end);

        File generatedExcelFile = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);

        // Map tracking per-date candidate status and sample request data across the requested date range
        Map<LocalDate, TargetDateData> targetDateDataMap = new LinkedHashMap<>();
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            // Initialize every date in the requested range with default MISSED_DATE status;
            // if records are discovered during DB extraction, the status will be upgraded accordingly.
            targetDateDataMap.put(currentDate, new TargetDateData(IngestionStatus.MISSED_DATE.getValue(), null, tenantId));
            currentDate = currentDate.plusDays(1);
        }

        try (SXSSFExcelGeneratorService.StreamingExcelSession session = excelGeneratorService.createStreamingSession(moduleName)) {

            Module module = Module.valueOf(moduleName.toUpperCase());
            ModuleExtractor<?> extractor = extractorRegistry != null ? extractorRegistry.get(module) : null;

            if (extractor == null) {
                log.error("Extractor missing for module {}", module);
                return LegacyIngestionResponse.builder()
                        .totalDatesRequested(0)
                        .datesFailed(1)
                        .processedResults(List.of(IngestionResult.builder()
                                .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
                                .failureReason("Extractor missing for module " + module)
                                .build()))
                        .build();
            }
            // Step 1: Extractor queries DB date-by-date and streams rows directly to single Excel session
            long totalExtracted = batchExtractor.extractInBatches(module, start, end, tenantId, batchSize, batchRecords -> {
                List<Object> nonZeroRecords = new ArrayList<>();

                // Analyze records to determine candidate date status and filter non-zero records for Excel
                for (Object record : batchRecords) {
                    LocalDate recordDate = extractDateFromRecord(record);
                    boolean isZero = isRecordZeroMetrics(extractor, record);

                    // Zero-metric records are filtered out so that Excel only contains rows with meaningful data
                    if (!isZero) {
                        nonZeroRecords.add(record);
                    }

                    // Check if the record's date belongs to the requested date range tracking map
                    if (recordDate != null && targetDateDataMap.containsKey(recordDate)) {
                        TargetDateData targetDateData = targetDateDataMap.get(recordDate);
                        String candidateStatus = isZero ? IngestionStatus.SUCCESS_ZERO_METRICS.getValue() : IngestionStatus.SUCCESS.getValue();

                        // If previously marked as MISSED_DATE or SUCCESS_ZERO_METRICS, upgrade to SUCCESS if non-zero data is found for this date
                        if (IngestionStatus.MISSED_DATE.getValue().equals(targetDateData.status)
                                || (IngestionStatus.SUCCESS_ZERO_METRICS.getValue().equals(targetDateData.status) && !isZero)) {
                            targetDateData.status = candidateStatus;
                        }

                        // Store sample request JSON from the first available record for this date for persistence
                        if (targetDateData.samplePayloadJson == null) {
                            try {
                                targetDateData.samplePayloadJson = objectMapper != null ? objectMapper.writeValueAsString(record) : record.toString();
                            } catch (Exception serializationException) {
                                log.error("Failed to serialize sample payload record for module {}: {}", moduleName, serializationException.getMessage());
                                targetDateData.samplePayloadJson = record.toString();
                            }
                        }

                        // Extract tenant ID specific to the record (e.g. ULB) to record the precise tenant hierarchy in detail records
                        String recordTenantId = extractTenantId(record, tenantId);
                        if (recordTenantId != null) {
                            targetDateData.tenantId = recordTenantId;
                        }
                    }
                }

                // If non-zero records are present in this batch chunk, append them to the streaming Excel session
                if (!nonZeroRecords.isEmpty()) {
                    log.info("Streaming DB batch chunk of {} non-zero records to Excel file session (skipped {} zero-metric records)...",
                            nonZeroRecords.size(), batchRecords.size() - nonZeroRecords.size());
                    session.appendBatchRecords(nonZeroRecords);
                } else {
                    log.info("Skipped adding {} zero-metric records to Excel file session.", batchRecords.size());
                }
            });

            if (totalExtracted == 0) {
                log.info("No records found for legacy extraction job {}. Skipping Excel generation.", jobId);
                String emptyResponse = "{\"message\": \"No records found for specified date range\"}";
                persistenceService.updateLegacyJobStatus(jobId, DashboardExtractorConstants.STATUS_SUCCESS, null, emptyResponse);

                // When totalExtracted is 0 (or for any specific calendar date where no data/activity existed in the DB),
                // MISSED_DATE detail entries are persisted into the ingestion_detail table.
                // This explicitly records that the pipeline attempted extraction for that date but found no business transactions/rows,
                // differentiating an empty/inactive date from an extraction failure or an unattempted run.
                persistDateWiseDetails(targetDateDataMap, moduleName, emptyResponse, false);

                return LegacyIngestionResponse.builder()
                        .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                        .datesSkipped(0)
                        .datesProcessedSuccessfully(0)
                        .datesFailed(0)
                        .skippedDates(List.of())
                        .processedResults(List.of(IngestionResult.builder()
                                .ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS)
                                .failureReason("No records found for specified date range")
                                .build()))
                        .build();
            }

            // Step 2: Finalize single combined Excel file
            generatedExcelFile = session.finishWorkbook();

            // Step 3: Send generated Excel file to appropriate endpoint via unified ingestion client
            String legacyMode = dashboardProperties.getEffectiveLegacyUploadMode();
            IngestionResult ingestionResult = ingestionClient.ingest(generatedExcelFile, moduleName, tenantId, legacyMode);

            boolean isSuccess = DashboardExtractorConstants.STATUS_SUCCESS.equalsIgnoreCase(ingestionResult.getIngestionStatus());
            String responseJson = ingestionResult.getResponseData() != null
                    ? ingestionResult.getResponseData()
                    : "{\"failureReason\": \"" + (ingestionResult.getFailureReason() != null ? ingestionResult.getFailureReason().replace("\"", "'") : "Unknown Error") + "\"}";

            // Persist the status and fileStoreId into legacy_data_ingestion_detail
            persistenceService.updateLegacyJobStatus(jobId, ingestionResult.getIngestionStatus(), null, responseJson);

            // Persist per-date detail entries into ingestion_detail
            persistDateWiseDetails(targetDateDataMap, moduleName, responseJson, isSuccess);

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
            String errResponse = "{\"error\": \"" + (exception.getMessage() != null ? exception.getMessage().replace("\"", "'") : "Exception") + "\"}";
            persistenceService.updateLegacyJobStatus(jobId, DashboardExtractorConstants.STATUS_FAILURE, null, errResponse);

            // Persist per-date failure detail entries into ingestion_detail
            persistDateWiseDetails(targetDateDataMap, moduleName, errResponse, false);

            return LegacyIngestionResponse.builder()
                    .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                                .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
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

    /**
     * Persists per-date entries into the ingestion_detail table for every
     * individual day in the legacy batch range, reflecting whether each date
     * was SUCCESS, MISSED_DATE, or FAILURE.
     *
     * @param targetDateDataMap tracking map of candidate statuses per date
     * @param moduleName module short code
     * @param responseData JSON response received from upstream or error details
     * @param overallSuccess boolean flag indicating if the overall batch
     * succeeded
     */
    private void persistDateWiseDetails(Map<LocalDate, TargetDateData> targetDateDataMap, String moduleName, String responseData, boolean overallSuccess) {
        if (targetDateDataMap == null || targetDateDataMap.isEmpty()) {
            return;
        }
        List<DailyIngestionData> detailRecords = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);

        for (Map.Entry<LocalDate, TargetDateData> entry : targetDateDataMap.entrySet()) {
            LocalDate date = entry.getKey();
            TargetDateData targetDateData = entry.getValue();

            String finalStatus;
            if (!overallSuccess && !IngestionStatus.MISSED_DATE.getValue().equals(targetDateData.status)) {
                finalStatus = IngestionStatus.FAILURE.getValue();
            } else {
                finalStatus = targetDateData.status;
            }

            DailyIngestionData detail = DailyIngestionData.builder()
                    .moduleIngestionId(CommonUtils.generateUUID())
                    .tenantId(targetDateData.tenantId)
                    .moduleName(moduleName)
                    .pushDate(date.format(formatter))
                    .requestData(targetDateData.samplePayloadJson)
                    .responseData(responseData)
                    .ingestionStatus(finalStatus)
                    .createdBy("SYSTEM")
                    .lastModifiedBy("SYSTEM")
                    .build();

            detailRecords.add(detail);
        }

        log.info("Saving {} per-date ingestion_detail records for legacy batch {}", detailRecords.size(), moduleName);
        persistenceService.saveIngestionDetailsBatch(detailRecords);
    }

    /**
     * Dynamically extracts the business {@link LocalDate} from a generic record
     * object using reflection.
     *
     * @param item the extracted record object
     * @return parsed LocalDate or null if not extractable
     */
    private LocalDate extractDateFromRecord(Object item) {
        if (item == null) {
            return null;
        }
        try {
            java.lang.reflect.Method getDateMethod = item.getClass().getMethod("getDate");
            Object dateValue = getDateMethod.invoke(item);
            if (dateValue != null) {
                if (dateValue instanceof LocalDate localDate) {
                    return localDate;
                }
                if (dateValue instanceof String dateString && !dateString.isBlank()) {
                    String trimmedDate = dateString.trim();
                    try {
                        return LocalDate.parse(trimmedDate);
                    } catch (Exception parseException) {
                        return LocalDate.parse(trimmedDate, DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Dynamically extracts the tenant ID or ULB from a generic record object
     * using reflection.
     *
     * @param item the extracted record object
     * @param fallback default tenant ID to return if extraction fails
     * @return resolved tenant ID string
     */
    private String extractTenantId(Object item, String fallback) {
        if (item == null) {
            return fallback;
        }
        try {
            java.lang.reflect.Method getUlb = item.getClass().getMethod("getUlb");
            Object val = getUlb.invoke(item);
            if (val != null && !val.toString().isBlank()) {
                return val.toString();
            }
        } catch (Exception ignored) {
        }
        try {
            java.lang.reflect.Method getTenantId = item.getClass().getMethod("getTenantId");
            Object val = getTenantId.invoke(item);
            if (val != null && !val.toString().isBlank()) {
                return val.toString();
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    /**
     * Evaluates if a given extracted record has all zero metric values by
     * delegating to the extractor.
     *
     * @param extractor module extractor capable of evaluating record metrics
     * @param item extracted record object
     * @return true if all metrics are zero, false otherwise
     */
    private boolean isRecordZeroMetrics(ModuleExtractor<?> extractor, Object item) {
        if (item == null) {
            return true;
        }
        if (extractor != null) {
            return extractor.isZeroMetrics(item);
        }
        return false;
    }

    private static class TargetDateData {

        String status;
        String samplePayloadJson;
        String tenantId;

        /**
         * Constructs target date tracking data.
         *
         * @param status candidate ingestion status
         * @param samplePayloadJson sample request JSON payload
         * @param tenantId tenant identifier
         */
        TargetDateData(String status, String samplePayloadJson, String tenantId) {
            this.status = status;
            this.samplePayloadJson = samplePayloadJson;
            this.tenantId = tenantId;
        }
    }
}
