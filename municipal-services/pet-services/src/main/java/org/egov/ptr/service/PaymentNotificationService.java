package org.egov.ptr.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.AuditDetails;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.ProcessInstance;
import org.egov.ptr.models.ProcessInstanceRequest;
import org.egov.ptr.models.collection.PaymentRequest;
import org.egov.ptr.models.workflow.ProcessInstanceResponse;
import org.egov.ptr.models.workflow.State;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.PTRConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentNotificationService {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PetConfiguration configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private Producer producer;

	/**
	 * Processes the received payment record and updates the workflow and application status if applicable.
	 *
	 * @param record The payment record received from the Kafka topic.
	 * @param topic The Kafka topic from which the record is consumed.
	 */
	public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
		log.info(" Receipt consumer class entry " + record.toString());
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			log.info("Payment request in pet method: " + paymentRequest.toString());
			String businessServiceString = PTRConstants.PET_MODULE_NAME;
			if (businessServiceString
					.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				State state = updateWorkflowStatus(paymentRequest);
				updateApplicationStatus(state.getApplicationStatus(), paymentRequest);
			}
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument exception occurred pet: " + e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred pet: " + e.getMessage());
		}

	}

	/**
	 * Updates the workflow status based on the payment request.
	 *
	 * @param paymentRequest The payment request object.
	 * @return The updated state of the workflow.
	 */
	public State updateWorkflowStatus(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = getProcessInstanceForPTR(paymentRequest);
		log.info(" Process instance of pet application " + processInstance.toString());
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(paymentRequest.getRequestInfo(),
				Collections.singletonList(processInstance));
		State state = callWorkFlow(workflowRequest);

		return state;

	}

//This process instance is created to update workflow parallely after payment
	private ProcessInstance getProcessInstanceForPTR(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = new ProcessInstance();
		processInstance
				.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
		processInstance.setAction(PTRConstants.ACTION_PAY);
		processInstance.setModuleName(PTRConstants.PET_MODULE_NAME);
		processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
		processInstance.setBusinessService(PTRConstants.PET_BUSINESS_SERVICE);
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;

	}

	/**
	 * Calls the workflow service to update the status of the process instance.
	 *
	 * @param workflowReq The ProcessInstanceRequest containing process details.
	 * @return The updated State object from the workflow service.
	 */
	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
		log.info(" Workflow Request for pet service for final step " + workflowReq.toString());
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		log.info(" URL for calling workflow service " + workflowReq.toString());
		Optional<Object> optional = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(optional.get(), ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}


	/**
	 * Updates the application status in the Pet Registration system based on the payment request.
	 *
	 * @param applicationStatus The updated status to be set.
	 * @param paymentRequest The payment request object containing request details.
	 */
	private void updateApplicationStatus(String applicationStatus, PaymentRequest paymentRequest) {

		AuditDetails auditDetails = AuditDetails.builder()
				.lastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid())
				.lastModifiedTime(System.currentTimeMillis()).build();
		PetRegistrationApplication application = PetRegistrationApplication.builder().auditDetails(auditDetails)
				.status(applicationStatus).build();
		PetRegistrationRequest petRegistrationRequest = PetRegistrationRequest.builder()
				.petRegistrationApplications((List<PetRegistrationApplication>) application).build();
		log.info("Pet Registration Request to update application status in consumer : " + petRegistrationRequest);
		producer.push(configs.getUpdatePtrTopic(), petRegistrationRequest);

	}

}
