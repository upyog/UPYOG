package org.upyog.adapter.service.impl;

import org.upyog.adapter.model.ErrorLogDTO;


import org.upyog.adapter.util.CommonUtils;
import org.upyog.adapter.repository.querybuilder.AuditQueryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.adapter.entity.DailyIngestionData;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.service.AuditService;
import org.upyog.adapter.util.JsonUtil;

@Service
@ConditionalOnProperty(name = "adapter.persister.enabled", havingValue = "false")
public class JdbcAuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(JdbcAuditServiceImpl.class);

    @Autowired
    private AuditQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                    .ingestionStatus(status).createdBy("SYSTEM")
                    .createdTime(now).lastModifiedBy("SYSTEM").lastModifiedTime(now).build();

            String sqlDetail = queryBuilder.getInsertIngestionDetailQuery();
            jdbcTemplate.update(sqlDetail,
                    record.getModuleIngestionId(), record.getTenantId(), record.getModuleName(), record.getPushDate(),
                    record.getRequestData(), record.getResponseData(), record.getIngestionStatus(), record.getExceptionCode(),
                    record.getCreatedBy(), record.getCreatedTime(), record.getLastModifiedBy(), record.getLastModifiedTime());

            if ("FAILURE".equals(status)) {
                ErrorLogDTO errorLog = ErrorLogDTO.builder()
                        .id(CommonUtils.generateUUID())
                        .tenantId(first != null ? first.getUlb() : null)
                        .moduleName(first != null ? first.getModule() : null)
                        .errorDate(first != null ? first.getDate() : null)
                        .issueDescription(responseOrError)
                        .createdTime(now)
                        .createdBy("SYSTEM")
                        .build();

                String sqlError = queryBuilder.getInsertAdapterIngestionErrorLogQuery();
                jdbcTemplate.update(sqlError,
                        errorLog.getId(), errorLog.getTenantId(), errorLog.getModuleName(), errorLog.getErrorDate(),
                        errorLog.getIssueDescription(), errorLog.getCreatedTime(), errorLog.getCreatedBy());
            }
        } catch (Exception exception) {
            log.error("JdbcAuditServiceImpl | failed to push ingestion record to Database", exception);
        }
    }
}
