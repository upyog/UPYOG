package org.egov.echallan.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.repository.ServiceRequestRepository;
import org.egov.echallan.web.models.workflow.BusinessService;
import org.egov.echallan.web.models.workflow.BusinessServiceResponse;
import org.egov.echallan.web.models.workflow.ProcessInstance;
import org.egov.echallan.web.models.workflow.ProcessInstanceRequest;
import org.egov.echallan.web.models.workflow.ProcessInstanceResponse;
import org.egov.echallan.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class WorkflowService {

	@Autowired
	private ChallanConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;


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
		Optional<Object> responseObject = restRepo.fetchResultV1(url, workflowReq);
		response = mapper.convertValue(responseObject.get(), ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}
	
	
	/**
	 * method to prepare process instance request 
	 * and assign status back to property
	 * 
	 * @param request
	 */
	public State updateWorkflow(ChallanRequest request) {


		
		ProcessInstanceRequest workflowReq = getProcessInstanceForHallBooking(request, request.getRequestInfo());
		
		State state = callWorkFlow(workflowReq);
		
		request.getChallan().setStatus(state.getApplicationStatus());
		
	    return state;
	}
	
	
	private ProcessInstanceRequest getProcessInstanceForHallBooking(ChallanRequest request, RequestInfo requestInfo) {
//		Workflow workflow = application.getWorkflow();	
//		Asset asset = request.getProperty();
		ProcessInstance workflow = null != request.getChallan().getWorkflow() ? request.getChallan().getWorkflow() : new ProcessInstance();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(request.getChallan().getChallanNo());
		processInstance.setAction(workflow.getAction());
		processInstance.setModuleName(workflow.getModuleName());
		processInstance.setTenantId(request.getChallan().getTenantId());//
		processInstance.setBusinessService(workflow.getBusinessService());
		processInstance.setDocuments(workflow.getDocuments());
		processInstance.setComment(workflow.getComment());

		if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
			List<User> users = new ArrayList<>();

			workflow.getAssignes().forEach(uuid -> {
				User user = new User();
				user.setUuid(uuid.getUuid());
				users.add(user);
			});

			processInstance.setAssignes(users);
		}

		//return processInstance;
		return ProcessInstanceRequest.builder()
				.requestInfo(requestInfo)
				.processInstances(Arrays.asList(processInstance))
				.build();

//
	}
	
	/**
	 * method to prepare process instance request 
	 * and assign status back to property
	 * 
	 * @param request
	 */



	/**
	 * Get the workflow config for the given tenant
	 * 
	 * @param tenantId    The tenantId for which businessService is requested
	 * @param requestInfo The RequestInfo object of the request
	 * @return BusinessService for the the given tenantId
	 */
	
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
		//url.append(configs.getWfProcessInstanceSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessIds=");
		url.append(businessId);
		return url;
	}

}