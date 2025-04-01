package org.egov.garbageservice.contract.workflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.garbageservice.model.GarbageAccountActionRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkflowService {
	
	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private GrbgConstants applicationPropertiesAndConstant;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	private RestTemplate restTemplate;

	public ProcessInstanceResponse callWf(ProcessInstanceRequest processInstanceRequest) {
		StringBuilder url = new StringBuilder(applicationPropertiesAndConstant.getWorkflowHost());
		url.append(applicationPropertiesAndConstant.workflowEndpointTransition);
		Object response = restCallRepository.fetchResult(url, processInstanceRequest);
		ProcessInstanceResponse processInstanceResponse = objectMapper.convertValue(response, ProcessInstanceResponse.class);
		
		if(null == response || null == processInstanceResponse) {
			throw new CustomException("WORKFLOW_RESPONSE_NULL","Error ocurred while running workflow.");
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
	
	public ValidActionResponce getValidAction(RequestInfo requestInfo, String businessId, String tenantId) {

		try {
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(applicationPropertiesAndConstant.getWorkflowHost())
					.path(applicationPropertiesAndConstant.getWorkflowValidActionSearchPath());

			if (StringUtils.isNotEmpty(businessId)) {
				uriBuilder.queryParam("businessId", businessId);
			}
			if (StringUtils.isNotEmpty(tenantId)) {
				uriBuilder.queryParam("tenantId", tenantId);
			}

			String url = uriBuilder.toUriString();

			ResponseEntity<ValidActionResponce> response = restTemplate.postForEntity(url.toString(),
					RequestInfoWrapper.builder().requestInfo(requestInfo).build(), ValidActionResponce.class);
			// Check the response status and return the body
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			} else {
//				throw new RuntimeException("Failed to retrieve action");
			}

		} catch (Exception e) {
			log.error("Exception while calling wf action: ", e);
			throw new CustomException("ERR_TECHNICAL", "Invalid response format from external API");
		}
		return ValidActionResponce.builder().build();
	}
	

}
