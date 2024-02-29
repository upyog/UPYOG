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
	public void process(HashMap<String, Object> record, String topic) {

		PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
		String businessServiceString = config.getBusinessService();
		if (businessServiceString.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
			updateWorkflowStatus(paymentRequest);
		}

	}

	public void updateWorkflowStatus(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = getProcessInstanceForPTR(paymentRequest);
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

		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		Optional<Object> optional = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(optional.get(), ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}

}
