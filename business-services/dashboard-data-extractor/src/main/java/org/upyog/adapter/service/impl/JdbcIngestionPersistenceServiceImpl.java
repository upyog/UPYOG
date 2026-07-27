package org.upyog.adapter.service.impl;


import org.upyog.adapter.util.CommonUtils;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.adapter.repository.querybuilder.IngestionSummaryQueryBuilder;
import org.upyog.adapter.service.IngestionPersistenceService;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC implementation of IngestionPersistenceService.
 * Used when adapter.persister.enabled is false.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "adapter.persister.enabled", havingValue = "false")
public class JdbcIngestionPersistenceServiceImpl implements IngestionPersistenceService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IngestionSummaryQueryBuilder queryBuilder;

    @Override
    public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String id = CommonUtils.generateUUID();
            
            jdbcTemplate.update(queryBuilder.getUpsertLastSuccessfulDateQuery(),
                    id, tenantId, moduleName, 
                    Date.valueOf(successfulDate), Date.valueOf(successfulDate), 
                    "SYSTEM", now, "SYSTEM", now);

            log.info("JdbcIngestionPersistenceServiceImpl | Saved last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("JdbcIngestionPersistenceServiceImpl | Failed to save last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName, exception);
        }
    }

    @Override
    public void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String id = CommonUtils.generateUUID();
            LocalDate fallbackSuccessDate = LocalDate.of(1970, 1, 1);
            
            jdbcTemplate.update(queryBuilder.getUpsertLastAttemptedDateQuery(),
                    id, tenantId, moduleName, 
                    Date.valueOf(fallbackSuccessDate), Date.valueOf(attemptedDate), 
                    "SYSTEM", now, "SYSTEM", now);

            log.info("JdbcIngestionPersistenceServiceImpl | Saved last_attempted_date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("JdbcIngestionPersistenceServiceImpl | Failed to save last_attempted_date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName, exception);
        }
    }

    @Override
    public void createLegacyJob(String tenantId, String moduleName, LocalDate date) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            String jobId = CommonUtils.generateUUID();
            
            jdbcTemplate.update(queryBuilder.getInsertLegacyJobQuery(),
                    jobId, tenantId, moduleName, 
                    Date.valueOf(date), "NOT_STARTED", null,
                    "SYSTEM", now, "SYSTEM", now);

            log.debug("JdbcIngestionPersistenceServiceImpl | Inserted legacy job for tenant {} module {} date {}", tenantId, moduleName, date);
        } catch (Exception exception) {
            log.error("JdbcIngestionPersistenceServiceImpl | Failed to insert legacy job for tenant {} module {} date {}", tenantId, moduleName, date, exception);
        }
    }

    @Override
    public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            
            jdbcTemplate.update(queryBuilder.getUpdateLegacyJobStatusQuery(),
                    status, requestData, responseData, now, jobId);

            log.info("JdbcIngestionPersistenceServiceImpl | Updated legacy job {} to status {}", jobId, status);
        } catch (Exception exception) {
            log.error("JdbcIngestionPersistenceServiceImpl | Failed to update legacy job {} to status {}", jobId, status, exception);
        }
    }
}
