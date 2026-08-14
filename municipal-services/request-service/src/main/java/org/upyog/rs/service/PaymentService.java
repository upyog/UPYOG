package org.upyog.rs.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.repository.RequestServiceRepository;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.service.impl.MobileToiletServiceImpl;
import org.upyog.rs.service.impl.WaterTankerServiceImpl;
import org.upyog.rs.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final ObjectMapper mapper;

	private final RequestServiceConfiguration configs;

	private final ServiceRequestRepository serviceRequestRepository;

	private final RequestServiceRepository repo;

	private final WorkflowService workflowService;

	private final WaterTankerServiceImpl waterTankerService;

	private final MobileToiletServiceImpl mobileToiletService;

	/**
	 *
	 * @param consumerRecord the payment receipt record consumed from Kafka
	 */

	public void process(HashMap<String, Object> consumerRecord) throws JsonProcessingException {
		log.info(" Receipt consumer class entry " + consumerRecord.toString());
		try {
			PaymentRequest paymentRequest = mapper.convertValue(consumerRecord, PaymentRequest.class);
			String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode().split("-")[0];
			log.info("paymentRequest : " + paymentRequest);
			String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();
			log.info("Payment request processing in Request Service method for businessService : " + businessService);
			log.info("consumerCode : " + consumerCode);
			if(configs.getWtModuleName()
					.equals(businessService)){
				String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
						.getConsumerCode();
				log.info("Updating payment status for water tanker booking : " + applicationNo);
				State state = workflowService.updateWorkflowStatus(paymentRequest, null);
				String applicationStatus = state.getApplicationStatus();
				waterTankerService.updateWaterTankerBooking(paymentRequest, applicationStatus);
			}else if(configs.getMtModuleName().equals(businessService)){
					String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
							.getConsumerCode();
					log.info("Updating payment status for mobile Toilet booking : " + applicationNo);
					State state = workflowService.updateMTWorkflowStatus(paymentRequest, null);
					String applicationStatus = state.getApplicationStatus();
					mobileToiletService.updateMobileToiletBooking(paymentRequest, applicationStatus);
			}
		} catch (IllegalArgumentException e) {
			log.error(
					"Illegal argument exception occured while sending notification Request Service : " + e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred while processing payment in Request Service : ", e);
		}

	}

}
