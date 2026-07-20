package org.upyog.adapter.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.adapter.api.AdapterClient;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.extractor.ModuleExtractor;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.model.LegacyIngestionResponse;
import org.upyog.adapter.registry.ExtractorRegistry;
import org.upyog.adapter.repository.IngestionSummaryRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service managing bulk historical (legacy) metrics ingestion for past date ranges.
 *
 * <p>Includes deduplication logic to ensure dates that have already been successfully
 * ingested for a tenant/module are skipped without re-pushing duplicate metrics.
 */
@Slf4j
@Service
public class LegacyIngestionService {

    @Autowired
    private AdapterClient adapterClient;

    @Autowired
    private ExtractorRegistry extractorRegistry;

    @Autowired
    private SchemaMappingConfig schemaMappingConfig;

    @Autowired
    private IngestionSummaryRepository summaryRepository;

    @Autowired
    private org.upyog.adapter.producer.AdapterProducer producer;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Value("${adapter.system.user.tenantId:pg}")
    private String tenantId;

    /**
     * Ingests historical data for the specified N months lookback up to yesterday.
     *
     * @param months number of months to look back (e.g. 5)
     * @param targetModule optional specific module to process; if null, processes all enabled modules
     * @return LegacyIngestionResponse summarizing skipped, succeeded, and failed dates
     */
    public LegacyIngestionResponse ingestHistoricalDataForLastMonths(int months, Module targetModule) {
        if (months <= 0) {
            months = 5;
        }
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate startDate = yesterday.minusMonths(months).withDayOfMonth(1);
        return ingestHistoricalDataForRange(startDate, yesterday, targetModule);
    }

