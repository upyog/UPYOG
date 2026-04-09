package com.mseva.gisintegration.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mseva.gisintegration.service.SewerageTaxService;
import com.mseva.gisintegration.service.WaterTaxService;
import com.mseva.gisintegration.service.PropertyService;
import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class InternalGisConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WaterTaxService waterTaxService;

    @Autowired
    private SewerageTaxService sewerageTaxService;
    
    @Autowired
    private PropertyService propertyService;

    /**
     * Consumes data from the receipt topic (Payments).
     */
    @KafkaListener(topics = { "${gis.reciept.topic}" })
    public void listenReceipt(Map<String, Object> record) {
        try {
            JsonNode root = mapper.valueToTree(record);
            JsonNode payment = root.path("Payment");
            JsonNode paymentDetails = payment.path("paymentDetails");
            RequestInfo requestInfo = mapper.treeToValue(root.path("RequestInfo"), RequestInfo.class);

            if (paymentDetails.isArray()) {
                for (JsonNode detail : paymentDetails) {
                    String businessService = detail.path("businessService").asText();
                    log.info("Incoming Receipt for Business Service: {}", businessService);

                    routeToService(businessService, detail, payment, requestInfo);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Receipt message", e);
        }
    }
    /**
     * Consumes data from the property-update-registry topic.
     * Maps the property master data directly from the message.
     */
    @KafkaListener(topics = { "${egov.gis.property.update.topic}" })
    public void listenPropertyUpdate(Map<String, Object> record) {
        try {
            JsonNode root = mapper.valueToTree(record);
            JsonNode propertyNode = root.path("Property");
            
            if (!propertyNode.isMissingNode()) {
                log.info("Incoming Property Update for Property ID: {}", propertyNode.path("propertyId").asText());
                
                RequestInfo requestInfo = mapper.treeToValue(root.path("RequestInfo"), RequestInfo.class);
                
                // Route to PropertyService for direct sync
                propertyService.syncPropertyMaster(propertyNode, requestInfo);
            }
        } catch (Exception e) {
            log.error("Error processing Property Update message", e);
        }
    }
    /**
     * NEW: Consumes data from the Assessment topic.
     * Routes strictly to PropertyService for master data enrichment.
     */
    @KafkaListener(topics = { "${egov.gis.assessment.create.topic}" })
    public void listenAssessment(Map<String, Object> record) {
        try {
            JsonNode root = mapper.valueToTree(record);
            JsonNode assessment = root.path("Assessment");
            
            if (!assessment.isMissingNode()) {
                log.info("Incoming Assessment for Property ID: {}", assessment.path("propertyId").asText());
                
                RequestInfo requestInfo = mapper.treeToValue(root.path("RequestInfo"), RequestInfo.class);
                
                // For Assessment, we route directly to a specific sync method in PropertyService
                propertyService.handleAssessmentSync(assessment, requestInfo);
            }
        } catch (Exception e) {
            log.error("Error processing Assessment message", e);
        }
    }

    private void routeToService(String businessService, JsonNode detail, JsonNode payment, RequestInfo requestInfo) {
        if (businessService.equalsIgnoreCase("WS") || businessService.toLowerCase().contains("water")) {
            waterTaxService.processPayment(detail, payment, requestInfo);
        } 
        else if (businessService.equalsIgnoreCase("SW") || businessService.toLowerCase().contains("sewerage")) {
            sewerageTaxService.processPayment(detail, payment, requestInfo);
        } 
        else if (businessService.equalsIgnoreCase("PT")) {
            propertyService.processPayment(detail, payment, requestInfo);
        }
    }
}