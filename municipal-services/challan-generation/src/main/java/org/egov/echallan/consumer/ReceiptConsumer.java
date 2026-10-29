package org.egov.echallan.consumer;

import lombok.extern.slf4j.Slf4j;

import org.egov.echallan.service.PaymentUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.Map;



@Slf4j
@Component
public class ReceiptConsumer {

    private final PaymentUpdateService paymentUpdateService;

    @Autowired
    public ReceiptConsumer(PaymentUpdateService paymentUpdateService) {
        this.paymentUpdateService = paymentUpdateService;
    }

    @KafkaListener(topics = {"${kafka.topics.receipt.create}"},concurrency = "${kafka.consumer.config.concurrency.count}")
    public void listen(final Map<String, Object> messagePayload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
        paymentUpdateService.process(messagePayload);
        } catch (final Exception e) {
            log.error("Error while listening to value: " + messagePayload + " on topic: " + topic + ": ", e.getMessage());
        }
    }
}
