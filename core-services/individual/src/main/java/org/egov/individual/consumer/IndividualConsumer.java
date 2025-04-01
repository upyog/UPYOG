package org.egov.individual.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.individual.service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IndividualConsumer {

    private final IndividualService individualService;

    private final ObjectMapper objectMapper;

    @Autowired
    public IndividualConsumer(IndividualService individualService,
                              @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.individualService = individualService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${individual.consumer.bulk.create.topic}")
    public List<Individual> bulkCreate(Map<String, Object> consumerRecord,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            IndividualBulkRequest request = objectMapper.convertValue(consumerRecord, IndividualBulkRequest.class);
            return individualService.create(request, true);
        } catch (Exception exception) {
            log.error("error in individual consumer bulk create", exception);
            return Collections.emptyList();
        }
    }

    @KafkaListener(topics = "${individual.consumer.bulk.update.topic}")
    public List<Individual> bulkUpdate(Map<String, Object> consumerRecord,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            IndividualBulkRequest request = objectMapper.convertValue(consumerRecord, IndividualBulkRequest.class);
            return individualService.update(request, true);
        } catch (Exception exception) {
            log.error("error in individual consumer bulk update", exception);
            return Collections.emptyList();
        }
    }

    @KafkaListener(topics = "${individual.consumer.bulk.delete.topic}")
    public List<Individual> bulkDelete(Map<String, Object> consumerRecord,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            IndividualBulkRequest request = objectMapper.convertValue(consumerRecord, IndividualBulkRequest.class);
            return individualService.delete(request, true);
        } catch (Exception exception) {
            log.error("error in individual consumer bulk delete", exception);
            return Collections.emptyList();
        }
    }
}
