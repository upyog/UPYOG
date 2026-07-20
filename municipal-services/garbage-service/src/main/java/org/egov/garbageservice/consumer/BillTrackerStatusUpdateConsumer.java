package org.egov.garbageservice.consumer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.egov.garbageservice.util.GrbgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer responsible for consuming bill/billing related events and updating
 * the GrbgBillTracker records accordingly.
 *
 * Behavior:
 * - Listens on the configured Kafka topic for bill update events.
 * - Parses incoming JSON to extract necessary bill fields (id, tenantId, amount, status).
 * - Builds/updates GrbgBillTracker and persists via GarbageBillTrackerRepository.
 *
 * Notes:
 * - JSON paths and field names used here are examples — adapt them to the producer's schema.
 * - Repository API calls below assume simple save/update methods; replace with the actual repo methods.
 */
@Slf4j
@Service
public class BillTrackerStatusUpdateConsumer {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GrbgUtils grbgUtils;
	
	@Autowired
	private GarbageBillTrackerRepository trackerRepository;



 /**
     * Kafka listener method to receive bill update messages.
     * The topic and groupId can be configured in application properties:
     * kafka.topics.billtracker.status.update
     */
    @KafkaListener(topics = {"garbage-bill-tracker-status-update"})
   public void listen(HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
    try{
    	log.info("record in garbage {}",record);
        RequestInfo reqInfo = objectMapper.convertValue(record.get("requestInfo"), RequestInfo.class);
        AuditDetails audit = grbgUtils.buildCreateAuditDetails(reqInfo);

        String demandId = (String) record.get("demandId");
        String status = (String) record.get("status");
        String consumerCode = (String) record.get("consumerCode");

        if (consumerCode != null) {
            GrbgBillTracker grbgBillTracker = GrbgBillTracker.builder()
                    .status(status)
                    .grbgApplicationId(consumerCode)
                    .demandId(demandId)
                    .auditDetails(audit)
                    .build();

            trackerRepository.updateStatusBillTracker(grbgBillTracker);
        }

    }catch(Exception e){
        log.error("Exception while reading from the queue: ", e);
    }
}
}
