package org.upyog.pgrai.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.pgrai.service.GrievanceFeignClient;
import org.upyog.pgrai.web.models.ServiceRequest;
import org.upyog.pgrai.web.models.grievanceClient.Grievance;
import org.upyog.pgrai.web.models.grievanceClient.GrievanceMapper;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GrievanceConsumer {

    @Autowired
    private GrievanceFeignClient grievanceFeignClient;

    @Value("${grievance.consumer.enabled:true}")
    private boolean isConsumerEnabled;

    /**
     * Kafka consumer that listens to grievance creation topic and forwards the request
     * to another service using Feign Client.
     *
     * @param record the incoming Kafka message payload
     * @param topic  the Kafka topic the message was received from
     */
    @KafkaListener(topics = {"${egov.grievance.es.consumer.topic}"})
    public void consume(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        if (!isConsumerEnabled) {
            log.info("Grievance consumer is disabled via configuration.");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ServiceRequest request = mapper.convertValue(record, ServiceRequest.class);
            Grievance grievance = GrievanceMapper.toGrievance(request);
            Map<String, String> response = grievanceFeignClient.createGrievance(grievance);

            log.info("Grievance created with response: {}", response);
        }
        catch (FeignException fe) {
            int status = fe.status();

            if (status == 401) {
                log.error("Unauthorized (401) - Invalid credentials or token expired: {}", fe.getMessage(), fe);
            } else if (status == 403) {
                log.error("Forbidden (403) - Access denied to grievance API: {}", fe.getMessage(), fe);
            } else if (status == 500) {
                log.error("Internal Server Error (500) - Issue at grievance service: {}", fe.getMessage(), fe);
            } else {
                log.error("Feign client error with status {}: {}", status, fe.getMessage(), fe);
            }
        } catch (SerializationException se) {
            log.error("Kafka deserialization failed: {}", se.getMessage(), se);
        }
        catch (IllegalArgumentException je) {
            log.error("JSON conversion error: {}", je.getMessage(), je);
        }
        catch (Exception e) {
            log.error("Unhandled exception while processing grievance request: {}", e.getMessage(), e);
        }
    }
}
