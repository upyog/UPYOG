package org.egov.noc.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.egov.noc.service.PaymentUpdateService;
import org.egov.noc.web.model.bill.PaymentRequest;
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

//    @KafkaListener(topics = {"${kafka.topics.receipt.create}"})
//    public void listenPayments(final PaymentRequest record) {
//        log.info("Start ReceiptConsumer.listenPayments method.","{}",record);
//        paymentUpdateService.process(record);
//    }

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
