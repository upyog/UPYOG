package org.egov.asset.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.CreationReason;
import org.egov.asset.web.models.workflow.BusinessService;
import org.egov.asset.web.models.workflow.BusinessServiceResponse;
import org.egov.asset.web.models.workflow.ProcessInstance;
import org.egov.asset.web.models.workflow.ProcessInstanceRequest;
import org.egov.asset.web.models.workflow.ProcessInstanceResponse;
import org.egov.asset.web.models.workflow.State;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;

@Service
public class WorkflowService {

	@Autowired
	private AssetConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;


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
		Object responseObject = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}
	
	/**
	 * method to prepare process instance request 
	 * and assign status back to property
	 * 
	 * @param request
	 */
	public State updateWorkflow(AssetRequest assetRequest, CreationReason creationReasonForWorkflow) {

		Asset asset = assetRequest.getAsset();
		
		ProcessInstanceRequest workflowReq = getProcessInstanceForAsset(asset, assetRequest.getRequestInfo());
		State state = callWorkFlow(workflowReq);
		
//		if (state.getApplicationStatus().equalsIgnoreCase(configs.getWfStatusActive()) && property.getPropertyId() == null) {
//			
//			String pId = utils.getIdList(request.getRequestInfo(), property.getTenantId(), configs.getPropertyIdGenName(), configs.getPropertyIdGenFormat(), 1).get(0);
//			request.getProperty().setPropertyId(pId);
//		}
//		
//		if(request.getProperty().getCreationReason().equals(CreationReason.STATUS) && request.getProperty().getWorkflow().getAction().equalsIgnoreCase("APPROVE"))
//		{	
//			request.getProperty().setStatus(Status.INACTIVE);
//		}
//		else
//		request.getProperty().setStatus(Status.fromValue(state.getApplicationStatus()));
//		request.getProperty().getWorkflow().setState(state);
		assetRequest.getAsset().setStatus(state.getApplicationStatus());
		return state;
	}

	private ProcessInstanceRequest getProcessInstanceForAsset(Asset asset, RequestInfo requestInfo) {
//		Workflow workflow = application.getWorkflow();	
//		Asset asset = request.getProperty();
		ProcessInstance workflow = null != asset.getWorkflow() ? asset.getWorkflow() : new ProcessInstance();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(asset.getApplicationNo());
		processInstance.setAction(workflow.getAction());
		processInstance.setModuleName(workflow.getModuleName());
		processInstance.setTenantId(asset.getTenantId());
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
		Object responseObject = restRepo.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response = null;
		try {
			response = mapper.convertValue(responseObject, BusinessServiceResponse.class);
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
		//url.append(configs.getWfProcessInstanceSearchPath());
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

		Object responseObject = restRepo.fetchResult(url, requestInfoWrapper);
		ProcessInstanceResponse response = null;

		try {
			response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);
		} catch (Exception e) {
			throw new CustomException("PARSING_ERROR", "Failed to parse workflow search response");
		}

		if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances())
				&& response.getProcessInstances().get(0) != null)
			return response.getProcessInstances().get(0).getState();

		return null;
	}

}
