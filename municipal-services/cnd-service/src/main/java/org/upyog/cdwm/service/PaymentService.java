package org.upyog.cdwm.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.service.impl.CNDServiceImpl;
import org.upyog.cdwm.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	 *
	 * @param record
	 * @param topic
	 */

	public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
		log.info(" Receipt consumer class entry " + record.toString());
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode().split("-")[0];
			log.info("paymentRequest : " + paymentRequest);
			String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();
			log.info("Payment request processing in CND method for businessService : " + businessService);
			log.info("consumerCode : " + consumerCode);
			if (configs.getModuleName()
					.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
						.getConsumerCode();
				log.info("Updating payment status for water tanker booking : " + applicationNo);
				State state = workflowService.updateWorkflowStatus(paymentRequest, null);
				String applicationStatus = state.getApplicationStatus();
				cndService.updateCNDApplicationDetails(null, paymentRequest, applicationStatus);
			}
			
		} catch (IllegalArgumentException e) {
			log.error(
					"Illegal argument exception occured while sending notification CND Service : " + e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred while sending notification CND Service : ", e);
		}

	}


}
