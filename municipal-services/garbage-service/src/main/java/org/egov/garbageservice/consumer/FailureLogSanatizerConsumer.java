package org.egov.garbageservice.consumer;

import java.util.HashMap;
import java.util.List;

import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Component
public class FailureLogSanatizerConsumer {
	
	@Autowired
	private GarbageBillTrackerRepository garbageBillTrackerRepository;


    @KafkaListener(topics = {"${kafka.topics.sanatize.failure}"})
    public void sanatizeFailure(List<String> Ulbs) {
    	
    	try {
    		garbageBillTrackerRepository.sanatizeBillFailure(Ulbs);
    	}catch(Exception ex) {
    		log.error(ex.getMessage());
    	}
    		
//        if(topic.equalsIgnoreCase(config.getReceiptTopic())){
//            paymentUpdateService.process(record);
//            paymentNotificationService.process(record, topic);
//        }
//        else
//        	paymentNotificationService.process(record, topic);

    }
}
