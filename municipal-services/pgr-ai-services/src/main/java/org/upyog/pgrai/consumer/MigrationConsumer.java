package org.upyog.pgrai.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.pgrai.service.MigrationService;
import org.upyog.pgrai.util.PGRConstants;
import org.upyog.pgrai.web.models.pgrv1.ServiceResponse;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer class for handling migration-related Kafka messages.
 * Processes migration requests when the migration feature is enabled.
 */
@ConditionalOnProperty(
        value = "migration.enabled",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
@Component
@RequiredArgsConstructor
public class MigrationConsumer {

    private final MigrationService migrationService;

    private final ObjectMapper mapper;

    /**
     * Listens to the configured Kafka topic for migration requests.
     * Converts the received kafkaRecord into a `ServiceResponse` object and processes it.
     *
     * @param kafkaRecord The Kafka message payload as a map.
     * @param topic  The name of the Kafka topic from which the message was received.
     */
    @KafkaListener(topics = { "${pgr.kafka.migration.topic}" })
    public void listen(final Map<String, Object> kafkaRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received migration request " + kafkaRecord);
            ServiceResponse serviceResponse = mapper.convertValue(kafkaRecord, ServiceResponse.class);

            // Adding tenant ID to MDC for tracing purposes
            MDC.put(PGRConstants.TENANTID_MDC_STRING, serviceResponse.getServices().get(0).getTenantId());

            migrationService.migrate(serviceResponse);
        } catch (Exception e) {
            log.error("Error occurred while processing the kafkaRecord from topic: " + topic, e);
        }
    }
}