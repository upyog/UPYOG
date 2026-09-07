package org.upyog.dashboard.producer;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Kafka producer for the adapter-service.
 *
 * <p>This class is a thin wrapper around the eGov tracer library's
 * {@link CustomKafkaTemplate}, which provides consistent serialization,
 * correlation-ID propagation, and observability across all UPYOG microservices.
 * Every component that needs to publish a Kafka message should inject and use
 * this bean rather than calling {@link CustomKafkaTemplate} directly, so that
 * logging and topic routing stay centralized.
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * // Build the Kafka envelope
 * Map<String, Object> message = new HashMap<>();
 * message.put("dailyIngestionData", Collections.singletonList(record));
 *
 * // Publish
 * producer.push(KafkaTopics.SAVE_INGESTION_DETAIL, message);
 * }</pre>
 *
 * <h3>Topic constants</h3>
 * All topic names are defined as compile-time constants in
 * DashboardProperties.  The corresponding
 * INSERT / UPDATE SQL is declared in {@code adapter-service-persister.yml} and
 * executed by the persister service when it consumes the topic.
 *
 * @see org.upyog.dashboard.entity.DailyIngestionData
 * @see org.upyog.dashboard.entity.LegacyIngestionData
 */
/**
 * Class representing the DashboardProducer class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Service
@Slf4j
public class DashboardProducer {

    /**
     * eGov tracer Kafka template.
     * Handles JSON serialization via {@code JsonSerializer} and attaches
     * tracing headers configured in {@code application.properties} under
     * {@code spring.kafka.producer.*}.
     */
    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Serializes {@code value} to JSON and publishes it to the specified
     * Kafka {@code topic}.
     *
     * <p>The method is intentionally fire-and-forget: it does not wait for
     * broker acknowledgement.  Callers that require delivery guarantees should
     * handle retries at the service layer.
     *
     * <p>A single INFO log line is emitted before the send so that the topic
     * name is always visible in application logs regardless of the caller.
     *
     * @param topic the Kafka topic to publish to; use a constant from
     *              DashboardProperties
     *              to avoid hard-coding topic strings at call sites
     * @param value the object to serialize and publish; must be
     *              JSON-serializable by Jackson (the configured
     *              {@code spring.kafka.producer.value-serializer})
     */
    public void push(String topic, Object value) {
        log.info("DashboardProducer | pushing to topic: {}", topic);
        kafkaTemplate.send(topic, value);
    }
}
