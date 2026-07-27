package org.upyog.adapter.service.impl;


import org.upyog.adapter.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.upyog.adapter.common.constants.KafkaTopics;
import org.upyog.adapter.entity.DailyIngestionData;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.service.AuditService;
import org.upyog.adapter.util.JsonUtil;

@Service
@ConditionalOnProperty(name = "adapter.persister.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaAuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditServiceImpl.class);

    @Autowired
    private AdapterProducer producer;

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

            Map<String, Object> kafkaMessage = new HashMap<>();
            kafkaMessage.put("dailyIngestionData", Collections.singletonList(record));
            producer.push(KafkaTopics.SAVE_INGESTION_DETAIL, kafkaMessage);

            if ("FAILURE".equals(status)) {
                org.upyog.adapter.model.ErrorLogDTO errorLog = org.upyog.adapter.model.ErrorLogDTO.builder()
                        .id(CommonUtils.generateUUID())
                        .tenantId(first != null ? first.getUlb() : null)
                        .moduleName(first != null ? first.getModule() : null)
                        .errorDate(first != null ? first.getDate() : null)
                        .issueDescription(responseOrError)
                        .createdTime(now)
                        .createdBy("SYSTEM")
                        .build();
                Map<String, Object> errorKafkaMessage = new HashMap<>();
                errorKafkaMessage.put("errorLog", Collections.singletonList(errorLog));
                producer.push(KafkaTopics.SAVE_ADAPTER_ERROR_LOG, errorKafkaMessage);
            }
        } catch (Exception exception) {
            log.error("KafkaAuditServiceImpl | failed to push ingestion record to Kafka", exception);
        }
    }
}
