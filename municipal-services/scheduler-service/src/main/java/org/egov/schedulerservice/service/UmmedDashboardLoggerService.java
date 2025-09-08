package org.egov.schedulerservice.service;

import java.time.LocalDate;

import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.config.SchedulerConfiguration;
import org.egov.schedulerservice.constants.ErrorConstants;
import org.egov.schedulerservice.exception.SchedulerServiceException;
import org.egov.schedulerservice.request.UmeedDashboardRequest;
import org.egov.schedulerservice.request.UmeedLogRequest;
import org.egov.schedulerservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UmmedDashboardLoggerService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;
	
	@Autowired
	private final ObjectMapper objectMapper;


	public Object saveUmeedDashbaordLog(String ingestResponse , UmeedDashboardRequest umeedDashboardRequest) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getHpudLandingServiceHostUrl());
			url.append(applicationConfig.getUmeedDashboardLoggerCreateEndpoint());
			// Make the POST request
			 ObjectMapper mapper = new ObjectMapper();
			 UmeedLogRequest createLog = UmeedLogRequest.builder()
	                    .date(LocalDate.now().toString())
	                    .serviceType("NewTL")
	                    .requestPayload(objectMapper.valueToTree(umeedDashboardRequest))
	                    .responsePayload(objectMapper.valueToTree(ingestResponse))
	                    .build();
			 
			ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url.toString(), createLog,
					Object.class);
			return responseEntity.getBody();
			
//			return null;
			
		} catch (Exception e) {
			log.error("Error occured while getting umeed dashbaord data matrics.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_TL_SERVICE_ERROR,
					"Error occured while getting umeed dashbaord data matrics. Message: " + e.getMessage());
		}
	}
}
