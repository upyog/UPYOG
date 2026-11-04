package upyog.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import upyog.notification.service.NotificationProcessor;

import java.util.HashMap;

/**
 * Async notification entry point. Listens to domain persister topics — same pattern as CHB.
 * Adding a new notification = MDMS config entry only (trigger, channels, messages, variables).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "upyog.notification.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationConsumer {

    private final NotificationProcessor notificationProcessor;

    @KafkaListener(topics = {
            "${save-estate-management-allotment-topic}",
            "${update-estate-management-allotment-topic}"
    })
    public void onAllotmentEvent(final HashMap<String, Object> message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Notification consumer received event on topic={}", topic);
        try {
            notificationProcessor.process(topic, message);
        } catch (Exception e) {
            log.error("Notification processing failed for topic={}: {}", topic, e.getMessage(), e);
        }
    }
}
