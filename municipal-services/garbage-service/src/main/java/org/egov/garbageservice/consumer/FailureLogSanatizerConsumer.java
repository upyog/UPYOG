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

/**
 * Consumer that listens for ULB identifiers for which failed bill entries
 * should be sanitized/cleaned up.
 *
 * Expected payload: a list of ULB codes (List<String>).
 *
 * Responsibilities:
 * - Validate incoming list.
 * - Delegate to GarbageBillTrackerRepository.sanatizeBillFailure(...) to perform the cleanup.
 * - Log success/failure and avoid throwing to keep consumer resilient.
 */
@Slf4j
@Component
public class FailureLogSanatizerConsumer {
	
	@Autowired
	private GarbageBillTrackerRepository garbageBillTrackerRepository;

   /**
     * Listens to the configured topic and invokes repository to sanitize failures
     * for the provided list of ULBs.
     *
     * Topic value is read from application properties: kafka.topics.sanatize.failure
     */
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
