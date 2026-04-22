package com.cdac.esign.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cdac.esign.model.UserSearchRequest;
import com.cdac.esign.repository.ServiceRequestRepository;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService{
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Value("${egov.hrms.host}")
	private String hrmsHost;

	@Value("${egov.employee.search.endpoint}")
	private String hrmsSearchEndpoint;

	public String getEmployeeDesignation(RequestInfo requestInfo , String uuid, String tenantId) {
		StringBuilder uri = new StringBuilder(hrmsHost).append(hrmsSearchEndpoint);
		uri.append("?tenantId=").append(tenantId)
				.append("&isActive=true")
				.append("&uuids=")
				.append(uuid);
		JSONObject hrmsRequest = new JSONObject();
		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setRequestInfo(requestInfo);
		hrmsRequest.put("RequestInfo", requestInfo);
		Object response = serviceRequestRepository.fetchResult(uri, userSearchRequest);
		String designation = "Citizen";

		List<String> designations = JsonPath.read(response, "$.Employees.*.[?(@.uuid == '" + uuid + "')].assignments.[0].designation");
			if(!CollectionUtils.isEmpty(designations))
				designation = designations.get(0);
	
		return designation;
	}
	
}
