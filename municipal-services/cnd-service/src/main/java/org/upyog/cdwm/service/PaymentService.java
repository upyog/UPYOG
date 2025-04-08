package org.upyog.cdwm.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.service.impl.CNDServiceImpl;
import org.upyog.cdwm.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentDetail;
import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CNDConfiguration configs;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private CNDServiceImpl cndService;

	/**
	 * Processes a payment notification received from the Kafka topic. This method is triggered 
	 * by a consumer listening to payment events. It parses the received record into a {@link PaymentRequest},
	 * extracts the relevant application and business details, and invokes the workflow and update logic 
	 * for CND (Community Notification/Disposal) applications.
	 *
	 * <p>If the business service in the payment matches the configured module name, it updates the 
	 * workflow status and invokes the CND application update logic accordingly.</p>
	 *
	 * @param record the payment record received from the Kafka topic, containing the payment information.
	 * @param topic the Kafka topic name from which the payment event was consumed.
	 * @throws JsonProcessingException if the record cannot be processed into a valid {@link PaymentRequest} object.
	 */
	
	public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
		log.info("Receipt consumer class entry: {}", record);

		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			log.info("paymentRequest: {}", paymentRequest);

			PaymentDetail paymentDetail = paymentRequest.getPayment().getPaymentDetails().get(0);
			String consumerCode = paymentDetail.getBill().getConsumerCode().split("-")[0];
			String businessService = paymentDetail.getBusinessService();

			log.info("Payment request processing in CND method for businessService: {}", businessService);
			log.info("consumerCode: {}", consumerCode);

			if (configs.getModuleName().equals(businessService)) {
				String applicationNo = paymentDetail.getBill().getConsumerCode();
				log.info("Updating payment status for CND application: {}", applicationNo);

				State state = workflowService.updateWorkflowStatus(paymentRequest, null);
				String applicationStatus = state.getApplicationStatus();

				cndService.updateCNDApplicationDetails(null, paymentRequest, applicationStatus);
			}
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument exception occurred while sending notification to CND Service: {}", e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred while sending notification to CND Service: ", e);
		}
	}


}
