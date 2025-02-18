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
public class PGRService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public String escalatePGRRequest(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPgrServiceHostUrl());
			url.append(applicationConfig.getPgrRequestEscalatorEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while escalating PGR request.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PGR_SERVICE_ERROR,
					"Error occured while escalating PGR request. Message: " + e.getMessage());
		}
	}

	public String sendPgrNotification(RequestInfo requestInfo) {
		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPgrServiceHostUrl());
			url.append(applicationConfig.getPgrNotificationSenderEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while sending PGR notification.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PGR_SERVICE_ERROR,
					"Error occured while sending PGR notification. Message: " + e.getMessage());
		}
	}

	public String deletePgrNotification(RequestInfo requestInfo) {
		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPgrServiceHostUrl());
			url.append(applicationConfig.getPgrDeleteNotificationEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while deleting PGR notification.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PGR_SERVICE_ERROR,
					"Error occured while deleting PGR notification. Message: " + e.getMessage());
		}
	}

}
