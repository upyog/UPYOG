package org.upyog.chb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.workflow.BusinessService;
import org.upyog.chb.web.models.workflow.BusinessServiceResponse;
import org.upyog.chb.web.models.workflow.ProcessInstance;
import org.upyog.chb.web.models.workflow.ProcessInstanceRequest;
import org.upyog.chb.web.models.workflow.ProcessInstanceResponse;
import org.upyog.chb.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;

/**
 * This service class handles workflow-related operations for the Community Hall Booking module.
 * 
 * Purpose:
 * - To manage the lifecycle of bookings by interacting with the workflow service.
 * - To ensure that bookings adhere to the defined workflow states and transitions.
 * 
 * Dependencies:
 * - CommunityHallBookingConfiguration: Provides configuration properties for workflow operations.
 * - ServiceRequestRepository: Sends HTTP requests to the workflow service.
 * - ObjectMapper: Serializes and deserializes JSON objects for requests and responses.
 * 
 * Features:
 * - Initiates workflow instances for new bookings.
 * - Updates workflow instances based on booking status changes.
 * - Fetches and validates workflow configurations for the booking module.
 * - Logs workflow operations and errors for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. initiateWorkflow:
 *    - Sends a request to the workflow service to initiate a new workflow instance.
 *    - Populates workflow-related fields in the booking request.
 * 
 * 2. updateWorkflow:
 *    - Updates an existing workflow instance based on booking status changes.
 *    - Ensures that the workflow state is consistent with the booking state.
 * 
 * 3. getBusinessService:
 *    - Fetches the workflow configuration (BusinessService) for the booking module.
 *    - Validates the workflow states and actions for the module.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever workflow-related
 *   operations are required.
 * - It ensures consistent and reusable logic for managing workflows in the module.
 */
@Service
public class WorkflowService {

	@Autowired
	private CommunityHallBookingConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	public State callWorkFlow(ProcessInstanceRequest workflowReq) {

		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		Object responseObject = restRepo.fetchResult(url, workflowReq);
		response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}

	public State updateWorkflow(CommunityHallBookingRequest bookingRequest) {

		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();

		ProcessInstanceRequest workflowReq = getProcessInstanceForHallBooking(bookingDetail,
				bookingRequest.getRequestInfo());

		State state = callWorkFlow(workflowReq);

		return state;
	}
// Create process instance request for workflow call
	private ProcessInstanceRequest getProcessInstanceForHallBooking(CommunityHallBookingDetail bookingDetail,
			RequestInfo requestInfo) {

		ProcessInstance workflow = null != bookingDetail.getWorkflow() ? bookingDetail.getWorkflow()
				: new ProcessInstance();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(bookingDetail.getBookingNo());
		processInstance.setAction(workflow.getAction());
		processInstance.setModuleName(workflow.getModuleName());
		processInstance.setTenantId(bookingDetail.getTenantId());
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

		// return processInstance;
		return ProcessInstanceRequest.builder().requestInfo(requestInfo)
				.processInstances(Arrays.asList(processInstance)).build();

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
		// url.append(configs.getWfProcessInstanceSearchPath());
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