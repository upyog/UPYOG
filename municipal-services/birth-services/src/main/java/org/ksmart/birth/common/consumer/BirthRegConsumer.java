package org.ksmart.birth.common.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.common.services.BirthNotificationService;
import org.ksmart.birth.utils.CommonUtils;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;

@Slf4j
@Component
public class BirthRegConsumer {
	
	 private BirthNotificationService notificationService;
	
	@Autowired
    public BirthRegConsumer(BirthNotificationService notificationService ) {
		this.notificationService=notificationService;
	}
      
	 @KafkaListener(topics = {"${persister.ksmart.save.birth.topic}","${persister.ksmart.update.birth.topic}"})
	    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
	        ObjectMapper mapper = new ObjectMapper();
	        NewBirthDetailRequest request = new NewBirthDetailRequest();
	        try {
	        	request = mapper.convertValue(record, NewBirthDetailRequest.class);
	        } catch (final Exception e) {
	            log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
	        }
 
	        notificationService.process(request);
	    }
     

}
