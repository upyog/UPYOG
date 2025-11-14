package org.egov.garbageservice.consumer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.egov.collection.model.PaymentRequest;
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


@Slf4j
@Service
public class BillTrackerStatusUpdateConsumer {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GrbgUtils grbgUtils;
	
	@Autowired
	private GarbageBillTrackerRepository trackerRepository;

//	@KafkaListener(topics = {"${kafka.topics.receipt.create}","${kafka.topics.notification.pg.save.txns}"})
//    @KafkaListener(topics = {"${kafka.topics.bill.tracker.status.update}"}, groupId = "update-tracker-group")
//	public void trackerStatusUpdate(Map<String, Object> consumerRecord,
//			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//		DocumentContext context = null;
//
//		try {
//			context = JsonPath.parse(objectMapper.writeValueAsString(consumerRecord));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace(); // or use logger.error("Error serializing record", e);
//		}
//		String paymentId = objectMapper.convertValue(context.read("$.Payment.id"), String.class);
//		List<Bill> bills = Arrays
//				.asList(objectMapper.convertValue(context.read("$.Payment.paymentDetails.*.bill"), Bill[].class));
//
//		RequestInfo requestInfo = objectMapper.convertValue(context.read("$.RequestInfo"), RequestInfo.class);
////		billReq.setBills(bills);
////		billReq.setRequestInfo(requestInfo);
//
//	}
    @KafkaListener(topics = {"garbage-bill-tracker-status-update"})
    public void listen(HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try{
    		Bill bill = objectMapper.convertValue(record.get("bill"), Bill.class);
    		RequestInfo reqInfo = objectMapper.convertValue(record.get("requestInfo"), RequestInfo.class);

			AuditDetails audit = grbgUtils.buildCreateAuditDetails(reqInfo);
			bill.getBillDetails().stream().forEach(detail->{
				GrbgBillTracker grbgBillTracker = null;
				if(detail.getAdditionalDetails().get("type").asText().equals("ARREAR")) {
		    		String year = detail.getAdditionalDetails().get("financialYear").asText();
			        String[] parts = year.split("-");
			        String startYear = parts[0];
			        String endYear = parts[1];
			        String endShort = endYear.substring(2);

			        String yearConvert =  startYear + "-" + endShort;
					 grbgBillTracker = GrbgBillTracker.builder().status(bill.getStatus().toString()).grbgApplicationId(bill.getConsumerCode())
							.type(detail.getAdditionalDetails().get("type").asText()).auditDetails(audit).year(yearConvert).build();
					
				}else {
					 grbgBillTracker = GrbgBillTracker.builder().status(bill.getStatus().toString()).grbgApplicationId(bill.getConsumerCode())
							.month(detail.getAdditionalDetails().get("MONTH").asText()).type(detail.getAdditionalDetails().get("type").asText()).auditDetails(audit).build();
				}
				
				trackerRepository.updateStatusBillTracker(grbgBillTracker);
			});
//    		System.out.println("kasjhka");

        }catch(Exception e){
            log.error("Exception while reading from the queue: ", e);
        }
    }
	
	
}