    /**
     * Ingests historical data for a custom date range [startDate to endDate].
     *
     * @param startDate start date of the historical range (inclusive)
     * @param endDate end date of the historical range (inclusive)
     * @param targetModule optional specific module to process; if null, processes all enabled modules
     * @return LegacyIngestionResponse summarizing total, skipped, succeeded, and failed dates
     */
    public LegacyIngestionResponse ingestHistoricalDataForRange(LocalDate startDate, LocalDate endDate, Module targetModule) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid date range: startDate must be on or before endDate");
        }

        List<Module> modulesToProcess = getModulesToProcess(targetModule);
        List<String> skippedDatesList = new ArrayList<>();
        List<IngestionResult> processedResultsList = new ArrayList<>();

        int totalDatesRequested = 0;
        int datesSkipped = 0;
        int datesProcessedSuccessfully = 0;
        int datesFailed = 0;

        for (Module module : modulesToProcess) {
            ModuleExtractor extractor = extractorRegistry.get(module);
            if (extractor == null) {
                log.error("LegacyIngestionService | Module {} has no registered ModuleExtractor", module);
                continue;
            }

            Set<LocalDate> alreadyIngestedDates = summaryRepository.findSuccessfullyIngestedDates(
                    tenantId, module.name(), startDate, endDate);
            Optional<LocalDate> currentLastSuccessOpt = summaryRepository.findLastSuccessfulDate(tenantId, module.name());

            log.info("LegacyIngestionService | Starting historical ingestion for module {} from {} to {}. Already ingested count: {}",
                    module, startDate, endDate, alreadyIngestedDates.size());

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                totalDatesRequested++;

                if (alreadyIngestedDates.contains(currentDate)) {
                    skippedDatesList.add(module.name() + ":" + currentDate);
                    datesSkipped++;
                    log.debug("LegacyIngestionService | Skipping date {} for module {} (already successfully ingested)", currentDate, module);
                    currentDate = currentDate.plusDays(1);
                    continue;
                }

                IngestionResult result = ingestModuleForDate(module, extractor, currentDate);
                processedResultsList.add(result);

                if ("SUCCESS".equalsIgnoreCase(result.getIngestionStatus())) {
                    datesProcessedSuccessfully++;
                    // Update summary tracker if currentDate is newer than current recorded last_successful_date
                    if (currentLastSuccessOpt.isEmpty() || currentDate.isAfter(currentLastSuccessOpt.get())) {
                        summaryRepository.saveOrUpdateLastSuccessfulDate(tenantId, module.name(), currentDate);
                        currentLastSuccessOpt = Optional.of(currentDate);
                    }
                } else {
                    datesFailed++;
                    log.warn("LegacyIngestionService | Historical ingestion failed for module {} on date {}", module, currentDate);
                }

                currentDate = currentDate.plusDays(1);
            }
        }

        return LegacyIngestionResponse.builder()
                .totalDatesRequested(totalDatesRequested)
                .datesSkipped(datesSkipped)
                .datesProcessedSuccessfully(datesProcessedSuccessfully)
                .datesFailed(datesFailed)
                .skippedDates(skippedDatesList)
                .processedResults(processedResultsList)
                .build();
    }

    private List<Module> getModulesToProcess(Module targetModule) {
        if (targetModule != null) {
            return List.of(targetModule);
        }
        List<Module> enabled = schemaMappingConfig.getEnabledModules();
        return (enabled != null && !enabled.isEmpty()) ? enabled : List.of();
    }

    private IngestionResult ingestModuleForDate(Module module, ModuleExtractor extractor, LocalDate date) {
        long now = System.currentTimeMillis();
        String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        DashboardData rawData = null;
        IngestionResult result = null;
        String requestJson = "{}";
        String responseJson = "{}";

        try {
            rawData = extractor.extractData(date);
            AdapterRequest request = AdapterRequest.builder().module(module).rawData(List.of(rawData)).build();
            try {
                requestJson = objectMapper.writeValueAsString(request);
            } catch (Exception ignored) {}

            result = adapterClient.execute(request);
            log.info("LegacyIngestionService | Ingested module {} date {}: status {}", module, date, result.getIngestionStatus());

            responseJson = result.getResponseData() != null ? sanitizeJson(result.getResponseData()) : sanitizeJson(result.getFailureReason());

        } catch (Exception e) {
            log.error("LegacyIngestionService | Ingestion error for module {} date {}", module, date, e);
            responseJson = sanitizeJson(e.getMessage());
            result = IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason(e.getMessage())
                    .ingestedAt(now)
                    .build();
        }

        pushLegacyIngestionRecord(module, rawData, dateStr, requestJson, responseJson, result.getIngestionStatus(), now);

        return result;
    }

    private void pushLegacyIngestionRecord(Module module, DashboardData rawData, String pushMonthStr,
                                           String requestJson, String responseOrError, String status, long timestamp) {
        try {
            String recordTenant = (rawData != null && rawData.getUlb() != null) ? rawData.getUlb() : tenantId;

            org.upyog.adapter.entity.LegacyIngestionData legacyRecord = org.upyog.adapter.entity.LegacyIngestionData.builder()
                    .moduleIngestionId(java.util.UUID.randomUUID().toString())
                    .moduleDetailId(null)
                    .tenantId(recordTenant)
                    .ulbName(recordTenant)
                    .moduleName(module.name())
                    .pushMonth(pushMonthStr)
                    .userId("SYSTEM")
                    .requestData(requestJson)
                    .responseData(responseOrError)
                    .ingestionStatus(status)
                    .createdBy("SYSTEM")
                    .createdTime(timestamp)
                    .lastModifiedBy("SYSTEM")
                    .lastModifiedTime(timestamp)
                    .build();

            producer.push(org.upyog.adapter.common.constants.KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL,
                    java.util.Map.of("legacyIngestionData", List.of(legacyRecord)));
            log.info("LegacyIngestionService | Pushed legacy audit record to Kafka topic {} for module {} date {}",
                    org.upyog.adapter.common.constants.KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL, module, pushMonthStr);
        } catch (Exception ex) {
            log.error("LegacyIngestionService | Failed to push legacy audit record to Kafka", ex);
        }
    }

    private String sanitizeJson(String input) {
        if (input == null || input.isBlank()) {
            return "{}";
        }
        try {
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(input);
            if (node != null && (node.isObject() || node.isArray())) {
                return input;
            }
        } catch (Exception ignored) {
        }
        try {
            return objectMapper.writeValueAsString(java.util.Map.of("error", input));
        } catch (Exception ex) {
            return "{\"error\":\"" + input.replace("\"", "\\\"").replace("\n", " ") + "\"}";
        }
    }
}
