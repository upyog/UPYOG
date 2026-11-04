package org.upyog.dashboard.service;

import org.apache.commons.lang3.StringUtils;
import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.util.CommonUtils;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.upyog.dashboard.api.DashboardClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.entity.DailyIngestionData;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.IngestionSchedulerDetail;
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;
import org.upyog.dashboard.config.DashboardExtractorProperties;
import org.upyog.dashboard.enums.IngestionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class that manages daily metrics extraction and ingestion for all
 * state-enabled modules.
 *
 * <p>
 * This service iterates over enabled modules, determines the appropriate start
 * date for catch‑up ingestion, and delegates data extraction to the registered
 * {@link ModuleExtractor}s. It persists ingestion progress using
 * {@link IngestionSummaryRepository} and builds {@link IngestionResult} objects
 * that are returned to callers.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyIngestionService {

    private static final String STATUS_SUCCESS = IngestionStatus.SUCCESS.getValue();
    private static final String STATUS_FAILURE = IngestionStatus.FAILURE.getValue();
    private static final String STATUS_SUCCESS_ZERO_METRICS = IngestionStatus.SUCCESS_ZERO_METRICS.getValue();
    private static final String STATUS_SUCCESS_DUPLICATE = IngestionStatus.SUCCESS_DUPLICATE.getValue();

    private static final Map<Class<?>, Optional<Method>> ULB_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Optional<Method>> TENANT_ID_METHOD_CACHE = new ConcurrentHashMap<>();

    private final DashboardClient dashboardClient;
    private final ExtractorRegistry extractorRegistry;
    private final SchemaMappingConfig schemaMappingConfig;
    private final IngestionSummaryRepository summaryRepository;
    private final DashboardExtractorProperties dashboardProperties;
    private final ObjectMapper objectMapper;
    private final TenantSyncService tenantSyncService;

    private int batchSize;
    private int tenantBatchSize;

    private String tenantId;
    private String defaultStartDateStr;

    /**
     * Initialises service-level configuration values from
     * {@link DashboardExtractorProperties}. This method is called after bean
     * construction.
     */
    @PostConstruct
    public void init() {
        this.tenantId = dashboardProperties.getTenantId();
        this.defaultStartDateStr = dashboardProperties.getDefaultStartDateStr();
        this.batchSize = dashboardProperties.getIngestionBatchSize();
        this.tenantBatchSize = dashboardProperties.getTenantBatchSize();
    }

    /**
     * Executes the daily scheduled ingestion pipeline wrapped with full execution lifecycle tracking.
     * Records the initial RUNNING status in ug_ingestion_scheduler_detail, processes all enabled modules,
     * and updates the run with COMPLETED or FAILED status and processed counts using constant values.
     *
     * @param cronExpression the active cron expression driving this scheduler run
     */
    public void executeScheduledIngestion(String cronExpression) {
        String schedulerId = CommonUtils.generateUUID();
        long startTime = CommonUtils.getCurrentEpochMillis();
        log.info("Daily Ingestion Scheduler triggered with schedulerId: {}", schedulerId);

        IngestionSchedulerDetail schedulerDetail = IngestionSchedulerDetail.builder()
                .schedulerId(schedulerId)
                .schedulerName(DashboardExtractorConstants.SCHEDULER_NAME_DAILY_INGESTION)
                .cronExpression(cronExpression)
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

        int totalCount = 0;
        int successCount = 0;
        int failureCount = 0;
        String finalStatus = DashboardExtractorConstants.STATUS_COMPLETED;
        String errorMessage = null;

        try {
            List<IngestionResult> results = ingestDailyData(schedulerId);
            if (results != null) {
                totalCount = results.size();
                for (IngestionResult result : results) {
                    if (result != null && IngestionStatus.fromValue(result.getIngestionStatus()).isSuccess()) {
                        successCount++;
                    } else {
                        failureCount++;
                    }
                }
            }
            log.info("Daily Ingestion Scheduler finished for schedulerId: {}. Processed: {}, Success: {}, Failure: {}",
                    schedulerId, totalCount, successCount, failureCount);
        } catch (Exception exception) {
            log.error("Daily Ingestion Scheduler encountered an error for schedulerId: {}", schedulerId, exception);
            finalStatus = DashboardExtractorConstants.STATUS_FAILED;
            errorMessage = exception.getMessage();
        } finally {
            summaryRepository.completeSchedulerRun(schedulerId, startTime, totalCount, successCount, failureCount, finalStatus, errorMessage);
        }
    }

    /**
     * Executes daily ingestion for all enabled modules across all active ULB
     * tenants using the default date range (yesterday). The method batches
     * active tenants and determines the appropriate start date for each module
     * and tenant based on previous successful runs.
     *
     * @return a list of {@link IngestionResult} objects representing the
     * outcome of each module's ingestion attempt.
     */
    public List<IngestionResult> ingestDailyData() {
        return ingestDailyData((String) null);
    }

    /**
     * Executes daily ingestion for all enabled modules across all active ULB
     * tenants using the default date range (yesterday) with an optional scheduler ID.
     *
     * @param schedulerId optional unique identifier for the triggering scheduler run
     * @return a list of {@link IngestionResult} objects representing the outcome
     */
    public List<IngestionResult> ingestDailyData(String schedulerId) {
        List<IngestionResult> allResults = new ArrayList<>();
        List<Module> enabledModules = schemaMappingConfig.getEnabledModules();

        if (enabledModules.isEmpty()) {
            log.warn("No modules enabled under extractor.enabled-modules in schema-mapping.yml");
            return allResults;
        }

        if (!summaryRepository.hasAnyModuleDetails()) {
            String errorMsg = "No tenant configuration found in ug_ingestion_module_detail table. Please run the MDMS tenant sync API (POST /api/v1/tenant/_sync) or configure tenants manually.";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate defaultStartDate = parseDefaultStartDate();

        for (Module module : enabledModules) {
            ModuleExtractor<?> extractor = extractorRegistry.get(module);
            if (extractor == null) {
                log.error("Enabled module {} has no registered ModuleExtractor bean", module);
                continue;
            }

            List<String> activeTenants = tenantSyncService.getActiveTenants(module);
            if (activeTenants.isEmpty()) {
                log.warn("No active tenants configured for module {} in ug_ingestion_module_detail table. Skipping module.", module);
                continue;
            }

            // Bulk fetch last successful dates for all tenants in 1 single query
            Map<String, LocalDate> lastSuccessMap = summaryRepository.findAllLastSuccessfulDatesByModule(module.name());

            List<String> pendingTenants = new ArrayList<>();
            for (String currentTenant : activeTenants) {
                LocalDate lastSuccess = lastSuccessMap.get(currentTenant);
                LocalDate startDate = (lastSuccess != null) ? lastSuccess.plusDays(1) : defaultStartDate;

                if (startDate.isAfter(yesterday)) {
                    log.info("Tenant {} module {} is already up-to-date up to yesterday ({}). Skipping.", currentTenant, module, yesterday);
                    allResults.add(buildResult("SKIPPED", module, yesterday,
                            "Tenant " + currentTenant + " module " + module.name() + " is already up-to-date up to yesterday (" + yesterday + ")", null));
                    continue;
                }
                pendingTenants.add(currentTenant);
            }

            if (pendingTenants.isEmpty()) {
                log.info("All {} active tenants for module {} are already up-to-date up to yesterday ({}). Skipping DB queries.",
                        activeTenants.size(), module, yesterday);
                continue;
            }

            log.info("Processing daily catch-up ingestion for module {} across {} pending tenants (total active: {}, batch size: {})",
                    module, pendingTenants.size(), activeTenants.size(), this.tenantBatchSize);

            for (int offset = 0; offset < pendingTenants.size(); offset += this.tenantBatchSize) {
                List<String> tenantBatch = pendingTenants.subList(offset, Math.min(offset + this.tenantBatchSize, pendingTenants.size()));
                processCatchUpForTenantBatch(tenantBatch, module, extractor, defaultStartDate, yesterday, lastSuccessMap, allResults, schedulerId);
            }
        }

        return allResults;
    }

    /**
     * Processes catch-up ingestion for a specific batch of tenants across their
     * respective date ranges.
     *
     * @param tenantBatch sublist of tenants to process
     * @param module business module
     * @param extractor extractor implementation
     * @param defaultStartDate fallback start date
     * @param yesterday catch-up target end date
     * @param lastSuccessMap cached map of tenant last successful dates
     * @param allResults accumulator list for results
     * @param schedulerId optional scheduler run identifier
     */
    private void processCatchUpForTenantBatch(List<String> tenantBatch, Module module, ModuleExtractor<?> extractor,
            LocalDate defaultStartDate, LocalDate yesterday, Map<String, LocalDate> lastSuccessMap,
            List<IngestionResult> allResults, String schedulerId) {
        Map<String, LocalDate> tenantStartDates = new HashMap<>();
        LocalDate minStartDate = yesterday;

        for (String currentTenant : tenantBatch) {
            LocalDate lastSuccess = (lastSuccessMap != null) ? lastSuccessMap.get(currentTenant) : null;
            LocalDate startDate = (lastSuccess != null) ? lastSuccess.plusDays(1) : defaultStartDate;

            if (startDate.isAfter(yesterday)) {
                log.info("Tenant {} module {} is already up-to-date up to yesterday ({}). Skipping.", currentTenant, module, yesterday);
                allResults.add(buildResult("SKIPPED", module, yesterday,
                        "Tenant " + currentTenant + " module " + module.name() + " is already up-to-date up to yesterday (" + yesterday + ")", null));
                continue;
            }

            long daysToIngest = ChronoUnit.DAYS.between(startDate, yesterday) + 1;
            int catchUpLimit = dashboardProperties.getDailyCatchUpLimitDays();
            if (daysToIngest > catchUpLimit) {
                log.error("Catch-up gap of {} days exceeds max limit of {} days for tenant {} module {}. Please use legacy migration.",
                        daysToIngest, catchUpLimit, currentTenant, module);
                allResults.add(buildResult(STATUS_FAILURE, module, yesterday,
                        "Catch-up gap of " + daysToIngest + " days exceeds max limit of " + catchUpLimit + " days for tenant " + currentTenant, null));
                continue;
            }

            tenantStartDates.put(currentTenant, startDate);
            if (startDate.isBefore(minStartDate)) {
                minStartDate = startDate;
            }
        }

        if (tenantStartDates.isEmpty()) {
            return;
        }

        LocalDate currentDate = minStartDate;
        while (!currentDate.isAfter(yesterday) && !tenantStartDates.isEmpty()) {
            List<String> tenantsNeedingDate = new ArrayList<>();
            for (Map.Entry<String, LocalDate> entry : tenantStartDates.entrySet()) {
                if (!entry.getValue().isAfter(currentDate)) {
                    tenantsNeedingDate.add(entry.getKey());
                }
            }

            if (tenantsNeedingDate.isEmpty()) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            summaryRepository.saveOrUpdateLastAttemptedDatesBatch(tenantsNeedingDate, module.name(), currentDate);

            List<IngestionResult> dateResults = ingestModuleBatchForDate(tenantsNeedingDate, module, extractor, currentDate, schedulerId);
            allResults.addAll(dateResults);

            // Check for failures and halt subsequent catch-up dates only for failed tenants
            for (IngestionResult result : dateResults) {
                if (result != null && !IngestionStatus.fromValue(result.getIngestionStatus()).isSuccess()) {
                    String failedTenant = extractTenantIdFromResult(result, tenantsNeedingDate);
                    log.warn("Ingestion failed for tenant {} module {} on date {}. Halting subsequent catch-up dates for this tenant.",
                            failedTenant, module, currentDate);
                    tenantStartDates.remove(failedTenant);
                }
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Executes ingestion for all enabled modules for a specific target date
     * across all active ULB tenants in batched multi-ULB queries, skipping
     * tenants that have already completed ingestion for that date.
    /**
     * Executes ingestion for all enabled modules for a specific target date
     * across all active ULB tenants in batched multi-ULB queries, skipping
     * tenants that have already completed ingestion for that date.
     *
     * @param targetDate the date for which data should be ingested
     * @return a list of {@link IngestionResult} objects representing the
     * outcome of each module's ingestion attempt.
     */
    public List<IngestionResult> ingestDailyData(LocalDate targetDate) {
        return ingestDailyData(targetDate, null);
    }

    /**
     * Executes ingestion for all enabled modules for a specific target date
     * with an optional scheduler ID.
     *
     * @param targetDate the date for which data should be ingested
     * @param schedulerId optional scheduler run identifier
     * @return list of {@link IngestionResult} objects
     */
    public List<IngestionResult> ingestDailyData(LocalDate targetDate, String schedulerId) {
        List<IngestionResult> results = new ArrayList<>();
        List<Module> enabledModules = schemaMappingConfig.getEnabledModules();

        if (enabledModules.isEmpty()) {
            log.warn("No modules enabled under extractor.enabled-modules in schema-mapping.yml");
            return results;
        }

        if (!summaryRepository.hasAnyModuleDetails()) {
            String errorMsg = "No tenant configuration found in ug_ingestion_module_detail table. Please run the MDMS tenant sync API (POST /api/v1/tenant/_sync) or configure tenants manually.";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        for (Module module : enabledModules) {
            ModuleExtractor<?> extractor = extractorRegistry.get(module);
            if (extractor == null) {
                log.error("Enabled module {} has no registered ModuleExtractor bean", module);
                continue;
            }

            List<String> activeTenants = tenantSyncService.getActiveTenants(module);
            if (activeTenants.isEmpty()) {
                log.warn("No active tenants configured for module {} in ug_ingestion_module_detail table. Skipping module.", module);
                continue;
            }

            // Bulk check which tenants have already completed targetDate
            Set<String> alreadyCompletedTenants = summaryRepository.findTenantsSuccessfullyIngestedForDate(module.name(), targetDate);

            List<String> pendingTenants = activeTenants.stream()
                    .filter(tenant -> !alreadyCompletedTenants.contains(tenant))
                    .collect(Collectors.toList());

            if (pendingTenants.isEmpty()) {
                log.info("All {} active tenants for module {} have already successfully ingested for date {}. Skipping duplicate DB extraction.",
                        activeTenants.size(), module, targetDate);
                for (String currentTenant : activeTenants) {
                    results.add(buildResult("SKIPPED", module, targetDate,
                            "Tenant " + currentTenant + " module " + module.name() + " already successfully ingested for date " + targetDate, null));
                }
                continue;
            }

            if (!alreadyCompletedTenants.isEmpty()) {
                log.info("Skipping {} already completed tenants for module {} on date {}. Processing remaining {} pending tenants.",
                        alreadyCompletedTenants.size(), module, targetDate, pendingTenants.size());
                for (String completedTenant : alreadyCompletedTenants) {
                    if (activeTenants.contains(completedTenant)) {
                        results.add(buildResult("SKIPPED", module, targetDate,
                                "Tenant " + completedTenant + " module " + module.name() + " already successfully ingested for date " + targetDate, null));
                    }
                }
            }

            log.info("Processing target date ({}) ingestion for module {} across {} pending tenants (total active: {}, batch size: {})",
                    targetDate, module, pendingTenants.size(), activeTenants.size(), this.tenantBatchSize);

            for (int offset = 0; offset < pendingTenants.size(); offset += this.tenantBatchSize) {
                List<String> tenantBatch = pendingTenants.subList(offset, Math.min(offset + this.tenantBatchSize, pendingTenants.size()));
                summaryRepository.saveOrUpdateLastAttemptedDatesBatch(tenantBatch, module.name(), targetDate);

                List<IngestionResult> batchResults = ingestModuleBatchForDate(tenantBatch, module, extractor, targetDate, schedulerId);
                results.addAll(batchResults);
            }
        }

        return results;
    }

    /**
     * Performs ingestion for a single tenant and module on a specific date.
     *
     * @param currentTenant the tenant to ingest
     * @param module the module to ingest
     * @param extractor the extractor implementation for the module
     * @param date the date for which data should be ingested
     * @return an {@link IngestionResult} describing success or failure
     */
    public IngestionResult ingestModuleForDate(String currentTenant, Module module, ModuleExtractor<?> extractor, LocalDate date) {
        List<IngestionResult> results = ingestModuleBatchForDate(List.of(currentTenant), module, extractor, date, null);
        return (!results.isEmpty()) ? results.get(0) : buildResult(STATUS_FAILURE, module, date, "No result produced", null);
    }

    /**
     * Performs batch extraction and ingestion for multiple tenants and a module
     * on a specific date in a single query.
     *
     * @param tenantBatch sublist of tenant IDs
     * @param module the module to ingest
     * @param extractor the extractor implementation
     * @param date the target date
     * @param schedulerId optional scheduler run identifier
     * @return list of {@link IngestionResult}s corresponding to the tenants
     */
    private List<IngestionResult> ingestModuleBatchForDate(List<String> tenantBatch, Module module, ModuleExtractor<?> extractor,
            LocalDate date, String schedulerId) {
        try {
            log.info("Starting batch extraction for module {} on date {} across {} tenants", module, date, tenantBatch.size());
            Object rawData = extractor.extractData(tenantBatch, date);
            List<?> dataList;
            if (rawData instanceof List<?>) {
                dataList = (List<?>) rawData;
            } else if (rawData != null) {
                dataList = List.of(rawData);
            } else {
                dataList = List.of();
            }

            return processBatchDataList(tenantBatch, module, extractor, dataList, date, schedulerId);
        } catch (Exception exception) {
            log.error("Batch extraction failed for module {} on date {} across tenants {}: {}", module, date, tenantBatch, exception.getMessage(), exception);
            List<IngestionResult> failureResults = new ArrayList<>();
            for (String tenant : tenantBatch) {
                failureResults.add(buildResult(STATUS_FAILURE, module, date, exception.getMessage(), null));
            }
            return failureResults;
        }
    }

    /**
     * Processes a batch list of extracted items for a module and persists audit
     * and summary checkpoints.
     *
     * @param tenantBatch original tenant batch list
     * @param module the module being processed
     * @param extractor the extractor instance
     * @param dataList the list of extracted data items
     * @param date the ingestion date
     * @param schedulerId optional scheduler run identifier
     * @return list of {@link IngestionResult}s
     */
    private List<IngestionResult> processBatchDataList(List<String> tenantBatch, Module module, ModuleExtractor<?> extractor,
            List<?> dataList, LocalDate date, String schedulerId) {
        List<IngestionResult> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);
        long now = CommonUtils.getCurrentEpochMillis();

        if (dataList.isEmpty()) {
            log.info("No data returned for module {} on date {} across tenants {}", module, date, tenantBatch);
            List<DailyIngestionData> emptyDetailRecords = new ArrayList<>();
            for (String tenant : tenantBatch) {
                summaryRepository.saveOrUpdateLastSuccessfulDate(tenant, module.name(), date);
                String responseDataJson = "{\"message\":\"No data returned for tenant. Marked as zero-metrics success.\"}";
                results.add(buildResult(STATUS_SUCCESS_ZERO_METRICS, module, date, null, responseDataJson));

                String moduleDetailId = tenantSyncService.getModuleDetailId(tenant, module.name());
                DailyIngestionData detailData = DailyIngestionData.builder()
                        .moduleIngestionId(CommonUtils.generateUUID())
                        .moduleDetailId(moduleDetailId)
                        .schedulerId(schedulerId)
                        .tenantId(tenant)
                        .moduleName(module.name())
                        .pushDate(date.format(formatter))
                        .requestData(null)
                        .responseData(responseDataJson)
                        .ingestionStatus(STATUS_SUCCESS_ZERO_METRICS)
                        .createdBy(DashboardExtractorConstants.SYSTEM_USER)
                        .createdTime(now)
                        .lastModifiedBy(DashboardExtractorConstants.SYSTEM_USER)
                        .lastModifiedTime(now)
                        .build();
                emptyDetailRecords.add(detailData);
            }
            if (!emptyDetailRecords.isEmpty()) {
                summaryRepository.saveIngestionDetailsBatch(emptyDetailRecords);
            }
            return results;
        }

        List<DailyIngestionData> batchDetailRecords = new ArrayList<>();
        Set<String> processedTenants = new HashSet<>();

        for (int batchOffset = 0; batchOffset < dataList.size(); batchOffset += this.batchSize) {
            List<?> batchSubList = dataList.subList(batchOffset, Math.min(batchOffset + this.batchSize, dataList.size()));

            for (Object item : batchSubList) {
                String itemTenantId = extractTenantId(item);
                if (StringUtils.isBlank(itemTenantId) || itemTenantId.equals(this.tenantId)) {
                    itemTenantId = (!tenantBatch.isEmpty()) ? tenantBatch.get(0) : this.tenantId;
                }
                processedTenants.add(itemTenantId);

                IngestionResult result;
                if (isAllZeroMetrics(extractor, item)) {
                    log.info("All metrics for tenant {} module {} on date {} are zero. Skipping downstream API push.", itemTenantId, module, date);
                    result = buildResult(STATUS_SUCCESS_ZERO_METRICS, module, date, null, "{\"message\":\"All metrics are zero. Downstream API push skipped.\"}");

                    String itemRequestJson = null;
                    try {
                        itemRequestJson = objectMapper != null ? objectMapper.writeValueAsString(item) : item.toString();
                    } catch (Exception serializationException) {
                        log.error("Failed to serialize item request payload for tenant {} module {} on date {}: {}",
                                itemTenantId, module, date, serializationException.getMessage());
                        itemRequestJson = item.toString();
                    }

                    String moduleDetailId = tenantSyncService.getModuleDetailId(itemTenantId, module.name());
                    DailyIngestionData detailData = DailyIngestionData.builder()
                            .moduleIngestionId(CommonUtils.generateUUID())
                            .moduleDetailId(moduleDetailId)
                            .schedulerId(schedulerId)
                            .tenantId(itemTenantId)
                            .moduleName(module.name())
                            .pushDate(date.format(formatter))
                            .requestData(itemRequestJson)
                            .responseData(result != null ? result.getResponseData() : null)
                            .ingestionStatus(STATUS_SUCCESS_ZERO_METRICS)
                            .createdBy(DashboardExtractorConstants.SYSTEM_USER)
                            .createdTime(now)
                            .lastModifiedBy(DashboardExtractorConstants.SYSTEM_USER)
                            .lastModifiedTime(now)
                            .build();
                    batchDetailRecords.add(detailData);
                } else {
                    String moduleDetailId = tenantSyncService.getModuleDetailId(itemTenantId, module.name());
                    result = executeIngestion(module, item, date, schedulerId, moduleDetailId);
                }

                if (result != null && IngestionStatus.fromValue(result.getIngestionStatus()).isSuccess()) {
                    summaryRepository.saveOrUpdateLastSuccessfulDate(itemTenantId, module.name(), date);
                }

                results.add(result);
            }

            if (!batchDetailRecords.isEmpty()) {
                summaryRepository.saveIngestionDetailsBatch(batchDetailRecords);
                batchDetailRecords.clear();
            }
        }

        // Checkpoint any tenants in tenantBatch that had no records returned in dataList (e.g. inactive tenants)
        for (String tenant : tenantBatch) {
            if (!isTenantProcessed(tenant, processedTenants)) {
                log.info("Tenant {} in batch had no data returned for module {} on date {}. Checkpointing as zero-metrics success.", tenant, module, date);
                summaryRepository.saveOrUpdateLastSuccessfulDate(tenant, module.name(), date);
                String responseDataJson = "{\"message\":\"No activity recorded for tenant. Checkpointed as zero-metrics success.\"}";
                results.add(buildResult(STATUS_SUCCESS_ZERO_METRICS, module, date, null, responseDataJson));

                String moduleDetailId = tenantSyncService.getModuleDetailId(tenant, module.name());
                DailyIngestionData detailData = DailyIngestionData.builder()
                        .moduleIngestionId(CommonUtils.generateUUID())
                        .moduleDetailId(moduleDetailId)
                        .schedulerId(schedulerId)
                        .tenantId(tenant)
                        .moduleName(module.name())
                        .pushDate(date.format(formatter))
                        .requestData(null)
                        .responseData(responseDataJson)
                        .ingestionStatus(STATUS_SUCCESS_ZERO_METRICS)
                        .createdBy(DashboardExtractorConstants.SYSTEM_USER)
                        .createdTime(now)
                        .lastModifiedBy(DashboardExtractorConstants.SYSTEM_USER)
                        .lastModifiedTime(now)
                        .build();
                batchDetailRecords.add(detailData);
            }
        }

        if (!batchDetailRecords.isEmpty()) {
            summaryRepository.saveIngestionDetailsBatch(batchDetailRecords);
            batchDetailRecords.clear();
        }

        return results;
    }

    /**
     * Checks whether a tenant from the requested batch is covered by the
     * processed items.
     *
     * @param tenant requested tenant ID (e.g. "pg" or "pg.citya")
     * @param processedTenants set of tenant IDs extracted from items
     * @return true if exact match or hierarchical parent/child match exists,
     * false otherwise
     */
    private boolean isTenantProcessed(String tenant, Set<String> processedTenants) {
        if (processedTenants.contains(tenant)) {
            return true;
        }
        for (String processed : processedTenants) {
            if (processed.startsWith(tenant + ".") || tenant.startsWith(processed + ".")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to extract or correlate tenant ID from an IngestionResult.
     *
     * @param result IngestionResult object
     * @param fallbackTenants list of candidate tenants for fallback correlation
     * @return tenant ID string
     */
    private String extractTenantIdFromResult(IngestionResult result, List<String> fallbackTenants) {
        if (result == null || fallbackTenants == null || fallbackTenants.isEmpty()) {
            return this.tenantId;
        }
        for (String tenant : fallbackTenants) {
            if (result.getFailureReason() != null && result.getFailureReason().contains(tenant)) {
                return tenant;
            }
        }
        return fallbackTenants.get(0);
    }

    /**
     * Executes the dashboard client for a single extracted item. Utilises
     * Java 16 pattern matching to wrap {@link DashboardData} items in a
     * singleton list.
     *
     * @param module the module associated with the item
     * @param item the extracted payload (either a {@link DashboardData} or any
     * other object)
     * @param date the ingestion date
     * @return the {@link IngestionResult} returned by the client
     */
    private IngestionResult executeIngestion(Module module, Object item, LocalDate date) {
        return executeIngestion(module, item, date, null, null);
    }

    private IngestionResult executeIngestion(Module module, Object item, LocalDate date, String schedulerId, String moduleDetailId) {
        Object payloadItem = item instanceof DashboardData dashboardData ? List.of(dashboardData) : item;
        DashboardRequest request = DashboardRequest.builder()
                .module(module)
                .rawData(payloadItem)
                .schedulerId(schedulerId)
                .moduleDetailId(moduleDetailId)
                .build();
        log.info("Executing dashboardClient for item: {}", item);
        IngestionResult result = dashboardClient.execute(request);
        if (result != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);
            if (result.getDate() == null) {
                result.setDate(date != null ? date.format(formatter) : null);
            }
            String errorDetails = (result.getFailureReason() != null ? result.getFailureReason() : "")
                    + (result.getResponseData() != null ? result.getResponseData() : "");
            // Handle duplicate date errors gracefully: if the upstream service already received this date's data,
            // treat this as an idempotent success (SUCCESS_DUPLICATE) rather than a hard failure.
            if (STATUS_FAILURE.equalsIgnoreCase(result.getIngestionStatus()) && isDuplicateDateError(errorDetails)) {
                log.info("Duplicate date error detected for module {} on date {}. Marking status as SUCCESS_DUPLICATE.", module, date);
                result.setIngestionStatus(STATUS_SUCCESS_DUPLICATE);
            }
        }
        return result;
    }

    /**
     * Processes a single data item (non‑list) for ingestion.
     *
     * @param module the module being processed
     * @param rawData the raw payload extracted by the module extractor
     * @param date the ingestion date
     * @return the {@link IngestionResult} from the dashboard client
     */
    private IngestionResult processSingleData(Module module, Object rawData, LocalDate date) {
        DashboardRequest request = DashboardRequest.builder().module(module).rawData(rawData).build();
        IngestionResult result = dashboardClient.execute(request);
        if (result != null && result.getDate() == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);
            result.setDate(date != null ? date.format(formatter) : null);
        }
        log.info("Ingestion status for module {} on date {}: {}", module, date, result != null ? result.getIngestionStatus() : null);
        return result;
    }

    /**
     * Helper to construct a standardized {@link IngestionResult} instance.
     *
     * @param status ingestion status (e.g., SUCCESS or FAILURE)
     * @param module the module for which the result is built
     * @param date the ingestion date
     * @param failureReason optional failure reason message; may be {@code null}
     * @param responseData optional response payload from the client; may be
     * {@code null}
     * @return a fully populated {@link IngestionResult}
     */
    private IngestionResult buildResult(String status, Module module, LocalDate date, String failureReason, String responseData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);
        return IngestionResult.builder()
                .ingestionStatus(status)
                .date(date != null ? date.format(formatter) : null)
                .moduleName(module.name())
                .failureReason(failureReason)
                .responseData(responseData)
                .ingestedAt(CommonUtils.getCurrentEpochMillis())
                .build();
    }

    /**
     * Parses the default start date configuration value. If the value is
     * missing, blank, or unparsable, the method falls back to yesterday's date.
     *
     * @return the configured start date or yesterday as a fallback
     */
    private LocalDate parseDefaultStartDate() {
        try {
            if (StringUtils.isNotBlank(defaultStartDateStr)) {
                return LocalDate.parse(defaultStartDateStr.trim());
            }
        } catch (Exception exception) {
            log.warn("Failed to parse defaultStartDateStr '{}'. Falling back to yesterday.", defaultStartDateStr, exception);
        }
        return LocalDate.now().minusDays(1);
    }

    /**
     * Helper to extract tenant ID (ULB) dynamically from an extracted item
     * using cached reflection.
     *
     * @param item extracted metric DTO or DashboardData object
     * @return tenant ID string (e.g. "pg.citya") or configured tenant fallback
     */
    private String extractTenantId(Object item) {
        if (item == null) {
            return this.tenantId;
        }
        if (item instanceof DashboardData dashboardData && StringUtils.isNotBlank(dashboardData.getUlb())) {
            return dashboardData.getUlb();
        }
        Class<?> clazz = item.getClass();
        java.lang.reflect.Method getUlbMethod = getCachedMethod(ULB_METHOD_CACHE, clazz, "getUlb");
        if (getUlbMethod != null) {
            try {
                Object ulbValue = getUlbMethod.invoke(item);
                if (ulbValue != null && StringUtils.isNotBlank(ulbValue.toString())) {
                    return ulbValue.toString();
                }
            } catch (Exception ignored) {
            }
        }
        java.lang.reflect.Method getTenantIdMethod = getCachedMethod(TENANT_ID_METHOD_CACHE, clazz, "getTenantId");
        if (getTenantIdMethod != null) {
            try {
                Object tenantIdValue = getTenantIdMethod.invoke(item);
                if (tenantIdValue != null && StringUtils.isNotBlank(tenantIdValue.toString())) {
                    return tenantIdValue.toString();
                }
            } catch (Exception ignored) {
            }
        }
        return this.tenantId;
    }

    /**
     * Resolves and caches reflective getter method lookup for a given class.
     *
     * @param cache map holding cached method reflection lookups
     * @param clazz class being inspected
     * @param methodName name of the getter method
     * @return the resolved {@link java.lang.reflect.Method} or {@code null} if
     * not present
     */
    private Method getCachedMethod(Map<Class<?>, Optional<Method>> cache, Class<?> clazz, String methodName) {
        return cache.computeIfAbsent(clazz, c -> {
            try {
                return Optional.of(c.getMethod(methodName));
            } catch (NoSuchMethodException e) {
                return Optional.empty();
            }
        }).orElse(null);
    }

    /**
     * Evaluates whether all metrics in an extracted data payload are zero.
     * Delegates to {@link ModuleExtractor#isZeroMetrics(Object)} when
     * available, adhering to the Open/Closed Principle (OCP).
     *
     * @param extractor the extractor responsible for the module
     * @param item extracted metric DTO or DashboardData object
     * @return true if all metrics are zero, false if any metric is greater than
     * zero
     */
    private boolean isAllZeroMetrics(ModuleExtractor<?> extractor, Object item) {
        if (item == null) {
            return true;
        }
        if (item instanceof DashboardData dashboardData) {
            if (dashboardData.getMetrics() == null || dashboardData.getMetrics().isEmpty()) {
                return true;
            }
            for (Object metricValue : dashboardData.getMetrics().values()) {
                if (isNonZero(metricValue)) {
                    return false;
                }
            }
            return true;
        }
        if (extractor != null) {
            return extractor.isZeroMetrics(item);
        }
        return false;
    }

    /**
     * Recursively checks whether a generic object, collection, map, number, or
     * string value contains a positive non-zero numeric quantity.
     *
     * @param value value to inspect
     * @return true if positive non-zero, false otherwise
     */
    private boolean isNonZero(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Number number) {
            return number.doubleValue() > 0;
        }
        if (value instanceof String stringValue) {
            try {
                return Double.parseDouble(stringValue) > 0;
            } catch (Exception ignored) {
            }
        }
        if (value instanceof Collection<?> collection) {
            for (Object element : collection) {
                if (isNonZero(element)) {
                    return true;
                }
            }
        }
        if (value instanceof Map<?, ?> map) {
            for (Object mapEntryValue : map.values()) {
                if (isNonZero(mapEntryValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether an upstream failure message indicates that data for the
     * target date was already received, allowing idempotent handling.
     *
     * @param message failure error message string
     * @return true if error represents duplicate date, false otherwise
     */
    private boolean isDuplicateDateError(String message) {
        if (StringUtils.isBlank(message)) {
            return false;
        }
        String lower = message.toLowerCase();
        return lower.contains("duplicate")
                || lower.contains("already exists")
                || lower.contains("already_exist")
                || lower.contains("already present")
                || lower.contains("already ingested")
                || lower.contains("already_ingested")
                || lower.contains("record_already_ingested")
                || lower.contains("eg_ds_record_already_ingested_err")
                || lower.contains("409");
    }
}
