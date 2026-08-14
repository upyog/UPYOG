package org.egov.ndc.workflow;

import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.web.model.RequestInfoWrapper;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class WorkflowService {

	private final NDCConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;

	public WorkflowService(NDCConfiguration config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	public BusinessService getBusinessService(NdcApplicationRequest ndc, RequestInfo requestInfo, String bussinessServiceValue) {
		StringBuilder url = getSearchURLWithParams(bussinessServiceValue, ndc.getApplications().get(0).getTenantId());
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response;
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
					&& state.getApplicationStatus().equalsIgnoreCase(status))
				return state;
		}
		return null;
	}

	public Boolean isStateUpdatable(String status, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getApplicationStatus() != null
					&& state.getApplicationStatus().equalsIgnoreCase(status))
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
		List<String> plainRequestFieldsList = List.of("mobileNumber");

		ProcessInstanceResponse response;
		try {
			response = mapper.convertValue(result, ProcessInstanceResponse.class);
			for (ProcessInstance processInstance : response.getProcessInstances()) {
				if (ObjectUtils.isEmpty(processInstance)) {
					continue;
				}

				if (response.getProcessInstances().get(0).getAssignes() != null) {
					PlainAccessRequest plainAccessRequest = PlainAccessRequest.builder()
							.recordId(response.getProcessInstances().get(0).getAssignes().get(0).getUuid())
							.plainRequestFields(plainRequestFieldsList)
							.build();

					requestInfo.setPlainAccessRequest(plainAccessRequest);
					requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
				}

				Object resultNew = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
				response = mapper.convertValue(resultNew, ProcessInstanceResponse.class);
				requestInfo.setPlainAccessRequest(apiPlainAccessRequest);

				Optional<ProcessInstance> processInstances = Optional.ofNullable(processInstance);
				if (!ObjectUtils.isEmpty(response.getProcessInstances())
						&& processInstances.isPresent()
						&& processInstances.get().getAssignes() != null) {
					processInstances.get().setAssignes(processInstances.get().getAssignes());
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
