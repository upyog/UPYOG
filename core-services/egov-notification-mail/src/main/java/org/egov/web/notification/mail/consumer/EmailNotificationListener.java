package org.egov.web.notification.mail.consumer;

import java.util.*;


import org.egov.web.notification.mail.consumer.contract.EmailRequest;
import org.egov.web.notification.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailNotificationListener {

    
    private EmailService emailService;
    
    private ObjectMapper objectMapper;

    @Autowired
    public EmailNotificationListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "${kafka.topics.notification.mail.name}")
    public void listen(final HashMap<String, Object> record) {

        EmailRequest emailRequest;
        log.info("Received email notification record: {}", record);
        // If already wrapped as EmailRequest
        if (record.containsKey("email") && record.get("email") instanceof Map) {
            emailRequest = objectMapper.convertValue(record, EmailRequest.class);
        } 
        // If flat email payload
        else {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("email", record);
            emailRequest = objectMapper.convertValue(wrapper, EmailRequest.class);
        }

        emailService.sendEmail(emailRequest.getEmail());
        log.info("Processed email notification for: {}", emailRequest.getEmail().getEmailTo());
    }

    
    

}
