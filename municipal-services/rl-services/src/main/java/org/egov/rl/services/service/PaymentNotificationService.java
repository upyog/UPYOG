package org.egov.rl.services.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.rl.services.config.RentLeaseConfiguration;
import org.egov.rl.services.models.*;
import org.egov.rl.services.models.collection.PaymentRequest;
import org.egov.rl.services.models.workflow.ProcessInstanceRequest;
import org.egov.rl.services.models.workflow.ProcessInstanceResponse;
import org.egov.rl.services.models.workflow.State;
import org.egov.rl.services.repository.AllotmentRepository;
import org.egov.rl.services.repository.ServiceRequestRepository;
import org.egov.rl.services.util.RLConstants;
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
	private RentLeaseConfiguration configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private AllotmentRepository allotmentRepository;
	
	@Autowired
	private NotificationService notificationService;

	/**
	 * Process the incoming record and topic from the payment notification consumer.
	 * Performs defensive null checks and validates the payment request before proceeding.
	 */
	public void process(PaymentRequest paymentRequest, String topic) throws JsonProcessingException {
		try {
			if (paymentRequest == null || paymentRequest.getPayment() == null || 
					paymentRequest.getPayment().getPaymentDetails() == null || 
					paymentRequest.getPayment().getPaymentDetails().isEmpty()) {
				log.error("Invalid payment request structure");
				return;
			}

			String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();

			if (RLConstants.RL_SERVICE_NAME.equals(businessService)) {
				processPaymentAndUpdateApplication(paymentRequest);
			} else {
				log.debug("Ignoring payment with business service: {} (expected: {})", businessService, RLConstants.RL_SERVICE_NAME);
			}
		} catch (Exception e) {
			log.error("Error processing payment notification: {}", e.getMessage(), e);
		}
	}

	/**
	 * Main method to process payment and update application status
	 * 1. Calls workflow with PAY action to transition to APPROVED
	 * 2. Fetches updated process instance to get the current status
	 * 3. Generates petRegistrationNumber if needed
	 * 4. Updates database with the status from workflow and petRegistrationNumber
	 */
	private void processPaymentAndUpdateApplication(PaymentRequest paymentRequest) {
		String applicationNumber = null;
		String tenantId = null;
		
		try {
			applicationNumber = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
			tenantId = paymentRequest.getPayment().getTenantId();
			log.info("Processing payment for application: {}", applicationNumber);
			
			AllotmentCriteria criteria = AllotmentCriteria.builder()
					.applicationNumbers(Collections.singleton(applicationNumber))
					.tenantId(tenantId)
					.build();

			List<AllotmentDetails> applications = allotmentRepository.getAllotmentSearch(criteria);
			boolean status=applications.get(0).getStatus().equalsIgnoreCase(RLConstants.PENDING_FOR_PAYMENT_RL_APPLICATION);
			log.info("Processing payment for application: {}", applicationNumber);
			
			// Transition workflow with PAY action
			State workflowState = transitionWorkflowToApproved(paymentRequest,status?"PAY":"PAY_SETTLEMENT_AMOUNT");
			
			if (workflowState == null) {
				log.error("Workflow transition failed for application: {}", applicationNumber);
				return;
			}
			
			// Fetch the updated process instance to get the current state
			ProcessInstance updatedProcessInstance = fetchProcessInstanceFromWorkflow(applicationNumber, tenantId, paymentRequest.getRequestInfo());
			if (updatedProcessInstance != null && updatedProcessInstance.getState() != null) {
				workflowState = updatedProcessInstance.getState();
			} else {
				log.warn("Could not fetch updated process instance for application: {}, using state from workflow transition", applicationNumber);
			}
			
			// Get the application status from workflow state
			String applicationStatus = workflowState.getApplicationStatus() != null ? workflowState.getApplicationStatus() : workflowState.getState();
			
			log.info("Workflow state for application {} - State: {}, ApplicationStatus: {}, Final status: {}", 
					applicationNumber, workflowState.getState(), workflowState.getApplicationStatus(), applicationStatus);
			
			// For payment completion, we expect APPROVED status
			if (!"APPROVED".equals(applicationStatus)) {
				log.warn("Application status is not APPROVED after payment: {} for application: {}. Will force to APPROVED.", applicationStatus, applicationNumber);
			} else {
				log.info("Application status is APPROVED after payment for application: {}", applicationNumber);
			}
			
			if (applications == null || applications.isEmpty()) {
				log.error("No application found for application number: {}", applicationNumber);
				return;
			}

			AllotmentDetails application = applications.get(0);
			
			// For both new and renewal applications, ALWAYS set status to APPROVED after payment completion
			// This ensures consistency regardless of what workflow returns (same logic as new applications)
			String applicationType = application.getApplicationType();
			boolean isRenewal = applicationType != null && RLConstants.RENEWAL_RL_APPLICATION.equals(applicationType);
			
			// Always use APPROVED status after payment completion for both new and renewal applications
			// Workflow should return APPROVED, but we enforce it to ensure consistency
			if (!"APPROVED".equals(applicationStatus)) {
				log.warn("Workflow returned status '{}' instead of APPROVED for application: {} (type: {}). Forcing to APPROVED.", 
						applicationStatus, applicationNumber, applicationType);
			}
			applicationStatus = "APPROVED";
			application.setStatus(RLConstants.PENDING_FOR_PAYMENT_RL_APPLICATION);
			
			log.info("Updating application status to APPROVED for application: {} (type: {}, isRenewal: {}, previous workflow status: {})", 
					applicationNumber, applicationType, isRenewal, workflowState.getApplicationStatus() != null ? workflowState.getApplicationStatus() : workflowState.getState());
			if(status) {
				application.setStatus(RLConstants.PAY);
			// Always generate/update petRegistrationNumber and update status for both new and renewal applications after payment
				updateDatabaseWithStatus(application,applicationNumber, applicationStatus, paymentRequest.getRequestInfo());
			}else {
				applicationStatus = "CLOSED";
				updateDatabaseWithStatus(application,applicationNumber, applicationStatus, paymentRequest.getRequestInfo());	
			}
		} catch (Exception e) {
			log.error("Error processing payment for application: {}, Error: {}", applicationNumber, e.getMessage(), e);
		}
	}
	
	/**
	 * Transitions workflow to APPROVED state by calling workflow service with PAY action
	 * Returns the state from workflow response
	 */
	private State transitionWorkflowToApproved(PaymentRequest paymentRequest, String action) {
		try {
			ProcessInstance processInstance = getProcessInstanceForRL(paymentRequest,action);
			if (processInstance == null) {
				log.error("ProcessInstance is null, cannot transition workflow");
				return null;
			}
			
			Role role = Role.builder().code("SYSTEM").tenantId(paymentRequest.getPayment().getTenantId()).build();
			paymentRequest.getRequestInfo().getUserInfo().getRoles().add(role);
			
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(
					paymentRequest.getRequestInfo(),
					Collections.singletonList(processInstance));
			
			State state = callWorkFlow(workflowRequest);
			if (state == null) {
				log.error("Workflow transition returned null state");
			}			
			return state;
			
		} catch (Exception e) {
			    log.error("Error transitioning workflow to APPROVED: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Constructs a ProcessInstance object from the payment request.
	 * Performs null checks to prevent NullPointerExceptions.
	 */
	private ProcessInstance getProcessInstanceForRL(PaymentRequest paymentRequest,String action) {
		if (paymentRequest == null || paymentRequest.getPayment() == null ||
				paymentRequest.getPayment().getPaymentDetails() == null ||
				paymentRequest.getPayment().getPaymentDetails().isEmpty() ||
				paymentRequest.getPayment().getPaymentDetails().get(0).getBill() == null) {
			log.error("Missing required data to build ProcessInstance from paymentRequest: {}", paymentRequest);
			return null;
		}

		String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
		String tenantId = paymentRequest.getPayment().getTenantId();

		if (consumerCode == null || consumerCode.isEmpty() || tenantId == null || tenantId.isEmpty()) {
			log.error("Consumer code or tenant id is missing or empty");
			return null;
		}

		// Determine the correct workflow based on application type
		String workflowName = getWorkflowNameForApplication(consumerCode, tenantId);

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(consumerCode);
		processInstance.setAction(action);
		processInstance.setModuleName(RLConstants.RL_SERVICE_NAME);
		processInstance.setTenantId(tenantId);
		processInstance.setBusinessService(workflowName);
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;
	}

	/**
	 * Determines the workflow name based on additionalDetails.applicationType.
	 * If applicationType is "Legacy", returns RENT_AND_LEASE_LG workflow.
	 * Otherwise, returns the default RENT_N_LEASE_NEW workflow.
	 *
	 * @param consumerCode The application number
	 * @param tenantId The tenant ID
	 * @return The appropriate workflow name
	 */
	private String getWorkflowNameForApplication(String consumerCode, String tenantId) {
		try {
			AllotmentCriteria criteria = AllotmentCriteria.builder()
					.applicationNumbers(Collections.singleton(consumerCode))
					.tenantId(tenantId)
					.build();
			List<AllotmentDetails> applications = allotmentRepository.getAllotmentSearch(criteria);
			
			if (applications != null && !applications.isEmpty()) {
				AllotmentDetails application = applications.get(0);

				if (RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(application.getApplicationType())) {
					log.info("Using Legacy workflow from root applicationType for payment of application: {}", consumerCode);
					return RLConstants.RL_WORKFLOW_NAME_LEGACY;
				}
				
				// Only use root-level applicationType to determine workflow; do not consult additionalDetails
			}
		} catch (Exception e) {
			log.error("Error determining workflow name for application: {}, using default workflow. Error: {}", 
					consumerCode, e.getMessage());
		}
		return RLConstants.RL_WORKFLOW_NAME;
	}

	/**
	 * Calls the workflow service with the given request and returns the state.
	 * Handles null/empty responses safely.
	 */
	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
		State state = null;
		try {
			StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
			Object object = serviceRequestRepository.fetchResult(url, workflowReq).get();
			if (object == null) {
				log.error("No response from workflow service");
				return null;
			}

			ProcessInstanceResponse response = mapper.convertValue(object, ProcessInstanceResponse.class);
			if (response == null || response.getProcessInstances() == null || response.getProcessInstances().isEmpty()) {
				log.error("Empty response from workflow service");
				return null;
			}

			state = response.getProcessInstances().get(0).getState();
		} catch (Exception e) {
			log.error("Exception while calling workflow service: {}", e.getMessage(), e);
		}
		return state;
	}
	
	/**
	 * Fetches the current process instance from workflow service
	 */
	private ProcessInstance fetchProcessInstanceFromWorkflow(String businessId, String tenantId, RequestInfo requestInfo) {
		try {
			StringBuilder url = new StringBuilder(configs.getWfHost());
			url.append(configs.getWfProcessInstanceSearchPath());
			url.append("?tenantId=").append(tenantId);
			url.append("&businessIds=").append(businessId);
			
			org.egov.rl.services.web.contracts.RequestInfoWrapper requestInfoWrapper = 
					org.egov.rl.services.web.contracts.RequestInfoWrapper.builder()
					.requestInfo(requestInfo)
					.build();
			
			Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper).get();
			if (result == null) {
				log.error("No response from workflow service when fetching process instance");
				return null;
			}
			
			ProcessInstanceResponse response = mapper.convertValue(result, ProcessInstanceResponse.class);
			if (response == null || response.getProcessInstances() == null || response.getProcessInstances().isEmpty()) {
				log.error("Empty response from workflow service");
				return null;
			}
			
			ProcessInstance wfProcessInstance = response.getProcessInstances().get(0);
			
			ProcessInstance processInstance = new ProcessInstance();
			processInstance.setBusinessId(wfProcessInstance.getBusinessId());
			processInstance.setTenantId(wfProcessInstance.getTenantId());
			processInstance.setBusinessService(wfProcessInstance.getBusinessService());
			processInstance.setState(wfProcessInstance.getState());
			
			return processInstance;
			
		} catch (Exception e) {
			log.error("Error fetching process instance from workflow service: {}", e.getMessage(), e);
			return null;
		}
	}


	/**
	 * Updates database with status and petRegistrationNumber
	 */
	private void updateDatabaseWithStatus(AllotmentDetails allotmentDetails,String applicationNumber, String status, RequestInfo requestInfo) {
		try {
			String updateQuery;
			Object[] params;
			long currentTime = System.currentTimeMillis();
			
			if (applicationNumber != null && !applicationNumber.isEmpty()) {
				updateQuery = "UPDATE eg_rl_allotment SET status = ?, lastmodified_time=?, lastmodified_by=? WHERE application_number = ?";
				params = new Object[]{
					status,
					currentTime,
					requestInfo.getUserInfo().getUuid(),
					applicationNumber
				};
			} else {
				updateQuery = "UPDATE eg_rl_allotment SET status = ?, lastmodified_time=?, lastmodified_by=? WHERE application_number = ?";
				params = new Object[]{
					status,
					currentTime,
					requestInfo.getUserInfo().getUuid(),
					applicationNumber
				};
			}
			
			    log.info("Executing database update for application: {} with status: {}, RL: {}", 
					status, applicationNumber != null ? applicationNumber : "null");
			
			int rowsUpdated = allotmentRepository.getJdbcTemplate().update(updateQuery, params);
			
			if (rowsUpdated > 0) {
				log.info("Successfully updated application - ApplicationNumber: {}, Status: {} , Rows updated: {}", 
						applicationNumber, status, applicationNumber != null ? applicationNumber : "null", rowsUpdated);
				List<AllotmentDetails> allotmentlist=new ArrayList<>();
				allotmentlist.add(allotmentDetails);
				log.info("Sending Notification"); 
				notificationService.process(AllotmentRequest.builder().allotment(allotmentlist).requestInfo(requestInfo).build());
			} else {
				log.error("FAILED to update database - No rows updated for application: {}. Query: {}, Params: status={}, lastModifiedBy={}, lastModifiedTime={}, applicationNumber={}", 
						applicationNumber, updateQuery, status, requestInfo.getUserInfo().getUuid(), currentTime, applicationNumber);
			}
			
		} catch (Exception e) {
			    log.error("Failed to update database for application: {}, Error: {}", 
					applicationNumber, e.getMessage(), e);
		}
	}
}
