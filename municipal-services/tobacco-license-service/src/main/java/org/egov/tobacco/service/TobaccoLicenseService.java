package org.egov.tobacco.service;

import org.egov.tobacco.web.models.TobaccoLicenseRequest;
import org.egov.tobacco.web.models.TobaccoLicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID; 

@Service
public class TobaccoLicenseService {

    private static final String TOPIC = "save-tobacco-application";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public TobaccoLicenseResponse createLicense(TobaccoLicenseRequest request) {
        // Generate acknowledgment number
        String ackNumber = "TL-" + UUID.randomUUID().toString().substring(0, 8) + "-" + Instant.now().getEpochSecond();
        request.setAcknowledgmentNumber(ackNumber);

        // Publish the request to Kafka topic
        kafkaTemplate.send(TOPIC, request);

        // Return response immediately
        return new TobaccoLicenseResponse(ackNumber, "Application received successfully");
    }
}

