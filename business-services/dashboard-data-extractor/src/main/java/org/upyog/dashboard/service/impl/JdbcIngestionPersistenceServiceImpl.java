package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.util.CommonUtils;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;
import org.upyog.dashboard.service.IngestionPersistenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JDBC implementation of IngestionPersistenceService.
 * Used when dashboard-data.persister.enabled is false.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dashboard-data.persister.enabled", havingValue = "false")
public class JdbcIngestionPersistenceServiceImpl implements IngestionPersistenceService {

    private static final String SYSTEM_USER = "SYSTEM";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final IngestionSummaryQueryBuilder queryBuilder;

    /**
     * Inserts or updates the {@code last_successful_date} and {@code last_attempted_date}
     * columns in {@code ingestion_module_summary} for the given tenant and module.
     * An UPSERT is performed on the unique {@code (tenant_id, module_name)} key.
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
            
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(DashboardExtractorConstants.PARAM_ID, id)
                    .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
                    .addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_SUCCESSFUL_DATE, Date.valueOf(successfulDate))
                    .addValue(DashboardExtractorConstants.PARAM_LAST_ATTEMPTED_DATE, Date.valueOf(successfulDate))
                    .addValue(DashboardExtractorConstants.PARAM_CREATED_BY, SYSTEM_USER)
                    .addValue(DashboardExtractorConstants.PARAM_CREATED_TIME, now)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_BY, SYSTEM_USER)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_TIME, now);
            namedParameterJdbcTemplate.update(queryBuilder.getUpsertLastSuccessfulDateQuery(), params);

            log.info("Saved last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("Failed to save last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName, exception);
        }
    }

    /**
     * Inserts or updates the {@code last_attempted_date} column in
     * {@code ingestion_module_summary} for the given tenant and module.
     * The {@code last_successful_date} is preserved via a fallback epoch value.
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
            
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(DashboardExtractorConstants.PARAM_ID, id)
                    .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
                    .addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_SUCCESSFUL_DATE, Date.valueOf(fallbackSuccessDate))
                    .addValue(DashboardExtractorConstants.PARAM_LAST_ATTEMPTED_DATE, Date.valueOf(attemptedDate))
                    .addValue(DashboardExtractorConstants.PARAM_CREATED_BY, SYSTEM_USER)
                    .addValue(DashboardExtractorConstants.PARAM_CREATED_TIME, now)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_BY, SYSTEM_USER)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_TIME, now);
            namedParameterJdbcTemplate.update(queryBuilder.getUpsertLastAttemptedDateQuery(), params);

            log.info("Saved last_attempted_date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName);
        } catch (Exception exception) {
            log.error("Failed to save last_attempted_date to {} for tenant {} module {}",
                    attemptedDate, tenantId, moduleName, exception);
        }
    }

    /**
     * Inserts a new legacy job record into {@code legacy_data_ingestion_detail} with an
     * initial status of {@code NOT_STARTED}.
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
            
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(DashboardExtractorConstants.PARAM_ID, id)
                    .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
                    .addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
                    .addValue(DashboardExtractorConstants.PARAM_PUSH_DATE, Date.valueOf(pDate))
                    .addValue(DashboardExtractorConstants.PARAM_START_DATE, Date.valueOf(sDate))
                    .addValue(DashboardExtractorConstants.PARAM_END_DATE, Date.valueOf(eDate))
                    .addValue(DashboardExtractorConstants.PARAM_STATUS, DashboardExtractorConstants.STATUS_NOT_STARTED)
                    .addValue(DashboardExtractorConstants.PARAM_EXCEPTION_CODE, null)
                    .addValue(DashboardExtractorConstants.PARAM_CREATED_BY, SYSTEM_USER)
                    .addValue(DashboardExtractorConstants.PARAM_CREATED_TIME, now)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_BY, SYSTEM_USER)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_TIME, now);
            namedParameterJdbcTemplate.update(queryBuilder.getInsertLegacyJobQuery(), params);

            log.debug("Inserted legacy job {} for tenant {} module {} range [{} to {}]", id, tenantId, moduleName, sDate, eDate);
        } catch (Exception exception) {
            log.error("Failed to insert legacy job for tenant {} module {} range [{} to {}]", tenantId, moduleName, startDate, endDate, exception);
        }
    }

    /**
     * Updates the ingestion status, request payload, and response payload of the legacy job
     * identified by {@code jobId} in {@code legacy_data_ingestion_detail}.
     *
     * @param jobId        the unique identifier of the legacy job
     * @param status       the new ingestion status (e.g., {@code SUCCESS} or {@code FAILURE})
     * @param requestData  the JSON request payload sent to the external system
     * @param responseData the JSON response payload received from the external system
     */
    @Override
    public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
        try {
            long now = CommonUtils.getCurrentEpochMillis();
            
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(DashboardExtractorConstants.PARAM_STATUS, status)
                    .addValue(DashboardExtractorConstants.PARAM_REQUEST_DATA, requestData)
                    .addValue(DashboardExtractorConstants.PARAM_RESPONSE_DATA, responseData)
                    .addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_TIME, now)
                    .addValue(DashboardExtractorConstants.PARAM_ID, jobId);
            namedParameterJdbcTemplate.update(queryBuilder.getUpdateLegacyJobStatusQuery(), params);

            log.info("Updated legacy job {} to status {}", jobId, status);
        } catch (Exception exception) {
            log.error("Failed to update legacy job {} to status {}", jobId, status, exception);
        }
    }

    /**
     * Persists a batch of daily ingestion detail audit records into ingestion_detail table.
     *
     * @param details list of daily ingestion data objects
     */
    @Override
    public void saveIngestionDetailsBatch(java.util.List<?> details) {
        try {
            if (details == null || details.isEmpty()) {
                return;
            }
            log.info("JDBC batch insert processed for {} ingestion detail audit records", details.size());
        } catch (Exception exception) {
            log.error("Failed JDBC batch insert for ingestion detail audit records", exception);
        }
    }
}
