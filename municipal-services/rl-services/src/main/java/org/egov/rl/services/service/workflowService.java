package org.egov.rl.services.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.services.config.RentLeaseConfiguration;
import org.egov.rl.services.models.AllotmentDetails;
import org.egov.rl.services.models.AllotmentRequest;
import org.egov.rl.services.models.ProcessInstance;
import org.egov.rl.services.models.user.User;
import org.egov.rl.services.models.workflow.BusinessService;
import org.egov.rl.services.models.workflow.BusinessServiceResponse;
import org.egov.rl.services.models.workflow.ProcessInstanceRequest;
import org.egov.rl.services.models.workflow.ProcessInstanceResponse;
import org.egov.rl.services.models.workflow.State;
import org.egov.rl.services.models.workflow.Workflow;
import org.egov.rl.services.util.RLConstants;
import org.egov.rl.services.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class workflowService {

	@Autowired
	private RentLeaseConfiguration configs;

	@Autowired
	private org.egov.rl.services.repository.ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	org.egov.rl.services.repository.ServiceRequestRepository serviceRequestRepository;

	public void updateWorkflowStatus(AllotmentRequest allotmentRequest) {
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
			ProcessInstance processInstance = getProcessInstanceForAllotment(allotmentDetails,
					allotmentRequest.getRequestInfo());
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(allotmentRequest.getRequestInfo(),
					Collections.singletonList(processInstance));
			State state = callWorkFlow(workflowRequest);
			allotmentDetails.setStatus(state.getApplicationStatus());			
	}

	private ProcessInstance getProcessInstanceForAllotment(AllotmentDetails application, RequestInfo requestInfo) {
		Workflow workflow = application.getWorkflow();		
		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(application.getApplicationNumber());
		processInstance.setAction(workflow.getAction());
        processInstance.setModuleName(RLConstants.RL_SERVICE_NAME);
        // Check if applicationType is Legacy in additionalDetails if legacy then RENT_AND_LEASE_LEGACY otherwise RENT_AND_LEASE_NEW
		String workflowName = getWorkflowName(application); // Only use root-level applicationType to determine workflow; do not consult additionalDetails
        processInstance.setBusinessService(workflowName);
		processInstance.setTenantId(application.getTenantId());
		processInstance.setDocuments(workflow.getDocuments());
		processInstance.setComment(workflow.getComments());

		if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
			List<User> users = new ArrayList<>();

			workflow.getAssignes().forEach(uuid -> {
				User user = new User();
				user.setUuid(uuid);
				users.add(user);
			});

			processInstance.setAssignes(users);
		}

		return processInstance;

	}

    /**
     * Determines the workflow name based on additionalDetails.
     * If applicationType is "Legacy", returns RENT_AND_LEASE_LG workflow.
     * Otherwise, returns the default RENT_N_LEASE_NEW workflow.
     *
     * @param application The AllotmentDetails containing additionalDetails
     * @return The appropriate workflow name
     */
    private String getWorkflowName(AllotmentDetails application) {
		String rootType = application.getApplicationType();
		if (RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(rootType)) {
			return RLConstants.RL_WORKFLOW_NAME_LEGACY;
		}

		// Removed additionalDetails check for applicationType
        return RLConstants.RL_WORKFLOW_NAME;
    }



    /**
	 * Method to integrate with workflow
	 *
	 * takes the Pet request as parameter constructs the work-flow request
	 *
	 * and sets the resultant status from wf-response back to trade-license object
	 *
	 */
	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
	       
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		Object object = serviceRequestRepository.fetchResult(url, workflowReq).get();
		
		response = mapper.convertValue(object, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}

	/**
	 * Get the workflow config for the given tenant
	 * 
	 * @param tenantId    The tenantId for which businessService is requested
	 * @param requestInfo The RequestInfo object of the request
	 * @return BusinessService for the the given tenantId
	 */
	public BusinessService getBusinessService(String tenantId, String businessService, RequestInfo requestInfo) {

		StringBuilder url = getSearchURLWithParams(tenantId, businessService);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		System.out.println(url);
		Object result = restRepo.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response = null;
		try {
			response = mapper.convertValue(result, BusinessServiceResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
		}

		if (CollectionUtils.isEmpty(response.getBusinessServices()))
			throw new CustomException("BUSINESSSERVICE_NOT_FOUND",
					"The businessService " + businessService + " is not found");

		return response.getBusinessServices().get(0);
	}

	/**
	 * Creates url for search based on given tenantId
	 *
	 * @param tenantId The tenantId for which url is generated
	 * @return The search url
	 */
	private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append(configs.getWfBusinessServiceSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessServices=");
		url.append(businessService);
		return url;
	}

	/**
	 * Returns boolean value to specifying if the state is updatable
	 * 
	 * @param stateCode       The stateCode of the license
	 * @param businessService The BusinessService of the application flow
	 * @return State object to be fetched
	 */
	public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode))
				return state.getIsStateUpdatable();
		}
		return null;
	}

	/**
	 * Creates url for searching processInstance
	 *
	 * @return The search url
	 */
	private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {

		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append(configs.getWfProcessInstanceSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessIds=");
		url.append(businessId);
		return url;
	}

	/**
	 * Fetches the workflow object for the given assessment
	 * 
	 * @return
	 */
	public State getCurrentState(RequestInfo requestInfo, String tenantId, String businessId) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);

		Object res = restRepo.fetchResult(url, requestInfoWrapper);
		ProcessInstanceResponse response = null;

		try {
			response = mapper.convertValue(res, ProcessInstanceResponse.class);
		} catch (Exception e) {
			throw new CustomException("PARSING_ERROR", "Failed to parse workflow search response");
		}

		if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances())
				&& response.getProcessInstances().get(0) != null)
			return response.getProcessInstances().get(0).getState();

		return null;
	}

}
