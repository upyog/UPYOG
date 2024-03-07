package org.egov.ptr.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.ProcessInstance;
import org.egov.ptr.models.ProcessInstanceRequest;
import org.egov.ptr.models.collection.PaymentRequest;
import org.egov.ptr.models.workflow.ProcessInstanceResponse;
import org.egov.ptr.models.workflow.State;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentNotificationService {

	@Autowired
	private NotificationUtil util;

	@Autowired
	private ObjectMapper mapper;
	
	private PetConfiguration config;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsUrl;

	@Autowired
	private PetConfiguration configs;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	/**
	 *
	 * @param record
	 * @param topic
	 */
	public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
		log.info(" Receipt consumer class entry "+ record.toString());
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			log.info("Payment request in pet method: "+ paymentRequest.toString());
			String businessServiceString = "pet-services";
			if (businessServiceString.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				updateWorkflowStatus(paymentRequest);
			}
		} catch (IllegalArgumentException e) {
	        log.error("Illegal argument exception occurred pet: " + e.getMessage());
	    } catch (Exception e) {
	        log.error("An unexpected exception occurred pet: " + e.getMessage());
	    }

	}

	public void updateWorkflowStatus(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = getProcessInstanceForPTR(paymentRequest);
		log.info(" Process instance of pet application "+ processInstance.toString());
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(paymentRequest.getRequestInfo(),
				Collections.singletonList(processInstance));
		callWorkFlow(workflowRequest);

	}

	private ProcessInstance getProcessInstanceForPTR(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = new ProcessInstance();
		processInstance
				.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
		processInstance.setAction("PAY");
		processInstance.setModuleName("pet-services");
		processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
		processInstance.setBusinessService("ptr");
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;

	}

	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
		log.info(" Workflow Request for pet service for final step "+ workflowReq.toString());
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		log.info(" URL for calling workflow service "+ workflowReq.toString());
		Optional<Object> optional = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(optional.get(), ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}

}
