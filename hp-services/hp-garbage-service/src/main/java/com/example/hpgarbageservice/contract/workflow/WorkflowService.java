package com.example.hpgarbageservice.contract.workflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hpgarbageservice.model.GarbageAccountActionRequest;
import com.example.hpgarbageservice.util.ApplicationPropertiesAndConstant;
import com.example.hpgarbageservice.util.RequestInfoWrapper;
import com.example.hpgarbageservice.util.RestCallRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkflowService {
	
	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private ApplicationPropertiesAndConstant applicationPropertiesAndConstant;
	
	@Autowired
	ObjectMapper objectMapper;


	public ProcessInstanceResponse callWf(ProcessInstanceRequest processInstanceRequest) {
		StringBuilder url = new StringBuilder(applicationPropertiesAndConstant.getWorkflowHost());
		url.append(applicationPropertiesAndConstant.workflowEndpointTransition);
		Object response = restCallRepository.fetchResult(url, processInstanceRequest);
		ProcessInstanceResponse processInstanceResponse = objectMapper.convertValue(response, ProcessInstanceResponse.class);
		
		if(null == response && null == processInstanceResponse) {
			throw new RuntimeException("Error ocurred while running workflow.");
		}
		
		return processInstanceResponse;
	}
	

	public BusinessServiceResponse businessServiceSearch(GarbageAccountActionRequest garbageAccountActionRequest,
			String applicationTenantId, String applicationBusinessId) {
		StringBuilder uri = new StringBuilder(applicationPropertiesAndConstant.getWorkflowHost());
		uri.append(applicationPropertiesAndConstant.getWorkflowBusinessServiceSearchPath());
		uri.append("?tenantId=").append(applicationTenantId);
		uri.append("&businessServices=").append(applicationBusinessId);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
				.requestInfo(garbageAccountActionRequest.getRequestInfo()).build();
		LinkedHashMap<String, Object> responseObject = (LinkedHashMap<String, Object>) restCallRepository.fetchResult(uri, requestInfoWrapper);
		BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(responseObject
																				, BusinessServiceResponse.class);
		return businessServiceResponse;
	}
	

}
