package org.egov.pqm.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.pqm.repository.ServiceRequestRepository;
import org.egov.pqm.util.ErrorConstants;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.RequestInfoWrapper;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

	private ServiceConfiguration config;

	private ServiceRequestRepository serviceRequestRepository;

	private ObjectMapper mapper;

	@Autowired
	public WorkflowService(ServiceConfiguration config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	/**
	 * Get the workflow config for the given tenant
	 * 
	 * @param test         The PQM Object
	 * @param testRequest The RequestInfo object of the request
	 * @return BusinessService for the given tenantId
	 */
	public BusinessService getBusinessService(Test test, TestRequest testRequest, String businessServiceName, String id) {
		StringBuilder url = getSearchURLWithParams(test.getTenantId(), businessServiceName, id);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(testRequest.getRequestInfo()).build();
		Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response = null;
		try {
			response = mapper.convertValue(result, BusinessServiceResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorConstants.PARSING_ERROR, "Failed to parse response of Workflow");
		}
		return response.getBusinessServices().get(0);
	}

	/**
	 * Get the ProcessInstance for the given Application
	 * 
	 * @param test      The PQM Object
	 * @param testRequest The RequestInfo object of the request
	 * 
	 */
	public ProcessInstance getProcessInstance(Test test,String businessServiceName, TestRequest testRequest) {
		StringBuilder url = getSearchURLWithParams(test.getTenantId(), null, test.getTestId());
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(testRequest.getRequestInfo()).build();
		Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		ProcessInstanceResponse response = null;
		try {
			response = mapper.convertValue(result, ProcessInstanceResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorConstants.PARSING_ERROR, "Failed to parse response of Workflow");
		}
		if (!response.getProcessInstances().isEmpty())
			return response.getProcessInstances().get(0);
		else return null;
	}

	/**
	 * Creates url for search based on given tenantId
	 *
	 * @param tenantId The tenantId for which url is generated
	 * @return The search url
	 */
	private StringBuilder getSearchURLWithParams(String tenantId, String businessService, String id) {
		StringBuilder url = new StringBuilder(config.getWfHost());

		if (businessService != null) {
			url.append(config.getWfBusinessServiceSearchPath());
			url.append("?businessServices=");
			url.append(businessService);
		} else {
			url.append(config.getWfProcessPath());
			url.append("?businessIds=");
			url.append(id);
		}

		url.append("&tenantId=");
		url.append(tenantId);

		return url;
	}

	/**
	 * Returns boolean value to specifying if the state is updatable
	 * 
//	 * @param status      The stateCode of the pqm
	 * @param businessService The BusinessService of the application flow
	 * @return State object to be fetched
	 */
	public Boolean isStateUpdatable(String status, BusinessService businessService) {
		for (org.egov.pqm.web.model.workflow.State state : businessService.getStates()) {
			if (state.getApplicationStatus() != null && state.getApplicationStatus().equalsIgnoreCase(status))
				return state.getIsStateUpdatable();
		}
		return Boolean.FALSE;
	}

	/**
	 * Returns State Obj fo the current state of the document
	 * 
//	 * @param status      The stateCode of the pqm
	 * @param businessService The BusinessService of the application flow
	 * @return State object to be fetched
	 */
	public State getCurrentStateObj(String status, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getApplicationStatus() != null && state.getState().equalsIgnoreCase(status))
				return state;
		}
		return null;
	}
}
