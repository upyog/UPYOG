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

//import org.egov.common.contract.request.RequestInfo;
//import org.egov.schedulerservice.contract.garbage.GarbageAccountResponse;
//import org.egov.schedulerservice.contract.garbage.SearchCriteriaGarbageAccount;
//import org.egov.schedulerservice.contract.garbage.SearchCriteriaGarbageAccountRequest;
//import org.egov.schedulerservice.dto.SchedulerMasterBody;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import lombok.extern.slf4j.Slf4j;
//
@Service
@Slf4j
public class GarbageService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public String generateGarbageBills(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getGarbageServiceHostUrl());
			url.append(applicationConfig.getGarbageBillGeneratorEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while validate And Update Applications.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_GARBAGE_SERVICE_ERROR,
					"Error occured while generating garbage bill. Message: " + e.getMessage());
		}

	}

//	public SearchCriteriaGarbageAccountRequest createSearchAccountsRequest(SchedulerMasterBody request, RequestInfo requestInfo, String businessService, Long startId, Long endId) {
//		
//		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = SearchCriteriaGarbageAccount.builder().tenantId("hp")
//																			.startId(startId)
//																			.endId(endId)
//																			.build();
//		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest.builder()
//				.requestInfo(requestInfo)
//				.searchCriteriaGarbageAccount(searchCriteriaGarbageAccount)
//				.build();
//		
//		
//		
//		return searchCriteriaGarbageAccountRequest;
//	}
//	
//	
//	public GarbageAccountResponse searchAccounts(
//			SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
//		
//		
//		
//		
//		
//		return null;
//	}
//
}
