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
public class PgService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public String transferAmount(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPgServiceHostUrl());
			url.append(applicationConfig.getPgTransferAmountEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while transferring amount.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PG_SERVICE_ERROR,
					"Error occured while transferring amount. Message: " + e.getMessage());
		}
	}

	public String settlementStatusUpdate(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getPgServiceHostUrl());
			url.append(applicationConfig.getPgSettlementStatusUpdateEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while updating settlement status.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_PG_SERVICE_ERROR,
					"Error occured while updating settlement status. Message: " + e.getMessage());
		}

	}

}
