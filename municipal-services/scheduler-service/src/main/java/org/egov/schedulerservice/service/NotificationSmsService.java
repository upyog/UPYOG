package org.egov.schedulerservice.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.config.SchedulerConfiguration;
import org.egov.schedulerservice.constants.ErrorConstants;
import org.egov.schedulerservice.exception.SchedulerServiceException;
import org.egov.schedulerservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationSmsService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public String sendSmsNotification(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getNotificationSmsHostUrl());
			url.append(applicationConfig.getSendNotificationSmsEndpoint());
			
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while changing Send Notification request.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_ADRVCANOPY_SITE_BOOKING_SERVICE_ERROR,
					"Error occured while changing Site status request. Message: " + e.getMessage());
		}
	}

}
