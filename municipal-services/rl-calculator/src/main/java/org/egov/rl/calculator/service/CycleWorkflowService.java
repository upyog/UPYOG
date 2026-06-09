package org.egov.rl.calculator.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.rl.calculator.repository.DemandRepository;
import org.egov.rl.calculator.repository.Repository;
import org.egov.rl.calculator.repository.ServiceRequestRepository;
import org.egov.rl.calculator.util.Configurations;
import org.egov.rl.calculator.util.PropertyUtil;
import org.egov.rl.calculator.util.RLConstants;
import org.egov.rl.calculator.web.models.AllotmentCriteria;
import org.egov.rl.calculator.web.models.AllotmentDetails;
import org.egov.rl.calculator.web.models.AllotmentRequest;
import org.egov.rl.calculator.web.models.AllotmentSearchResponse;
import org.egov.rl.calculator.web.models.property.RequestInfoWrapper;
import org.egov.rl.calculator.web.models.workflow.ProcessInstance;
import org.egov.rl.calculator.web.models.workflow.ProcessInstanceRequest;
import org.egov.rl.calculator.web.models.workflow.ProcessInstanceResponse;
import org.egov.rl.calculator.web.models.workflow.State;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CycleWorkflowService {

	
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private Configurations configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private Configurations config;

	public void process(String tenantId, RequestInfo requestInfo,String consumerCode){
		try {		
			System.out.println(tenantId+"---allotmentDetails:--"+consumerCode);
			List<AllotmentDetails> allotmentDetails = getAllotmentDetailsByApplicationNumber(tenantId,requestInfo,consumerCode);
			allotmentDetails=allotmentDetails.stream().map(d->{
				AllotmentDetails d1=d;
				d1.setStatus("CYCLE_Bill_GENERATED");
				return d1;
			}).collect(Collectors.toList());
			System.out.println("allotmentDetails:--"+allotmentDetails);
			processPaymentAndUpdateApplication(AllotmentRequest.builder().allotment(allotmentDetails).requestInfo(requestInfo).build());		
			
		} catch (Exception e) {
			log.error("Error processing Payment Cycle: {}", e.getMessage(), e);
		}
	}

	/**
	 * Main method to process payment and update application status
	 * 1. Calls workflow with PAY action to transition to APPROVED
	 * 2. Fetches updated process instance to get the current status
	 * 3. Generates petRegistrationNumber if needed
	 * 4. Updates database with the status from workflow and petRegistrationNumber
	 */
	private void processPaymentAndUpdateApplication(AllotmentRequest allotmentRequest) {
		String applicationNumber = null;
		String tenantId = null;
		
		try {
			AllotmentDetails allotmentDetails=allotmentRequest.getAllotment().get(0);
			applicationNumber=allotmentDetails.getApplicationNumber();
			tenantId=allotmentDetails.getTenantId();
			
			
		 // Transition workflow with PAY action
			State workflowState = transitionWorkflowToApproved(allotmentRequest,"CYCLE_Bill_GENERATED");
			
			if (workflowState == null) {
				log.error("Workflow transition failed for application: {}", applicationNumber);
				return;
			}
			
			// Fetch the updated process instance to get the current state
			ProcessInstance updatedProcessInstance = fetchProcessInstanceFromWorkflow(applicationNumber, tenantId, allotmentRequest.getRequestInfo());
			if (updatedProcessInstance != null && updatedProcessInstance.getState() != null) {
				workflowState = updatedProcessInstance.getState();
			} else {
				log.warn("Could not fetch updated process instance for application: {}, using state from workflow transition", applicationNumber);
			}
			if(workflowState!=null) {
				updateDatabaseWithStatus(allotmentRequest);				
			}
				
		} catch (Exception e) {
			log.error("Error processing payment for application: {}, Error: {}", applicationNumber, e.getMessage(), e);
		}
	}
	
	/**
	 * Transitions workflow to APPROVED state by calling workflow service with PAY action
	 * Returns the state from workflow response
	 */
	private State transitionWorkflowToApproved(AllotmentRequest allotmentRequest, String action) {
		try {
			ProcessInstance processInstance = getProcessInstance(allotmentRequest,action);
			if (processInstance == null) {
				log.error("ProcessInstance is null, cannot transition workflow");
				return null;
			}
			
			Role role = 
					Role.builder().code("SYSTEM")
					.tenantId(allotmentRequest.getAllotment().get(0).getTenantId()).build();
			
			allotmentRequest.getRequestInfo().getUserInfo().getRoles().add(role);
			
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(
					allotmentRequest.getRequestInfo(),
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
	private ProcessInstance getProcessInstance(AllotmentRequest allotmentRequest,String action) {
		

		String consumerCode = allotmentRequest.getAllotment().get(0).getApplicationNumber();
		String tenantId = allotmentRequest.getAllotment().get(0).getTenantId();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(consumerCode);
		processInstance.setAction(action);
		processInstance.setModuleName(RLConstants.RL_SERVICE_NAME);
		processInstance.setTenantId(tenantId);
		
		// Determine correct workflow based on application type
		String workflowName = getWorkflowName(allotmentRequest.getAllotment().get(0));
		processInstance.setBusinessService(workflowName);
		
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;
	}

	/**
	 * Determines the workflow name based on applicationType.
	 * If applicationType is "Legacy", returns RENT_AND_LEASE_LG workflow.
	 * Otherwise, returns the default RENT_N_LEASE_NEW workflow.
	 *
	 * @param allotmentDetails The AllotmentDetails containing applicationType
	 * @return The appropriate workflow name
	 */
	private String getWorkflowName(AllotmentDetails allotmentDetails) {
		String applicationType = allotmentDetails.getApplicationType();
		if (RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(applicationType)) {
			log.info("Using Legacy workflow for application: {}", allotmentDetails.getApplicationNumber());
			return RLConstants.RL_WORKFLOW_NAME_LEGACY;
		}
		// Do not check additionalDetails.applicationType; only root applicationType is considered
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
			
			RequestInfoWrapper requestInfoWrapper = 
					RequestInfoWrapper.builder()
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
	private void updateDatabaseWithStatus(AllotmentRequest allotmentRequest) {
		AllotmentDetails allotmentDetails=allotmentRequest.getAllotment().get(0);
		String applicationNumber=allotmentDetails.getApplicationNumber();
		String status="PENDING_FOR_PAYMENT";
		RequestInfo requestInfo=allotmentRequest.getRequestInfo();
		
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
			
		    int rowsUpdated = serviceRequestRepository.getJdbcTemplate().update(updateQuery, params);

			if (rowsUpdated > 0) {
				log.info("Successfully updated application - ApplicationNumber: {}, Status: {} , Rows updated: {}", 
						applicationNumber, status, applicationNumber != null ? applicationNumber : "null", rowsUpdated);
				List<AllotmentDetails> allotmentlist=new ArrayList<>();
				allotmentlist.add(allotmentDetails);
				log.info("Sending Notification"); 
//				notificationService.process(AllotmentRequest.builder().allotment(allotmentlist).requestInfo(requestInfo).build());
			} else {
				log.error("FAILED to update database - No rows updated for application: {}. Query: {}, Params: status={}, lastModifiedBy={}, lastModifiedTime={}, applicationNumber={}", 
						applicationNumber, updateQuery, status, requestInfo.getUserInfo().getUuid(), currentTime, applicationNumber);
			}
			
		} catch (Exception e) {
			    log.error("Failed to update database for application: {}, Error: {}", 
					applicationNumber, e.getMessage(), e);
		}
	}
	
	private List<AllotmentDetails> getAllotmentDetailsByApplicationNumber(String tenantId, RequestInfo requestInfo,
			String consumerCode) {
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			String baseHost = config.getRlServiceHost();
			String basePath = config.getRlSearchEndpoint();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseHost).path(basePath)
					.queryParam("tenantId",tenantId).queryParam("applicationNumbers", consumerCode);

			String url = builder.build().toUriString();
			log.info("ALLOTMENT SEARCH URI1 :" + url);
			try {
				Object result = serviceRequestRepository.fetchResult(new StringBuilder(url), requestInfoWrapper).get();
				AllotmentSearchResponse response = mapper.convertValue(result, AllotmentSearchResponse.class);
				return response.getAllotment();
			} catch (Exception e) {
				log.error("Error while fetching approved allotment applications for tenant: {}", tenantId, e);
				throw new CustomException("RL_APP_SEARCH_ERROR", "Failed to fetch approved allotment applications");
			}
	  }
}
