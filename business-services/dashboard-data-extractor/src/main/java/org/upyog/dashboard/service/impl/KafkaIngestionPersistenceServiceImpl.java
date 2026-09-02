package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.util.CommonUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.entity.IngestionModuleSummary;
import org.upyog.dashboard.entity.LegacyIngestionData;
import org.upyog.dashboard.producer.DashboardProducer;
import org.upyog.dashboard.service.IngestionPersistenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka implementation of IngestionPersistenceService.
 * Used when dashboard-data.persister.enabled is true (default).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dashboard-data.persister.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaIngestionPersistenceServiceImpl implements IngestionPersistenceService {

    private static final String SYSTEM_USER = "SYSTEM";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);

    private final DashboardProducer producer;
    private final DashboardProperties dashboardProperties;

    /**
     * Builds an {@link org.upyog.dashboard.entity.IngestionModuleSummary} payload and publishes
     * it to the {@code UPDATE_ADAPTER_MODULE_SUMMARY} Kafka topic so the persister service can
     * update both {@code last_successful_date} and {@code last_attempted_date}.
     *
     * @param tenantId       the tenant identifier
     * @param moduleName     the module short code (e.g., {@code PT})
     * @param successfulDate the date of the successful ingestion run
     */
    @Override
    public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String id = CommonUtils.generateUUID();
            
            IngestionModuleSummary summary = IngestionModuleSummary.builder()
                .id(id)
                .tenantId(tenantId)
                .moduleName(moduleName)
                .lastSuccessfulDate(successfulDate.format(DATE_FORMATTER))
                .lastAttemptedDate(successfulDate.format(DATE_FORMATTER))
                .createdBy(SYSTEM_USER)
                .createdTime(now)
                .lastModifiedBy(SYSTEM_USER)
                .lastModifiedTime(now)
                .build();

            Map<String, Object> message = new HashMap<>();
            message.put("ingestionModuleSummary", Collections.singletonList(summary));
            producer.push(dashboardProperties.getUpdateAdapterModuleSummaryTopic(), message);

            log.info("Pushed update for last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("Failed to update last successful date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName, exception);
        }
    }

    /**
     * Builds an {@link org.upyog.dashboard.entity.IngestionModuleSummary} payload with the
     * attempted date and an epoch {@code last_successful_date} fallback, then publishes it to
     * the {@code UPDATE_ADAPTER_MODULE_SUMMARY} Kafka topic.
     *
     * @param tenantId      the tenant identifier
     * @param moduleName    the module short code
     * @param attemptedDate the date for which ingestion was attempted
     */
    @Override
    public void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String id = CommonUtils.generateUUID();
            LocalDate fallbackSuccessDate = LocalDate.of(1970, 1, 1);

            IngestionModuleSummary summary = IngestionModuleSummary.builder()
                .id(id)
                .tenantId(tenantId)
                .moduleName(moduleName)
                .lastSuccessfulDate(fallbackSuccessDate.format(DATE_FORMATTER))
                .lastAttemptedDate(attemptedDate.format(DATE_FORMATTER))
                .createdBy(SYSTEM_USER)
                .createdTime(now)
                .lastModifiedBy(SYSTEM_USER)
                .lastModifiedTime(now)
                .build();

            Map<String, Object> message = new HashMap<>();
            message.put("ingestionModuleSummary", Collections.singletonList(summary));
            producer.push(dashboardProperties.getUpdateAdapterModuleSummaryTopic(), message);

            log.info("Pushed update for last_attempted_date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("Failed to update last attempted date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName, exception);
        }
    }

    /**
     * Builds a {@link org.upyog.dashboard.entity.LegacyIngestionData} payload with status
     * {@code NOT_STARTED} and publishes it to the {@code SAVE_LEGACY_INGESTION_DETAIL} Kafka
     * topic so the persister service inserts the new job row.
     *
     * @param jobId      the unique identifier of the legacy job
     * @param tenantId   the tenant identifier
     * @param moduleName the module short code
     * @param pushDate   the push date for which the legacy job is created
     * @param startDate  the start date of the legacy range
     * @param endDate    the end date of the legacy range
     */
    @Override
    public void createLegacyJob(String jobId, String tenantId, String moduleName, LocalDate pushDate, LocalDate startDate, LocalDate endDate) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String id = (jobId != null) ? jobId : CommonUtils.generateUUID();
            LocalDate pDate = (pushDate != null) ? pushDate : (startDate != null ? startDate : LocalDate.now());
            LocalDate sDate = (startDate != null) ? startDate : pDate;
            LocalDate eDate = (endDate != null) ? endDate : pDate;
            
            LegacyIngestionData legacyData = LegacyIngestionData.builder()
                .moduleIngestionId(id)
                .tenantId(tenantId)
                .moduleName(moduleName)
                .pushDate(pDate.format(DATE_FORMATTER))
                .startDate(sDate.format(DATE_FORMATTER))
                .endDate(eDate.format(DATE_FORMATTER))
                .ingestionStatus(DashboardExtractorConstants.STATUS_NOT_STARTED)
                .createdBy(SYSTEM_USER)
                .createdTime(now)
                .lastModifiedBy(SYSTEM_USER)
                .lastModifiedTime(now)
                .build();
                
            Map<String, Object> message = new HashMap<>();
            message.put("legacyIngestionData", Collections.singletonList(legacyData));
            producer.push(dashboardProperties.getSaveLegacyIngestionDetailTopic(), message);

            log.debug("Pushed legacy job {} for tenant {} module {} range [{} to {}]", id, tenantId, moduleName, sDate, eDate);
        } catch (Exception exception) {
            log.error("Failed to push legacy job for tenant {} module {} range [{} to {}]", tenantId, moduleName, startDate, endDate, exception);
        }
    }

    /**
     * Builds a {@link org.upyog.dashboard.entity.LegacyIngestionData} payload carrying the
     * updated status, response data, and audit timestamps, then publishes it to the
     * {@code UPDATE_LEGACY_INGESTION_DETAIL} Kafka topic.
     *
     * @param jobId        the unique identifier of the legacy job
     * @param status       the new ingestion status (e.g., {@code SUCCESS} or {@code FAILURE})
     * @param requestData  the JSON request payload sent to the external system (unused in Kafka payload)
     * @param responseData the JSON response payload received from the external system
     */
    @Override
    public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            LegacyIngestionData legacyData = LegacyIngestionData.builder()
                .moduleIngestionId(jobId)
                .responseData(responseData)
                .ingestionStatus(status)
                .lastModifiedBy(SYSTEM_USER)
                .lastModifiedTime(now)
                .build();
                
            Map<String, Object> message = new HashMap<>();
            message.put("legacyIngestionData", Collections.singletonList(legacyData));
            producer.push(dashboardProperties.getUpdateLegacyIngestionDetailTopic(), message);

            log.info("Pushed update legacy job {} to status {}", jobId, status);
        } catch (Exception exception) {
            log.error("Failed to push update legacy job {} to status {}", jobId, status, exception);
        }
    }

    /**
     * Publishes a batch of daily ingestion detail audit records to the Kafka topic.
     *
     * @param details list of daily ingestion data objects
     */
    @Override
    public void saveIngestionDetailsBatch(java.util.List<?> details) {
        try {
            if (details == null || details.isEmpty()) {
                return;
            }

            Map<String, Object> message = new HashMap<>();
            message.put("dailyIngestionData", details);
            producer.push(dashboardProperties.getSaveIngestionDetailTopic(), message);

            log.info("Pushed batch of {} ingestion detail audit records to Kafka topic {}",
                    details.size(), dashboardProperties.getSaveIngestionDetailTopic());
        } catch (Exception exception) {
            log.error("Failed to push batch ingestion detail audit records to Kafka", exception);
        }
    }
}
