package org.hpud.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.hpud.errorhandlers.SchedulerServiceException;
import org.hpud.model.UserSearchRequest;
import org.hpud.model.UserSearchResponse;
import org.hpud.util.SupportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserService {

	private static final String SYSTEM = "SYSTEM";
	public static final String INTERNAL_MICROSERVICE_ROLE_CODE = "INTERNAL_MICROSERVICE_ROLE";


	@Autowired
	SupportConstants config;
	
	@Autowired
	private RestTemplate restTemplate;

	
	public RequestInfo createDefaultRequestInfo() {
		User userInfo = User.builder().uuid(SYSTEM).type(SYSTEM).roles(Collections.emptyList()).id(0L).build();

//		org.egov.common.contract.request.User userInfo = org.egov.common.contract.request.User.builder().type(SYSTEM).roles(Collections.emptyList()).id(0).build();

//		org.egov.common.contract.request.User userInfo = new org.egov.common.contract.request.User();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setUserInfo(userInfo);

		List<String> roles = new ArrayList<>();
		roles.add(INTERNAL_MICROSERVICE_ROLE_CODE);

		List<User> users = fetchEmployeeByRole(requestInfo, roles);

		if (!CollectionUtils.isEmpty(users)) {
			requestInfo.setUserInfo(users.get(0));
		}

		return requestInfo;
	}
	
	public Map<String, User> searchUser(UserSearchRequest userSearchRequest) {

		UserSearchResponse userSearchResponse = null;

		userSearchResponse = getUser(userSearchRequest);

		Map<String,User> uuidToUserMap = new HashMap<>();
		if (null != userSearchResponse && !CollectionUtils.isEmpty(userSearchResponse.getUser())) {
			userSearchResponse.getUser().forEach(user -> {
				uuidToUserMap.put(user.getUuid(), user);
			});
		}
		return uuidToUserMap;
	}
	
	public UserSearchResponse getUser(UserSearchRequest userSearchRequest){
		
		StringBuilder url = new StringBuilder(config.getUserServiceHostUrl()).append(config.getUserSearchEndpoint());
//		StringBuilder url = new StringBuilder(applicationConfig.getUserServiceHostUrl());
//		url.append(applicationConfig.getUserSearchEndpoint());

		UserSearchResponse userSearchResponse = null;

		try {
			userSearchResponse = restTemplate.postForObject(url.toString(), userSearchRequest,
					UserSearchResponse.class);
		} catch (HttpServerErrorException e) {

			LinkedHashMap<?, ?> customException = new Gson().fromJson(e.getResponseBodyAsString(), LinkedHashMap.class);

			String errorMessage = String.format("Message: %s", customException.get("errorMessage"));
			throw new SchedulerServiceException("ERR_USER_SERVICE_ERROR", errorMessage);
		} catch (Exception e) {
//			log.error("Error occured while update user role.", e);
			throw new SchedulerServiceException("ERR_USER_SERVICE_ERROR",
					"Error occured while update user role. Message: " + e.getMessage());
		}
		return userSearchResponse;
	}
	
	private List<User> fetchEmployeeByRole(@NotNull RequestInfo requestInfo, List<String> roleCodes) {
		List<User> userList = new ArrayList<>();

		UserSearchRequest request = UserSearchRequest.builder().tenantId("hp")
				.roleCodes(roleCodes).requestInfo(requestInfo).build();

		Map<String, User> uuidToUserMap = searchUser(request);

		if (!uuidToUserMap.isEmpty()) {
			User user = uuidToUserMap.values().stream().findFirst().get();
			userList.add(user);
		}

		return userList;
	}
}
