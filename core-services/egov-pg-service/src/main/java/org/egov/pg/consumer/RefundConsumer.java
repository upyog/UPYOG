package org.egov.pg.consumer;

import java.util.HashMap;

import org.egov.pg.models.CollectionPayment;
import org.egov.pg.models.PaymentRequest;
import org.egov.pg.service.RefundService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RefundConsumer {

    private final RefundService refundService;
    
    private final ObjectMapper mapper;

    public RefundConsumer(RefundService refundService,ObjectMapper mapper) {
        this.refundService = refundService;
        this.mapper = mapper;
    }

    @KafkaListener(topics = ("${kafka.topic.initiate.pg.refund}"))
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
            refundService.processRefund(paymentRequest, topic);
        } catch (Exception ex) {
            StringBuilder builder = new StringBuilder("Error while listening to value: ").append(record)
                    .append("on topic: ").append(topic);
            log.error(builder.toString(), ex);
        }
    }
}
