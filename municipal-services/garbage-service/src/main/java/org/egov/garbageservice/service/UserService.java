package org.egov.garbageservice.service;

import java.util.Collections;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.model.UserSearchRequest;
import org.egov.garbageservice.model.UserSearchResponse;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GrbgConstants grbgConfig;

	public UserSearchResponse searchUser(String userUuid) {
		StringBuilder url = new StringBuilder(grbgConfig.getUserServiceHostUrl());
		url.append(grbgConfig.getUserSearchEndpoint());

		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.requestInfo(RequestInfo.builder().userInfo(User.builder().uuid(userUuid).build()).build())
				.uuid(Collections.singletonList(userUuid)).build();

		UserSearchResponse userSearchResponse = null;
		try {
			userSearchResponse = restTemplate.postForObject(url.toString(), userSearchRequest,
					UserSearchResponse.class);
		} catch (Exception e) {
			log.error("Error occured while user search.", e);
			throw new CustomException("USER SEARCH ERROR",
					"Error occured while user search. Message: " + e.getMessage());
		}

		return userSearchResponse;
	}

}
