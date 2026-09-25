package org.upyog.tp.kafka.consumer;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.tp.service.PaymentService;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentUpdateConsumer {

    private final PaymentService paymentService;

    public PaymentUpdateConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = { "${kafka.topics.receipt.create}" })
    public void listen(final Map<String, Object> kafkaRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.info("Tree Pruning Appplication Received to update workflow after PAY on topic: {}", topic);
        try {
            paymentService.process(kafkaRecord, topic);
        } catch (JsonProcessingException e) {
            log.info("Catch block in listenPayments method of Request service consumer");
            e.printStackTrace();
        }

    }
}
