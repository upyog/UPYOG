package org.upyog.sv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.repository.ServiceRequestRepository;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.Workflow;
import org.upyog.sv.web.models.common.RequestInfoWrapper;
import org.upyog.sv.web.models.common.User;
import org.upyog.sv.web.models.workflow.BusinessService;
import org.upyog.sv.web.models.workflow.BusinessServiceResponse;
import org.upyog.sv.web.models.workflow.ProcessInstance;
import org.upyog.sv.web.models.workflow.ProcessInstanceRequest;
import org.upyog.sv.web.models.workflow.ProcessInstanceResponse;
import org.upyog.sv.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkflowService {

	@Autowired
	private StreetVendingConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	public void updateWorkflowStatus(StreetVendingRequest streetVendingRequest) {
		
			ProcessInstance processInstance = getProcessInstanceForSV(streetVendingRequest.getStreetVendingDetail(),
					streetVendingRequest.getRequestInfo());
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(streetVendingRequest.getRequestInfo(),
					Collections.singletonList(processInstance));
			callWorkFlow(workflowRequest);
		
	}

	private ProcessInstance getProcessInstanceForSV(StreetVendingDetail application, RequestInfo requestInfo) {
		Workflow workflow = application.getWorkflow();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(application.getApplicationNo());
		processInstance.setAction(workflow.getAction());
		processInstance.setModuleName(workflow.getModuleName());
		processInstance.setTenantId(application.getTenantId());
		processInstance.setBusinessService(workflow.getBusinessService());
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
		Object optional = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(optional, ProcessInstanceResponse.class);
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