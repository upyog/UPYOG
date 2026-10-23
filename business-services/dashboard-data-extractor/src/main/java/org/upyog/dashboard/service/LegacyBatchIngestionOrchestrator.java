package org.upyog.dashboard.service;

import org.apache.commons.lang3.StringUtils;
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
import org.upyog.dashboard.config.DashboardExtractorProperties;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.extractor.LegacyBatchExtractor;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.IngestionSchedulerDetail;
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
    private final DashboardExtractorProperties dashboardProperties;
    private final LockProvider lockProvider;
    private final IngestionPersistenceService persistenceService;
    private final IngestionSummaryRepository summaryRepository;
    private final ExtractorRegistry extractorRegistry;
    private final ObjectMapper objectMapper;
    private final TenantSyncService tenantSyncService;

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
        LocalDate start = LocalDate.parse(request.getStartDate());
        LocalDate end = LocalDate.parse(request.getEndDate());
        String moduleName = request.getModuleName();

        Module module;
        try {
            module = Module.valueOf(moduleName.toUpperCase());
        } catch (Exception exception) {
            String errorMsg = "Invalid module name: " + moduleName;
            log.error(errorMsg);
            return LegacyIngestionResponse.builder()
                    .totalDatesRequested(0)
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                            .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
                            .failureReason(errorMsg)
                            .build()))
                    .build();
        }

        List<String> targetTenants = new ArrayList<>();
        if (request.getTenantIds() != null && !request.getTenantIds().isEmpty()) {
            targetTenants.addAll(request.getTenantIds());
        } else if (StringUtils.isNotBlank(request.getTenantId())) {
            String reqTenant = request.getTenantId().trim();
            if (reqTenant.contains(",")) {
                for (String t : reqTenant.split(",")) {
                    if (StringUtils.isNotBlank(t)) {
                        targetTenants.add(t.trim());
                    }
                }
            } else if (!reqTenant.equalsIgnoreCase(dashboardProperties.getTenantId())) {
                targetTenants.add(reqTenant);
            }
        }

        if (targetTenants.isEmpty() && tenantSyncService != null) {
            try {
                List<String> activeTenants = tenantSyncService.getActiveTenants(module);
                if (activeTenants != null && !activeTenants.isEmpty()) {
                    targetTenants.addAll(activeTenants);
                }
            } catch (Exception exception) {
                log.warn("Could not resolve active tenants for module {}: {}", moduleName, exception.getMessage());
            }
        }
        if (targetTenants.isEmpty()) {
            targetTenants.add(dashboardProperties.getTenantId());
        }

        final String jobTenantId = (targetTenants.size() == 1 && targetTenants.get(0).contains("."))
                ? targetTenants.get(0)
                : dashboardProperties.getTenantId();

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

        if (!summaryRepository.hasAnyModuleDetails()) {
            String errorMsg = "No tenant configuration found in ug_ingestion_module_detail table. Please run MDMS tenant sync API (POST /extractor/v1/tenants/_sync) first.";
            log.error(errorMsg);
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
                .findOverlappingSuccessfulLegacyJobs(jobTenantId, moduleName, start, end);

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

        int batchSize = dashboardProperties.getLegacyBatchSize();
        log.info("Processing legacy batch ingestion job {} for module {} (date range: {} to {}, jobTenantId: {}, targetTenants: {}, batchChunkSize: {})",
                jobId, moduleName, start, end, jobTenantId, targetTenants, batchSize);

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
        persistenceService.createLegacyJob(jobId, jobTenantId, moduleName, LocalDate.now(), start, end);

        long startTime = CommonUtils.getCurrentEpochMillis();
        IngestionSchedulerDetail schedulerDetail = IngestionSchedulerDetail.builder()
                .schedulerId(jobId)
                .schedulerName("LEGACY_POPULATE_SCHEDULER")
                .startTime(startTime)
                .status(DashboardExtractorConstants.STATUS_RUNNING)
                .totalRecordsProcessed(0)
                .successRecordsCount(0)
                .failureRecordsCount(0)
                .createdBy(DashboardExtractorConstants.SYSTEM_USER)
                .createdTime(startTime)
                .lastModifiedBy(DashboardExtractorConstants.SYSTEM_USER)
                .lastModifiedTime(startTime)
                .build();
        summaryRepository.createSchedulerRun(schedulerDetail);

        File generatedFile = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);

        // Map tracking per-date and per-tenant candidate status and sample request data across the requested date range
        Map<LocalDate, Map<String, TargetDateData>> targetDateDataMap = new LinkedHashMap<>();
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            Map<String, TargetDateData> tenantDataMap = new LinkedHashMap<>();
            for (String targetTenant : targetTenants) {
                tenantDataMap.put(targetTenant, new TargetDateData(IngestionStatus.MISSED_DATE.getValue(), null, targetTenant));
            }
            targetDateDataMap.put(currentDate, tenantDataMap);
            currentDate = currentDate.plusDays(1);
        }

        try (SXSSFExcelGeneratorService.StreamingExcelSession session = excelGeneratorService.createStreamingSession(moduleName, DashboardExtractorConstants.LEGACY)) {

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
            // Step 1: Extractor queries DB date-by-date across all target tenants and streams rows directly to single Excel session
            long totalExtracted = batchExtractor.extractInBatches(module, start, end, targetTenants, batchSize, batchRecords -> {
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
                        String recordTenantId = extractTenantId(record, jobTenantId);
                        Map<String, TargetDateData> tenantMap = targetDateDataMap.get(recordDate);
                        if (tenantMap != null && recordTenantId != null) {
                            TargetDateData targetDateData = tenantMap.computeIfAbsent(recordTenantId, t -> new TargetDateData(IngestionStatus.MISSED_DATE.getValue(), null, t));
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
                summaryRepository.completeSchedulerRun(jobId, startTime, 0, 0, 0, DashboardExtractorConstants.STATUS_COMPLETED, null);

                // When totalExtracted is 0, MISSED_DATE detail entries are persisted into the ug_ingestion_detail table.
                persistDateWiseDetails(jobId, targetDateDataMap, moduleName, emptyResponse, false);

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

            // Step 2: Finalize dataset file (either .psv or .xlsx)
            generatedFile = session.finishWorkbook();

            String delimiter = (dashboardProperties.isDelimitedFileEnabled() || dashboardProperties.isPipeFileEnabled())
                    ? dashboardProperties.getPipeFileDelimiter()
                    : null;

            // Step 3: Send generated dataset file to appropriate endpoint via unified ingestion client
            String legacyMode = dashboardProperties.getEffectiveLegacyUploadMode();
            IngestionResult ingestionResult = (delimiter != null)
                    ? ingestionClient.ingest(generatedFile, moduleName, jobTenantId, legacyMode, delimiter)
                    : ingestionClient.ingest(generatedFile, moduleName, jobTenantId, legacyMode);

            boolean isSuccess = DashboardExtractorConstants.STATUS_SUCCESS.equalsIgnoreCase(ingestionResult.getIngestionStatus());
            String responseJson = ingestionResult.getResponseData() != null
                    ? ingestionResult.getResponseData()
                    : "{\"failureReason\": \"" + (ingestionResult.getFailureReason() != null ? ingestionResult.getFailureReason().replace("\"", "'") : "Unknown Error") + "\"}";

            // Persist the status and fileStoreId into ug_legacy_data_ingestion_detail
            persistenceService.updateLegacyJobStatus(jobId, ingestionResult.getIngestionStatus(), null, responseJson);
            summaryRepository.completeSchedulerRun(jobId, startTime, (int) totalExtracted, isSuccess ? (int) totalExtracted : 0, isSuccess ? 0 : (int) totalExtracted, isSuccess ? DashboardExtractorConstants.STATUS_COMPLETED : DashboardExtractorConstants.STATUS_FAILED, isSuccess ? null : ingestionResult.getFailureReason());

            // Persist per-date detail entries into ug_ingestion_detail
            persistDateWiseDetails(jobId, targetDateDataMap, moduleName, responseJson, isSuccess);

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
            summaryRepository.completeSchedulerRun(jobId, startTime, 0, 0, 1, DashboardExtractorConstants.STATUS_FAILED, exception.getMessage());

            // Persist per-date failure detail entries into ug_ingestion_detail
            persistDateWiseDetails(jobId, targetDateDataMap, moduleName, errResponse, false);

            return LegacyIngestionResponse.builder()
                    .totalDatesRequested((int) start.until(end.plusDays(1)).getDays())
                    .datesFailed(1)
                    .processedResults(List.of(IngestionResult.builder()
                            .ingestionStatus(DashboardExtractorConstants.STATUS_FAILURE)
                            .failureReason(exception.getMessage())
                            .build()))
                    .build();
        } finally {
            if (generatedFile != null && generatedFile.exists()) {
                boolean isDelimited = (dashboardProperties.isDelimitedFileEnabled() || dashboardProperties.isPipeFileEnabled());
                boolean keep = isDelimited ? dashboardProperties.isDelimitedKeepFile() : dashboardProperties.isLegacyKeepExcelFile();
                if (keep) {
                    log.info("PRESERVED legacy dataset file at: {}", generatedFile.getAbsolutePath());
                } else {
                    boolean deleted = generatedFile.delete();
                    log.info("Temporary dataset file deletion status for job {}: {}", jobId, deleted);
                }
            }
            lock.get().unlock();
            log.info("Released ShedLock for module {}", moduleName);
        }
    }

    /**
     * Persists per-date and per-tenant entries into the ug_ingestion_detail table for every
     * calendar date and tenant requested in the legacy date range.
     *
     * @param schedulerId the unique identifier of the legacy batch job
     * @param targetDateDataMap map of date -> tenantId -> sample payload JSON and status
     * @param moduleName module short code (e.g. "PT")
     * @param responseData overall response string or error JSON
     * @param overallSuccess whether the batch push succeeded
     */
    private void persistDateWiseDetails(String schedulerId, Map<LocalDate, Map<String, TargetDateData>> targetDateDataMap, String moduleName, String responseData, boolean overallSuccess) {
        if (targetDateDataMap == null || targetDateDataMap.isEmpty()) {
            return;
        }
        List<DailyIngestionData> detailRecords = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);

        for (Map.Entry<LocalDate, Map<String, TargetDateData>> entry : targetDateDataMap.entrySet()) {
            LocalDate date = entry.getKey();
            Map<String, TargetDateData> tenantDataMap = entry.getValue();
            if (tenantDataMap == null) {
                continue;
            }

            for (TargetDateData targetDateData : tenantDataMap.values()) {
                String finalStatus;
                if (!overallSuccess && !IngestionStatus.MISSED_DATE.getValue().equals(targetDateData.status)) {
                    finalStatus = IngestionStatus.FAILURE.getValue();
                } else {
                    finalStatus = targetDateData.status;
                }

                String moduleDetailId = (tenantSyncService != null)
                        ? tenantSyncService.getModuleDetailId(targetDateData.tenantId, moduleName)
                        : null;
                long now = CommonUtils.getCurrentEpochMillis();

                DailyIngestionData detail = DailyIngestionData.builder()
                        .moduleIngestionId(CommonUtils.generateUUID())
                        .moduleDetailId(moduleDetailId)
                        .schedulerId(schedulerId)
                        .tenantId(targetDateData.tenantId)
                        .moduleName(moduleName)
                        .pushDate(date.format(formatter))
                        .requestData(targetDateData.samplePayloadJson)
                        .responseData(responseData)
                        .ingestionStatus(finalStatus)
                        .createdBy(DashboardExtractorConstants.SYSTEM_USER)
                        .createdTime(now)
                        .lastModifiedBy(DashboardExtractorConstants.SYSTEM_USER)
                        .lastModifiedTime(now)
                        .build();

                detailRecords.add(detail);
            }
        }

        log.info("Saving {} per-date/per-tenant ug_ingestion_detail records for legacy batch {}", detailRecords.size(), moduleName);
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
