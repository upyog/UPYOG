package org.egov.schedulerservice.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.contract.garbage.GarbageAccountResponse;
import org.egov.schedulerservice.contract.garbage.SearchCriteriaGarbageAccount;
import org.egov.schedulerservice.contract.garbage.SearchCriteriaGarbageAccountRequest;
import org.egov.schedulerservice.dto.SchedulerMasterBody;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageService {

	public SearchCriteriaGarbageAccountRequest createSearchAccountsRequest(SchedulerMasterBody request, RequestInfo requestInfo, String businessService, Long startId, Long endId) {
		
		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = SearchCriteriaGarbageAccount.builder().tenantId("hp")
																			.startId(startId)
																			.endId(endId)
																			.build();
		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest.builder()
				.requestInfo(requestInfo)
				.searchCriteriaGarbageAccount(searchCriteriaGarbageAccount)
				.build();
		
		
		
		return searchCriteriaGarbageAccountRequest;
	}
	
	
	public GarbageAccountResponse searchAccounts(
			SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		
		
		
		
		
		return null;
	}

}
