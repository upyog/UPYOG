package org.upyog.sv.kafka.consumer;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.sv.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentUpdateConsumer {

	@Autowired
	private PaymentService paymentService;

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("Street Vending Appplication Received to update workflow after PAY ");
		try {
			paymentService.process(record, topic);
		} catch (JsonProcessingException e) {
			log.info("Catch block in listenPayments method of Pet service consumer");
			e.printStackTrace();
		}

	}

//	@KafkaListener(topics = { "${kafka.topics.update.pg.txns}" })
//	public void paymentUpdate(final HashMap<String, Object> record,
//			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//
//		log.info("Street vending Appplication payment status update for  : " + topic + " and record : " + record);
//		//TODO: need to remove after testing
//		log.info("Strigifed json : " + StreetVendingUtil.beuatifyJson(record));
//		paymentService.processTransaction(record, topic, null);
//
//	}
//	
//	
//	@KafkaListener(topics = { "${kafka.topics.save.pg.txns}" })
//	public void paymentStarted(final HashMap<String, Object> record,
//			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//
//		log.info("Street vending Appplication payment started for topic  : " + topic + " and record : " + record);
//		//TODO: need to remove after testing
//		log.info("Strigifed json : " + StreetVendingUtil.beuatifyJson(record));
//		paymentService.processTransaction(record, topic, null);
//
//	}

	/**
	 * Returns the map of the values required from the record
	 * 
	 * @param documentContext The DocumentContext of the record Object
	 * @return The required values as key,value pair
	 */
//	@SuppressWarnings("unused")
//	private Map<String, String> getValuesFromTransaction(DocumentContext documentContext) {
//		String txnStatus, txnAmount, moduleId, tenantId, mobileNumber, module;
//		HashMap<String, String> valMap = new HashMap<>();
//
//		try {
//			txnStatus = documentContext.read("$.Transaction.txnStatus");
//			valMap.put("txnStatus", txnStatus);
//
//			txnAmount = documentContext.read("$.Transaction.txnAmount");
//			valMap.put("txnAmount", txnAmount.toString());
//
//			tenantId = documentContext.read("$.Transaction.tenantId");
//			valMap.put("tenantId", tenantId);
//
//			moduleId = documentContext.read("$.Transaction.consumerCode");
//			valMap.put("moduleId", moduleId);
//			valMap.put("bookingNo", moduleId);
//			// valMap.put("assessmentNumber",moduleId.split(":")[1]);
//
//			mobileNumber = documentContext.read("$.Transaction.user.mobileNumber");
//			valMap.put("mobileNumber", mobileNumber);
//
//			// module =
//			// documentContext.read("$.Transaction.taxAndPayments[0].businessService");
//			// valMap.put("module",module);
//		} catch (Exception e) {
//			log.error("Transaction Object Parsing: ", e);
//			throw new CustomException("PARSING ERROR", "Failed to fetch values from the Transaction Object");
//		}
//
//		return valMap;
//	}
}
