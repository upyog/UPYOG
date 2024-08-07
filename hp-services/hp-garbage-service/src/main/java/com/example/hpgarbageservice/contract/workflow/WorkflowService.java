package com.example.hpgarbageservice.contract.workflow;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.util.ApplicationPropertiesAndConstant;
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


	public void callWf(ProcessInstanceRequest processInstanceRequest) {
		StringBuilder url = new StringBuilder(applicationPropertiesAndConstant.getWorkflowHost());
		url.append(applicationPropertiesAndConstant.workflowEndpointTransition);
		Object response = restCallRepository.fetchResult(url, processInstanceRequest);
		ProcessInstanceResponse processInstanceResponse = objectMapper.convertValue(response, ProcessInstanceResponse.class);
		
		if(null == response && null == processInstanceResponse) {
			throw new RuntimeException("Error ocurred while running workflow.");
		}
	}

}
