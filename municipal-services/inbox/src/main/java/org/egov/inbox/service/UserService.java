package org.egov.inbox.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public boolean isUserPresentForGivenMobileNumber(String mobileNumber, RequestInfo info) {
		List<String> userUuidList = fetchUserUUID(userSearchEndpoint, info, userHost);
		log.info("User uuids fetched : %s for mobile no :  %s");
		return CollectionUtils.isEmpty(userUuidList);
	};

	public List<String> fetchUserUUID(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(userHost).append(userSearchEndpoint);
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		userSearchRequest.put("mobileNumber", mobileNumber);
		List<String> userUuids = new ArrayList<>();
		try {
			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			if (null != user) {
				userUuids = JsonPath.read(user, "$.user.*.uuid");
			} else {
				log.error("Service returned null while fetching user for mobile number - " + mobileNumber);
			}
		} catch (Exception e) {
			log.error("Exception while fetching user for mobile number - " + mobileNumber);
			log.error("Exception trace: ", e);
		}
		return userUuids;
	}

}
