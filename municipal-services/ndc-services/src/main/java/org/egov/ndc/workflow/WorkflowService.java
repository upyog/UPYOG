package org.egov.ndc.workflow;


import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.web.model.RequestInfoWrapper;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class WorkflowService {
	
	@Autowired
	private NDCConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	public BusinessService getBusinessService(NdcApplicationRequest ndc, RequestInfo requestInfo, String bussinessServiceValue) {
		StringBuilder url = getSearchURLWithParams(bussinessServiceValue, ndc.getApplications().get(0).getTenantId());
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response = null;
		try {
			response = mapper.convertValue(result, BusinessServiceResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response");
		}
		return response.getBusinessServices().isEmpty() ? null : response.getBusinessServices().get(0);
	}
	
	private StringBuilder getSearchURLWithParams(String bussinessServiceValue, String tenantId) {
        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(bussinessServiceValue);
        return url;
    }
	
	public State getCurrentState(String status, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getApplicationStatus() != null
					&& state.getApplicationStatus().equalsIgnoreCase(status.toString()))
				return state;
		}
		return null;
	}
	
	public Boolean isStateUpdatable(String status, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getApplicationStatus() != null
					&& state.getApplicationStatus().equalsIgnoreCase(status.toString()))
				return state.getIsStateUpdatable();
		}
		return Boolean.FALSE;
	}

	public Map<String, ProcessInstance> getProcessInstances(RequestInfo requestInfo, Set<String> applicationNumbers, String tenantId, String businessServiceValue) {
		StringBuilder url = getProcessInstanceSearchURL(tenantId, applicationNumbers, businessServiceValue);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		Map<String, ProcessInstance> processInstanceMap = new HashMap<>();

		PlainAccessRequest apiPlainAccessRequest = requestInfo.getPlainAccessRequest();
		/* Creating a PlainAccessRequest object to get unmasked mobileNumber for Assignee */
		List<String> plainRequestFieldsList = new ArrayList<String>() {{
			add("mobileNumber");
		}};

		ProcessInstanceResponse response;
		try {
			response = mapper.convertValue(result, ProcessInstanceResponse.class);
			List<ProcessInstance> processInstanceList = new ArrayList<>();
			for (ProcessInstance processInstance : response.getProcessInstances()) {
				if (!ObjectUtils.isEmpty(processInstance)) {

					if (response.getProcessInstances().get(0).getAssignes() != null) {
						PlainAccessRequest plainAccessRequest = PlainAccessRequest.builder().recordId(response.
										getProcessInstances().get(0).getAssignes().get(0).getUuid())
								.plainRequestFields(plainRequestFieldsList).build();

						requestInfo.setPlainAccessRequest(plainAccessRequest);
						requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
					}
				}
				Object resultNew = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
				response = mapper.convertValue(resultNew, ProcessInstanceResponse.class);
				//Re-setting the original PlainAccessRequest object that came from api request
				requestInfo.setPlainAccessRequest(apiPlainAccessRequest);

				Optional<ProcessInstance> processInstances = Optional.ofNullable(processInstance);
				if (!ObjectUtils.isEmpty(response.getProcessInstances())) {

					if (processInstances.get().getAssignes() != null) {
						/* encrypt here */
						processInstances.get().setAssignes((List<org.egov.common.contract.request.User>) (processInstances.get().getAssignes()));

						/* decrypt here */
						//	processInstances.get().setAssignes(encryptionDecryptionUtil.decryptObject(processInstances.get().getAssignes(), WNS_OWNER_ENCRYPTION_MODEL, User.class, requestInfo));
					}
				}
				processInstanceMap.put(processInstance.getBusinessId(), processInstance);
			}
			return processInstanceMap;
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING_ERROR", "Failed to parse response of process instance");
		}
	}

	private StringBuilder getProcessInstanceSearchURL(String tenantId, Set<String> applicationNos, String businessServiceValue) {
		StringBuilder url = new StringBuilder(config.getWfHost());
		url.append(config.getWfProcessSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		if(businessServiceValue!=null) {
			url.append("&businessServices=");
			url.append(businessServiceValue);
		}
		url.append("&businessIds=");
		for (String appNo : applicationNos) {
			url.append(appNo).append(",");
		}
		url.setLength(url.length() - 1);
		return url;
	}


}