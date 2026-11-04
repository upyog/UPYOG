package org.upyog.externalaudit.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Fail-open, asynchronous publisher for external API audit events.
 * Kafka unavailability must not fail the originating business call.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(KafkaTemplate.class)
public class ExternalApiAuditProducer implements ExternalApiAuditPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends the event asynchronously. Exceptions from {@code send} and from the
     * completion callback are logged only — the caller is never failed.
     */
    @Override
    public void publishAsync(String topic, Object event) {
        try {
            kafkaTemplate.send(topic, event).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish integration audit event to topic {}: {}", topic, exception.getMessage());
                }
            });
        } catch (Exception exception) {
            log.error("Failed to publish integration audit event to topic {}: {}", topic, exception.getMessage());
        }
    }
}
