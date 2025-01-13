package org.egov.web.notification.sms.service.impl;

import java.util.Collections;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.egov.web.notification.sms.config.SMSProperties;
import org.egov.web.notification.sms.models.UserSearchRequest;
import org.egov.web.notification.sms.models.UserSearchResponse;
import org.egov.web.notification.sms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SMSProperties smsProperties;

	@Override
	public UserSearchResponse searchUser(String userUuid) {
		StringBuilder url = new StringBuilder(smsProperties.getUserServiceHostUrl());
		url.append(smsProperties.getUserSearchEndpoint());

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
