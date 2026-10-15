package org.egov.garbageservice.consumer;

import java.util.HashMap;
import org.egov.garbageservice.model.PaymentProcessingUpdateRequest;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GrbgPaymentProcessingConsumer {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private GarbageBillTrackerRepository trackerRepository;

	@KafkaListener(topics = { "grbg-payment-processing" })
	public void paymentProcessingUpdate(HashMap<String, Object> record) {
		PaymentProcessingUpdateRequest request = objectMapper.convertValue(record,PaymentProcessingUpdateRequest.class);
		trackerRepository.updatePaymentProcessing(request.getBillId(), request.isPaymentProcessing(), request.getTxnAmount());
	}
}
