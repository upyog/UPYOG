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
public class TLService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public Object getUmeedDashbaordDataMatrics(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getTlServiceHostUrl());
			url.append(applicationConfig.getTlDataMetricsEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					Object.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while getting umeed dashbaord data matrics.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_TL_SERVICE_ERROR,
					"Error occured while getting umeed dashbaord data matrics. Message: " + e.getMessage());
		}
	}

}
