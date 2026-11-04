package upyog.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import upyog.service.PaymentUpdateService;

import java.util.HashMap;

/**
 * Kafka consumer for payment receipt events.
 */
@Component
@Slf4j
public class PaymentUpdateConsumer {

    @Autowired
    private PaymentUpdateService paymentUpdateService;

     /**
     * Listens to payment receipt topic and processes the incoming record.
     *
     * @param record payment payload from Kafka
     * @param topic  source Kafka topic name
     */
    @KafkaListener(topics = {"${kafka.topics.receipt.create}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Estate Management received payment update from topic: {}", topic);
        try {
            paymentUpdateService.process(record, topic);
        } catch (JsonProcessingException e) {
            log.error("Error processing payment update in Estate Management", e);
        }
    }
}
