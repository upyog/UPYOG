package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.model.ErrorLogDTO;


import org.upyog.dashboard.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.entity.DailyIngestionData;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.producer.DashboardProducer;
import org.upyog.dashboard.service.AuditService;
import org.upyog.dashboard.util.JsonUtil;

@Service
@ConditionalOnProperty(name = "dashboard-data.persister.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaAuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditServiceImpl.class);

    @Autowired
    private DashboardProducer producer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DashboardProperties dashboardProperties;

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
                    .ingestionStatus(status).createdBy(DashboardConstants.SYSTEM_USER)
                    .createdTime(now).lastModifiedBy(DashboardConstants.SYSTEM_USER).lastModifiedTime(now).build();

            Map<String, Object> kafkaMessage = new HashMap<>();
            kafkaMessage.put("dailyIngestionData", Collections.singletonList(record));
            producer.push(dashboardProperties.getSaveIngestionDetailTopic(), kafkaMessage);

            if ("FAILURE".equals(status)) {
                ErrorLogDTO errorLog = ErrorLogDTO.builder()
                        .id(CommonUtils.generateUUID())
                        .tenantId(first != null ? first.getUlb() : null)
                        .moduleName(first != null ? first.getModule() : null)
                        .errorDate(first != null ? first.getDate() : null)
                        .issueDescription(responseOrError)
                        .createdTime(now)
                        .createdBy(DashboardConstants.SYSTEM_USER)
                        .build();
                Map<String, Object> errorKafkaMessage = new HashMap<>();
                errorKafkaMessage.put("errorLog", Collections.singletonList(errorLog));
                producer.push(dashboardProperties.getSaveAdapterErrorLogTopic(), errorKafkaMessage);
            }
        } catch (Exception exception) {
            log.error("KafkaAuditServiceImpl | failed to push ingestion record to Kafka", exception);
        }
    }
}
