package org.egov.ndc.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.egov.ndc.service.PaymentUpdateService;
import org.egov.ndc.web.model.bill.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReceiptConsumer {

    private PaymentUpdateService paymentUpdateService;


    @Autowired
    public ReceiptConsumer(PaymentUpdateService paymentUpdateService) {
        this.paymentUpdateService = paymentUpdateService;
    }

    @KafkaListener(topics = {"${kafka.topics.receipt.create}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void listenPayments(final String rawRecord) {
        log.info("Incoming raw message: {}", rawRecord);
        try {
            PaymentRequest record = new ObjectMapper().readValue(rawRecord, PaymentRequest.class);
            paymentUpdateService.process(record);
        } catch (Exception e) {
            log.error("Deserialization failed: {}", e.getMessage(), e);
        }
    }

}
