package org.upyog.chb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.workflow.BusinessService;
import org.upyog.chb.web.models.workflow.BusinessServiceResponse;
import org.upyog.chb.web.models.workflow.ProcessInstance;
import org.upyog.chb.web.models.workflow.ProcessInstanceRequest;
import org.upyog.chb.web.models.workflow.ProcessInstanceResponse;
import org.upyog.chb.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;

@Service
public class WorkflowService {

	private final CommunityHallBookingConfiguration configs;
	private final ServiceRequestRepository restRepo;
	private final ObjectMapper mapper;

	public WorkflowService(CommunityHallBookingConfiguration configs, ServiceRequestRepository restRepo,
			ObjectMapper mapper) {
		this.configs = configs;
		this.restRepo = restRepo;
		this.mapper = mapper;
	}

	public State callWorkFlow(ProcessInstanceRequest workflowReq) {

		ProcessInstanceResponse response;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		Object responseObject = restRepo.fetchResult(url, workflowReq);
		response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}

	public State updateWorkflow(VenueBookingRequest bookingRequest) {

		VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();

		ProcessInstanceRequest workflowReq = getProcessInstanceForHallBooking(bookingDetail,
				bookingRequest.getRequestInfo());

		return callWorkFlow(workflowReq);
	}

	private ProcessInstanceRequest getProcessInstanceForHallBooking(VenueBookingDetail bookingDetail,
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

		return ProcessInstanceRequest.builder().requestInfo(requestInfo)
				.processInstances(Arrays.asList(processInstance)).build();

	}

	public BusinessService getBusinessService(String tenantId, String businessService, RequestInfo requestInfo) {

		StringBuilder url = getSearchURLWithParams(tenantId, businessService);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object responseObject = restRepo.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response;
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

	private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append(configs.getWfBusinessServiceSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessServices=");
		url.append(businessService);
		return url;
	}

	public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode))
				return state.getIsStateUpdatable();
		}
		return Boolean.FALSE;
	}

	private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {

		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessIds=");
		url.append(businessId);
		return url;
	}

	public State getCurrentState(RequestInfo requestInfo, String tenantId, String businessId) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);

		Object responseObject = restRepo.fetchResult(url, requestInfoWrapper);
		ProcessInstanceResponse response;

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
