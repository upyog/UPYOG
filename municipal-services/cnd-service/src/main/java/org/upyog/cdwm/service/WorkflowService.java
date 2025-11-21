package org.upyog.cdwm.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.constants.CNDConstants;
import org.upyog.cdwm.repository.ServiceRequestRepository;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.Workflow;
import org.upyog.cdwm.web.models.workflow.BusinessService;
import org.upyog.cdwm.web.models.workflow.BusinessServiceResponse;
import org.upyog.cdwm.web.models.workflow.ProcessInstance;
import org.upyog.cdwm.web.models.workflow.ProcessInstanceRequest;
import org.upyog.cdwm.web.models.workflow.ProcessInstanceResponse;
import org.upyog.cdwm.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import digit.models.coremodels.RequestInfoWrapper;

@Service
public class WorkflowService {

	@Autowired
	private CNDConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;
	
	/**
	 * Updates the workflow status based on the provided PaymentRequest or CNDApplicationRequest.
	 * If both are null, an exception is thrown.
	 * 
	 * @param paymentRequest       The payment request object
	 * @param cndApplicationRequest The CND application request object
	 * @return The updated State object
	 */

	public State updateWorkflowStatus(PaymentRequest paymentRequest, CNDApplicationRequest cndApplicationRequest) {
		ProcessInstance processInstance;
		RequestInfo requestInfo;

		if (paymentRequest != null) {
			processInstance = getProcessInstanceForCnD(paymentRequest, null, null);
			requestInfo = paymentRequest.getRequestInfo();
		} else if (cndApplicationRequest != null) {
			processInstance = getProcessInstanceForCnD(null, cndApplicationRequest.getCndApplication(),
					cndApplicationRequest.getRequestInfo());
			requestInfo = cndApplicationRequest.getRequestInfo();
		} else {
			throw new IllegalArgumentException("Both PaymentRequest and cndApplicationRequest cannot be null");
		}
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo,
				Collections.singletonList(processInstance));

		return callWorkFlow(workflowRequest);
	}

	/**
	 * Creates a ProcessInstance object for workflow processing based on either PaymentRequest or CNDApplicationDetail.
	 * 
	 * @param paymentRequest The payment request object
	 * @param application The CND application detail
	 * @param requestInfo The request information
	 * @return A ProcessInstance object
	 */
	private ProcessInstance getProcessInstanceForCnD(PaymentRequest paymentRequest, CNDApplicationDetail application,
			RequestInfo requestInfo) {
		ProcessInstance processInstance = new ProcessInstance();

		if (paymentRequest != null) {
			processInstance
					.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
			processInstance.setAction(CNDConstants.ACTION_PAY);
			processInstance.setModuleName(configs.getModuleName());
			processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
			processInstance.setBusinessService(configs.getBusinessServiceName());
			processInstance.setDocuments(null);
			processInstance.setComment(null);
			processInstance.setAssignes(null);
		} else if (application != null) {
			Workflow workflow = application.getWorkflow();
			processInstance.setBusinessId(application.getApplicationNumber());
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
			throw new IllegalArgumentException("Both PaymentRequest and cndApplicationRequest cannot be null");
		}

		return processInstance;
	}

	/**
	 * Constructs the workflow transition request and retrieves the updated state.
	 * 
	 * @param workflowReq The workflow request object
	 * @return The updated State object
	 */
	public State callWorkFlow(ProcessInstanceRequest workflowReq) {

		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		Object optional = restRepo.fetchResult(url, workflowReq);
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