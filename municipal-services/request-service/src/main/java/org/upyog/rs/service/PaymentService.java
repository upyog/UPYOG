package org.upyog.rs.service;

import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.RequestServiceRepository;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.service.impl.MobileToiletServiceImpl;
import org.upyog.rs.service.impl.WaterTankerServiceImpl;
import org.upyog.rs.util.IdgenUtil;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;
import org.upyog.rs.web.models.workflow.ProcessInstance;
import org.upyog.rs.web.models.workflow.ProcessInstanceRequest;
import org.upyog.rs.web.models.workflow.ProcessInstanceResponse;
import org.upyog.rs.web.models.workflow.State;

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
	private RequestServiceConfiguration configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private RequestServiceRepository repo;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private WaterTankerServiceImpl waterTankerService;

	@Autowired
	private MobileToiletServiceImpl mobileToiletService;

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

	/*public State updateWorkflowStatus(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = getProcessInstanceForRS(paymentRequest);
		log.info(" Process instance of Request Service application " + processInstance.toString());
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(paymentRequest.getRequestInfo(),
				Collections.singletonList(processInstance));
		State state = callWorkFlow(workflowRequest);

		return state;

	}

	private ProcessInstance getProcessInstanceForRS(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = new ProcessInstance();
		processInstance
				.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
		processInstance.setAction(RequestServiceConstants.ACTION_PAY);
		processInstance.setModuleName(configs.getModuleName());
		processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
		processInstance.setBusinessService(configs.getBusinessServiceName());
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;

	}

	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
		log.info(" Workflow Request for Request service for final step " + workflowReq.toString());
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		log.info(" URL for calling workflow service " + workflowReq.toString());
		Object workflow = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(workflow, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	} */

/*	private WaterTankerBookingDetail updateApplicationStatus(String applicationStatus, PaymentRequest paymentRequest) {
		WaterTankerBookingDetail waterTankerDetail = repo
				.getWaterTankerBookingDetails(WaterTankerBookingSearchCriteria.builder()
						.bookingNo(
								paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode())
						.build())
				.get(0);

		if (waterTankerDetail == null) {
			log.info("Application not found in consumer class while updating status");
			return null;
		}

		WaterTankerBookingRequest waterTankerRequest = WaterTankerBookingRequest.builder()
				.requestInfo(paymentRequest.getRequestInfo()).build();

		waterTankerDetail.getAuditDetails()
				.setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
		waterTankerDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
		waterTankerDetail.setBookingStatus(applicationStatus);
		waterTankerDetail.setPaymentDate(System.currentTimeMillis());
		waterTankerRequest.setWaterTankerBookingDetail(waterTankerDetail);
		log.info("Water Tanker Request to update application status in consumer : " + waterTankerRequest);
		repo.updateWaterTankerBooking(waterTankerRequest);

		return waterTankerDetail;

	} */

}
