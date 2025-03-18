package org.upyog.rs.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.upyog.rs.web.models.Workflow;
import org.upyog.rs.web.models.workflow.BusinessService;
import org.upyog.rs.web.models.workflow.BusinessServiceResponse;
import org.upyog.rs.web.models.workflow.ProcessInstance;
import org.upyog.rs.web.models.workflow.ProcessInstanceRequest;
import org.upyog.rs.web.models.workflow.ProcessInstanceResponse;
import org.upyog.rs.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import digit.models.coremodels.RequestInfoWrapper;

@Service
public class WorkflowService {

	@Autowired
	private RequestServiceConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

/*	public State createWorkflowStatus(WaterTankerBookingRequest waterTankerRequest) {
		
			ProcessInstance processInstance = getProcessInstanceForRS(waterTankerRequest.getWaterTankerBookingDetail(),
					waterTankerRequest.getRequestInfo());
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(waterTankerRequest.getRequestInfo(),
					Collections.singletonList(processInstance));
			State state = callWorkFlow(workflowRequest);
			
			return state; 
		
	}*/
	
	public State updateWorkflowStatus(PaymentRequest paymentRequest, WaterTankerBookingRequest waterTankerRequest) {
	    ProcessInstance processInstance;
	    RequestInfo requestInfo;

	    if (paymentRequest != null) {
	        processInstance = getProcessInstanceForRS(paymentRequest, null, null);
	        requestInfo = paymentRequest.getRequestInfo();
	    } else if (waterTankerRequest != null) {
	        processInstance = getProcessInstanceForRS(null, waterTankerRequest.getWaterTankerBookingDetail(), waterTankerRequest.getRequestInfo());
	        requestInfo = waterTankerRequest.getRequestInfo();
	    } else {
	        throw new IllegalArgumentException("Both PaymentRequest and WaterTankerBookingRequest cannot be null");
	    }
	    ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo, Collections.singletonList(processInstance));

	    return callWorkFlow(workflowRequest);
	}

	public State updateMTWorkflowStatus(PaymentRequest paymentRequest, MobileToiletBookingRequest mobileToiletRequest) {
		ProcessInstance processInstance;
		RequestInfo requestInfo;

		if (paymentRequest != null) {
			processInstance = getProcessInstanceForMTRS(paymentRequest, null, null);
			requestInfo = paymentRequest.getRequestInfo();
		} else if (mobileToiletRequest != null) {
			processInstance = getProcessInstanceForMTRS(null, mobileToiletRequest.getMobileToiletBookingDetail(), mobileToiletRequest.getRequestInfo());
			requestInfo = mobileToiletRequest.getRequestInfo();
		} else {
			throw new IllegalArgumentException("Both PaymentRequest and WaterTankerBookingRequest cannot be null");
		}
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo, Collections.singletonList(processInstance));

		return callWorkFlow(workflowRequest);
	}


	/*private ProcessInstance getProcessInstanceForRS(WaterTankerBookingDetail application, RequestInfo requestInfo) {
		Workflow workflow = application.getWorkflow();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(application.getBookingNo());
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

	} */
	
	private ProcessInstance getProcessInstanceForRS(PaymentRequest paymentRequest, WaterTankerBookingDetail application, RequestInfo requestInfo) {
	    ProcessInstance processInstance = new ProcessInstance();

	    if (paymentRequest != null) {
	        processInstance.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
	        processInstance.setAction(RequestServiceConstants.ACTION_PAY);
	        processInstance.setModuleName(configs.getWtModuleName());
	        processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
	        processInstance.setBusinessService(configs.getBusinessServiceName());
	        processInstance.setDocuments(null);
	        processInstance.setComment(null);
	        processInstance.setAssignes(null);
	    } else if (application != null) {
	        Workflow workflow = application.getWorkflow();
	        processInstance.setBusinessId(application.getBookingNo());
	        processInstance.setAction(workflow.getAction());
	        processInstance.setModuleName(workflow.getModuleName());
	        processInstance.setTenantId(application.getTenantId());
	        processInstance.setBusinessService(workflow.getBusinessService());
	        processInstance.setDocuments(workflow.getDocuments());
	        processInstance.setComment(workflow.getComments());

	        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
	            List<User> users = workflow.getAssignes().stream().map(uuid -> {
	                User user = new User();
	                user.setUuid(uuid);
	                return user;
	            }).collect(Collectors.toList());

	            processInstance.setAssignes(users);
	        }
	    } else {
	        throw new IllegalArgumentException("Both PaymentRequest and WaterTankerBookingDetail cannot be null");
	    }

	    return processInstance;
	}

	private ProcessInstance getProcessInstanceForMTRS(PaymentRequest paymentRequest, MobileToiletBookingDetail application, RequestInfo requestInfo) {
		ProcessInstance processInstance = new ProcessInstance();

		if (paymentRequest != null) {
			processInstance.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
			processInstance.setAction(RequestServiceConstants.ACTION_PAY);
			processInstance.setModuleName(configs.getMtModuleName());
			processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
			processInstance.setBusinessService(configs.getMobileToiletBusinessService());
			processInstance.setDocuments(null);
			processInstance.setComment(null);
			processInstance.setAssignes(null);
		} else if (application != null) {
			Workflow workflow = application.getWorkflow();
			processInstance.setBusinessId(application.getBookingNo());
			processInstance.setAction(workflow.getAction());
			processInstance.setModuleName(workflow.getModuleName());
			processInstance.setTenantId(application.getTenantId());
			processInstance.setBusinessService(workflow.getBusinessService());
			processInstance.setDocuments(workflow.getDocuments());
			processInstance.setComment(workflow.getComments());

			if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
				List<User> users = workflow.getAssignes().stream().map(uuid -> {
					User user = new User();
					user.setUuid(uuid);
					return user;
				}).collect(Collectors.toList());

				processInstance.setAssignes(users);
			}
		} else {
			throw new IllegalArgumentException("Both PaymentRequest and WaterTankerBookingDetail cannot be null");
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

		StringBuilder url = getSearchURLWithBusinessService(tenantId, businessService);
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
	private StringBuilder getSearchURLWithBusinessService(String tenantId, String businessService) {

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
	public boolean isStateUpdatable(String stateCode, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode))
				return state.getIsStateUpdatable();
		}
		return false;
	}

	/**
	 * Creates url for searching processInstance
	 *
	 * @return The search url
	 */
	private StringBuilder getWorkflowSearchURLWithBusinessId(String tenantId, String businessId) {

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

		StringBuilder url = getWorkflowSearchURLWithBusinessId(tenantId, businessId);

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