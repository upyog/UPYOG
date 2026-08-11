package org.upyog.dashboard.service.impl;


import org.upyog.dashboard.util.CommonUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.common.constants.KafkaTopics;
import org.upyog.dashboard.entity.IngestionModuleSummary;
import org.upyog.dashboard.entity.LegacyIngestionData;
import org.upyog.dashboard.producer.DashboardProducer;
import org.upyog.dashboard.service.IngestionPersistenceService;

import lombok.extern.slf4j.Slf4j;

/**
 * Kafka implementation of IngestionPersistenceService.
 * Used when dashboard-data.persister.enabled is true (default).
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "dashboard-data.persister.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaIngestionPersistenceServiceImpl implements IngestionPersistenceService {

    @Autowired
    private DashboardProducer producer;

    @Override
    public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String id = CommonUtils.generateUUID();
            
            IngestionModuleSummary summary = IngestionModuleSummary.builder()
                .id(id)
                .tenantId(tenantId)
                .moduleName(moduleName)
                .lastSuccessfulDate(successfulDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .lastAttemptedDate(successfulDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .createdBy("SYSTEM")
                .createdTime(now)
                .lastModifiedBy("SYSTEM")
                .lastModifiedTime(now)
                .build();

            Map<String, Object> message = new HashMap<>();
            message.put("ingestionModuleSummary", Collections.singletonList(summary));
            producer.push(KafkaTopics.UPDATE_ADAPTER_MODULE_SUMMARY, message);

            log.info("KafkaIngestionPersistenceServiceImpl | Pushed update for last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("KafkaIngestionPersistenceServiceImpl | Failed to update last successful date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName, exception);
        }
    }

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
                .lastSuccessfulDate(fallbackSuccessDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .lastAttemptedDate(attemptedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .createdBy("SYSTEM")
                .createdTime(now)
                .lastModifiedBy("SYSTEM")
                .lastModifiedTime(now)
                .build();

            Map<String, Object> message = new HashMap<>();
            message.put("ingestionModuleSummary", Collections.singletonList(summary));
            producer.push(KafkaTopics.UPDATE_ADAPTER_MODULE_SUMMARY, message);

            log.info("KafkaIngestionPersistenceServiceImpl | Pushed update for last_attempted_date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("KafkaIngestionPersistenceServiceImpl | Failed to update last attempted date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName, exception);
        }
    }

    @Override
    public void createLegacyJob(String tenantId, String moduleName, LocalDate date) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String jobId = CommonUtils.generateUUID();
            
            LegacyIngestionData legacyData = LegacyIngestionData.builder()
                .moduleIngestionId(jobId)
                .tenantId(tenantId)
                .moduleName(moduleName)
                .pushDate(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .ingestionStatus("NOT_STARTED")
                .createdBy("SYSTEM")
                .createdTime(now)
                .lastModifiedBy("SYSTEM")
                .lastModifiedTime(now)
                .build();
                
            Map<String, Object> message = new HashMap<>();
            message.put("legacyIngestionData", Collections.singletonList(legacyData));
            producer.push(KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL, message);

            log.debug("KafkaIngestionPersistenceServiceImpl | Pushed legacy job for tenant {} module {} date {}", tenantId, moduleName, date);
        } catch (Exception exception) {
            log.error("KafkaIngestionPersistenceServiceImpl | Failed to push legacy job for tenant {} module {} date {}", tenantId, moduleName, date, exception);
        }
    }

    @Override
    public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            LegacyIngestionData legacyData = LegacyIngestionData.builder()
                .moduleIngestionId(jobId)
                .responseData(responseData)
                .ingestionStatus(status)
                .lastModifiedBy("SYSTEM")
                .lastModifiedTime(now)
                .build();
                
            Map<String, Object> message = new HashMap<>();
            message.put("legacyIngestionData", Collections.singletonList(legacyData));
            producer.push(KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL, message);

            log.info("KafkaIngestionPersistenceServiceImpl | Pushed update legacy job {} to status {}", jobId, status);
        } catch (Exception exception) {
            log.error("KafkaIngestionPersistenceServiceImpl | Failed to push update legacy job {} to status {}", jobId, status, exception);
        }
    }
}
