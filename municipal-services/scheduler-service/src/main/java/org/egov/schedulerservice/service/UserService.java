package org.egov.schedulerservice.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.contract.request.User;
import org.egov.schedulerservice.config.SchedulerConfiguration;
import org.egov.schedulerservice.constants.ErrorConstants;
import org.egov.schedulerservice.exception.SchedulerServiceException;
import org.egov.schedulerservice.request.UserSearchRequest;
import org.egov.schedulerservice.response.UserSearchResponse;
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

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public Map<String, User> searchUser(UserSearchRequest userSearchRequest) {

		StringBuilder url = new StringBuilder(applicationConfig.getUserServiceHostUrl());
		url.append(applicationConfig.getUserSearchEndpoint());

		UserSearchResponse userSearchResponse = null;

		try {
			userSearchResponse = restTemplate.postForObject(url.toString(), userSearchRequest,
					UserSearchResponse.class);
		} catch (HttpServerErrorException e) {

			LinkedHashMap<?, ?> customException = new Gson().fromJson(e.getResponseBodyAsString(), LinkedHashMap.class);

			String errorMessage = String.format("Message: %s", customException.get("errorMessage"));
			throw new SchedulerServiceException(ErrorConstants.ERR_USER_SERVICE_ERROR, errorMessage);
		} catch (Exception e) {
			log.error("Error occured while update user role.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_USER_SERVICE_ERROR,
					"Error occured while update user role. Message: " + e.getMessage());
		}

		Map<String, User> uuidToUserMap = new HashMap<>();
		if (null != userSearchResponse && !CollectionUtils.isEmpty(userSearchResponse.getUser())) {
			userSearchResponse.getUser().forEach(user -> {
				uuidToUserMap.put(user.getUuid(), user);
			});
		}
		return uuidToUserMap;
	}

}
