package org.upyog.dashboard.service.impl;

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
    private static final String STATUS_FAILURE = "FAILURE";
    private static final String SYSTEM_USER = "SYSTEM";

    @Autowired
    private AuditQueryBuilder queryBuilder;

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

            String sqlDetail = queryBuilder.getInsertIngestionDetailQuery();
            
            // Fix Issue 2: Using MapSqlParameterSource instead of positional params
            MapSqlParameterSource detailParams = new MapSqlParameterSource()
                    .addValue("moduleIngestionId", record.getModuleIngestionId())
                    .addValue("tenantId", record.getTenantId())
                    .addValue("moduleName", record.getModuleName())
                    .addValue("pushDate", record.getPushDate())
                    .addValue("requestData", record.getRequestData())
                    .addValue("responseData", record.getResponseData())
                    .addValue("ingestionStatus", record.getIngestionStatus())
                    .addValue("exceptionCode", record.getExceptionCode())
                    .addValue("createdBy", record.getCreatedBy())
                    .addValue("createdTime", record.getCreatedTime())
                    .addValue("lastModifiedBy", record.getLastModifiedBy())
                    .addValue("lastModifiedTime", record.getLastModifiedTime());
                    
            namedParameterJdbcTemplate.update(sqlDetail, detailParams);

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

                String sqlError = queryBuilder.getInsertAdapterIngestionErrorLogQuery();
                
                // Fix Issue 4: Using MapSqlParameterSource
                MapSqlParameterSource errorParams = new MapSqlParameterSource()
                        .addValue("id", errorLog.getId())
                        .addValue("tenantId", errorLog.getTenantId())
                        .addValue("moduleName", errorLog.getModuleName())
                        .addValue("errorDate", errorLog.getErrorDate())
                        .addValue("issueDescription", errorLog.getIssueDescription())
                        .addValue("createdTime", errorLog.getCreatedTime())
                        .addValue("createdBy", errorLog.getCreatedBy());
                        
                namedParameterJdbcTemplate.update(sqlError, errorParams);
            }
        } catch (Exception exception) {
            log.error("JdbcAuditServiceImpl | failed to push ingestion record to Database", exception);
        }
    }
}
