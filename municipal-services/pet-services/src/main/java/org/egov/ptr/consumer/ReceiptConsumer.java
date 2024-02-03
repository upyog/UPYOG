package org.egov.ptr.consumer;

import java.util.HashMap;

import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.service.PaymentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ReceiptConsumer {



	@Autowired
    private PaymentNotificationService paymentNotificationService;

	@Autowired
    private PetConfiguration config;

//    @KafkaListener(topics = {"${kafka.topics.receipt.create}","${kafka.topics.notification.pg.save.txns}"})
//    public void listenPayments(final HashMap<String, Object> record,  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//
//        if(topic.equalsIgnoreCase(config.getReceiptTopic())){
//          
//            paymentNotificationService.process(record, topic);
//        }
//        else paymentNotificationService.process(record, topic);
//
//    }
}
