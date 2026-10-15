package org.egov.pt.consumer;

import java.util.HashMap;

import org.egov.pt.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.pt.models.PaymentProcessingUpdateRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyPaymentProcessingConsumer {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PropertyRepository propertyRepository;

	@KafkaListener(topics = { "property-payment-processing" })
	public void paymentProcessingUpdate(HashMap<String, Object> record) {
		PaymentProcessingUpdateRequest request = objectMapper.convertValue(record,PaymentProcessingUpdateRequest.class);
		propertyRepository.updatePaymentProcessing(request.getBillId(), request.isPaymentProcessing(), request.getTxnAmount());
	}
}
