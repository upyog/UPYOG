package org.egov.ptr.consumer;

import java.util.HashMap;

import org.egov.ptr.service.PaymentNotificationService;
import org.egov.ptr.web.contracts.PetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetNotificationConsumer {



    @Autowired
    private PaymentNotificationService paymentNotificationService;
    
    @Autowired
    private ObjectMapper mapper;


//    @KafkaListener(topics = {"${ptr.kafka.create.topic}"})
//    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//    	
//        PetRequest petRequest = new PetRequest();
//        try {
//        	
//            log.debug("Consuming record: " + record);
//            petRequest = mapper.convertValue(record, PetRequest.class);
//        } catch (final Exception e) {
//        	
//            log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
//        }
//        
//        log.info("property Received: "+petRequest.getPetApplication().getApplicationNumber());
//
//        Source source = propertyRequest.getProperty().getSource();
//
//        if (source == null || !source.equals(Source.DATA_MIGRATION))
//            notificationService.process(petRequest,topic);
//    }
//
//
//    @KafkaListener(topics = {"${kafka.topics.notification.fullpayment}","${kafka.topics.notification.pg.save.txns}"})
//    public void listenPayments(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//    	
//        paymentNotificationService.process(record,topic);
//    }
//




}
