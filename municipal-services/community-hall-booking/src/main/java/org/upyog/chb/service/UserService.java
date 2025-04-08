package org.upyog.chb.service;

import java.util.Collections;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.chb.web.models.UserSearchRequest;
import org.upyog.chb.web.models.UserSearchResponse;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
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
	private CommunityHallBookingConfiguration config;

	public UserSearchResponse searchUser(String userName,RequestInfo requestInfo) {
		StringBuilder url = new StringBuilder(config.getUserHost());
		url.append(config.getUserSearchEndpoint());

		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.requestInfo(requestInfo)
				.userName(userName).userType("EMPLOYEE").tenantId("hp").build();

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
