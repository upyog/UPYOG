package org.egov.garbageservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.model.SchedulerLog;
import org.egov.garbageservice.repository.SchedulerLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerLogConsumer {

    private final SchedulerLogRepository schedulerLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.scheduler.log}")
    public void listen(final Map<String, Object> consumerRecord) {
        try {
            SchedulerLog schedulerLog = objectMapper.convertValue(consumerRecord.get("schedulerLog"), SchedulerLog.class);
            schedulerLogRepository.save(schedulerLog);
        } catch (Exception e) {
            log.error("Error processing scheduler log message", e);
        }
    }
}