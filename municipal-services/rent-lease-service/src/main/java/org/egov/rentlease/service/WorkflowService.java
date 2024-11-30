package org.egov.rentlease.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rentlease.model.BusinessService;
import org.egov.rentlease.model.BusinessServiceResponse;
import org.egov.rentlease.model.ProcessInstance;
import org.egov.rentlease.model.ProcessInstanceRequest;
import org.egov.rentlease.model.ProcessInstanceResponse;
import org.egov.rentlease.model.RentLease;
import org.egov.rentlease.model.RentLeaseCreationRequest;
import org.egov.rentlease.model.State;
import org.egov.rentlease.repository.RestCallRepository;
import org.egov.rentlease.util.RentConstants;
import org.egov.rentlease.util.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class WorkflowService {

	@Autowired
	private RentConstants constants;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestCallRepository restCallRepository;

	public void updateWorkflowStatus(RentLeaseCreationRequest request) {
		
		List<ProcessInstance> processInstances = new ArrayList<>();
		request.getRentLease().forEach(booking ->{
			ProcessInstance processInstance = getProcessInstanceForRentLease(booking,
					request.getRequestInfo());
			processInstances.add(processInstance);
		});
		
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(request.getRequestInfo(),processInstances);
		callWorkFlow(workflowRequest);
	}

	

	public State callWorkFlow(ProcessInstanceRequest workflowRequest) {
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(constants.getWorkflowHost().concat(constants.getWorkflowEndpointTransition()));
		Object optional = restCallRepository.fetchResult(url, workflowRequest);
		response = mapper.convertValue(optional, ProcessInstanceResponse.class);
		if(null == response) {
			throw new CustomException("WORKFLOW_SERVICE_CALL_FAILED","Failed to run Workflow Service.");
		}
		return response.getProcessInstances().get(0).getState();		
	}



	private ProcessInstance getProcessInstanceForRentLease(RentLease booking, RequestInfo requestInfo) {
		
		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(booking.getApplicationNo());
		processInstance.setAction(booking.getWorkflowAction());
		processInstance.setModuleName(constants.RENT_LEASE_CONSTANT);
		processInstance.setTenantId(constants.STATE_LEVEL_TEENENT_ID);
		processInstance.setBusinessService(constants.RENT_LEASE_CONSTANT);
		processInstance.setComment(booking.getComments());
		return processInstance;
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
		Object result = restCallRepository.fetchResult(url, requestInfoWrapper);
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

	private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

		StringBuilder url = new StringBuilder(constants.getWorkflowHost());
		url.append(constants.getWorkflowBusinessServiceSearchPath());
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

	
	 
	 private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {

		StringBuilder url = new StringBuilder(constants.getWorkflowHost());
		url.append(constants.getWorkflowBusinessServiceSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessIds=");
		url.append(businessId);
		return url;
	}
	 
	 public State getCurrentState(RequestInfo requestInfo, String tenantId, String businessId) {

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);

			Object res = restCallRepository.fetchResult(url, requestInfoWrapper);
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
	 
	 public BusinessServiceResponse businessServiceSearch(RentLeaseCreationRequest rentAccountActionRequest,
				String applicationTenantId, String applicationBusinessId) {
			StringBuilder uri = new StringBuilder(constants.getWorkflowHost());
			uri.append(constants.getWorkflowBusinessServiceSearchPath());
			uri.append("?tenantId=").append(applicationTenantId);
			uri.append("&businessServices=").append(applicationBusinessId);
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
					.requestInfo(rentAccountActionRequest.getRequestInfo()).build();
			LinkedHashMap<String, Object> responseObject = (LinkedHashMap<String, Object>) restCallRepository.fetchResult(uri, requestInfoWrapper);
			BusinessServiceResponse businessServiceResponse = mapper.convertValue(responseObject
																					, BusinessServiceResponse.class);
			return businessServiceResponse;
		}

}
