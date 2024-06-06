package org.upyog.chb.kafka.consumer;

import java.util.HashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    /*
    * TODO: Uncomment the below line to start consuming record from kafka.topics.consumer
    * Value of the variable kafka.topics.consumer should be overwritten in application.properties
    */
  //  @KafkaListener(topics = {"${persister.save.communityhall.booking.topic}"})
    public void listen(final HashMap<String, Object> record) {
    	System.out.println("Got record in Kafka consume for  persister.save.communityhall.booking.topic" + record);
        //TODO

    }
    
   // @KafkaListener(topics = {"${persister.save.communityhall.booking.init.topic}"})
    public void listenInit(final HashMap<String, Object> record) {
    	System.out.println("Got record in Kafka consume for  persister.save.communityhall.booking.topic" + record);
        //TODO

    }
}
