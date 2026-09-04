package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.model.ErrorLogDTO;
import org.upyog.dashboard.util.CommonUtils;
import org.upyog.dashboard.repository.querybuilder.AuditQueryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.entity.DailyIngestionData;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.service.AuditService;
import org.upyog.dashboard.util.JsonUtil;

@Service
@ConditionalOnProperty(name = "dashboard-data.persister.enabled", havingValue = "false")
public class JdbcAuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(JdbcAuditServiceImpl.class);

    // Constants to fix Issue 3 (Hardcoded Values)
    private static final String STATUS_FAILURE = DashboardConstants.STATUS_FAILURE;
    private static final String SYSTEM_USER = DashboardConstants.SYSTEM_USER;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void pushIngestionRecord(DashboardPayload data, String requestJson, String responseOrError, String status) {
        try {
            DashboardData first = (data.getData() != null && !data.getData().isEmpty()) ? data.getData().get(0) : null;
            long now = CommonUtils.getCurrentEpochMillis();

            DailyIngestionData record = DailyIngestionData.builder().moduleIngestionId(CommonUtils.generateUUID())
                    .moduleDetailId(null).tenantId(first != null ? first.getUlb() : null)
                    .moduleName(first != null ? first.getModule() : null)
                    .pushDate(first != null ? first.getDate() : null)
                    .requestData(JsonUtil.toJsonString(requestJson, objectMapper))
                    .responseData(JsonUtil.toJsonString(responseOrError, objectMapper))
                    .ingestionStatus(status).createdBy(SYSTEM_USER)
                    .createdTime(now).lastModifiedBy(SYSTEM_USER).lastModifiedTime(now).build();

            // Fix Issue 2: Using MapSqlParameterSource instead of positional params
            MapSqlParameterSource detailParams = new MapSqlParameterSource()
                    .addValue(DashboardConstants.PARAM_MODULE_INGESTION_ID, record.getModuleIngestionId())
                    .addValue(DashboardConstants.PARAM_TENANT_ID, record.getTenantId())
                    .addValue(DashboardConstants.PARAM_MODULE_NAME, record.getModuleName())
                    .addValue(DashboardConstants.PARAM_PUSH_DATE, record.getPushDate())
                    .addValue(DashboardConstants.PARAM_REQUEST_DATA, record.getRequestData())
                    .addValue(DashboardConstants.PARAM_RESPONSE_DATA, record.getResponseData())
                    .addValue(DashboardConstants.PARAM_INGESTION_STATUS, record.getIngestionStatus())
                    .addValue(DashboardConstants.PARAM_EXCEPTION_CODE, record.getExceptionCode())
                    .addValue(DashboardConstants.PARAM_CREATED_BY, record.getCreatedBy())
                    .addValue(DashboardConstants.PARAM_CREATED_TIME, record.getCreatedTime())
                    .addValue(DashboardConstants.PARAM_LAST_MODIFIED_BY, record.getLastModifiedBy())
                    .addValue(DashboardConstants.PARAM_LAST_MODIFIED_TIME, record.getLastModifiedTime());

            namedParameterJdbcTemplate.update(AuditQueryBuilder.INSERT_INGESTION_DETAIL, detailParams);

            if (STATUS_FAILURE.equals(status)) {
                ErrorLogDTO errorLog = ErrorLogDTO.builder()
                        .id(CommonUtils.generateUUID())
                        .tenantId(first != null ? first.getUlb() : null)
                        .moduleName(first != null ? first.getModule() : null)
                        .errorDate(first != null ? first.getDate() : null)
                        .issueDescription(responseOrError)
                        .createdTime(now)
                        .createdBy(SYSTEM_USER)
                        .build();

                // Fix Issue 4: Using MapSqlParameterSource
                MapSqlParameterSource errorParams = new MapSqlParameterSource()
                        .addValue(DashboardConstants.PARAM_ID, errorLog.getId())
                        .addValue(DashboardConstants.PARAM_TENANT_ID, errorLog.getTenantId())
                        .addValue(DashboardConstants.PARAM_MODULE_NAME, errorLog.getModuleName())
                        .addValue(DashboardConstants.PARAM_ERROR_DATE, errorLog.getErrorDate())
                        .addValue(DashboardConstants.PARAM_ISSUE_DESCRIPTION, errorLog.getIssueDescription())
                        .addValue(DashboardConstants.PARAM_CREATED_TIME, errorLog.getCreatedTime())
                        .addValue(DashboardConstants.PARAM_CREATED_BY, errorLog.getCreatedBy());

                namedParameterJdbcTemplate.update(AuditQueryBuilder.INSERT_ADAPTER_INGESTION_ERROR_LOG, errorParams);
            }
        } catch (Exception exception) {
            log.error("JdbcAuditServiceImpl | failed to push ingestion record to Database", exception);
        }
    }
}
