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
public class PropertyService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public String generatePropertyTax(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPropertyServiceHostUrl());
			url.append(applicationConfig.getPropertyTaxCalculatorEndpoint());

			// Make the POST request
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while generating property tax.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PROPERTY_SERVICE_ERROR,
					"Error occured while generating property tax. Message: " + e.getMessage());
		}
	}

	public String updateTrackerBillStatus(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPropertyServiceHostUrl());
			url.append(applicationConfig.getPropertyUpdateTrackerBillStatusEndpoint());

			// Make the POST request
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while updating tracker bill status.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PROPERTY_SERVICE_ERROR,
					"Error occured while updating tracker bill status. Message: " + e.getMessage());
		}
	}

	public String reverseRebateAmount(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPropertyServiceHostUrl());
			url.append(applicationConfig.getPropertyReverseRebateAmountEndpoint());

			// Make the POST request
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while reverse rebate amount.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PROPERTY_SERVICE_ERROR,
					"Error occured while reverse rebate amount. Message: " + e.getMessage());
		}
	}

}
