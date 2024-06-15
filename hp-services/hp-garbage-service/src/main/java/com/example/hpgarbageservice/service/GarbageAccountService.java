package com.example.hpgarbageservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccountRequest;
import com.example.hpgarbageservice.model.contract.RequestInfo;
import com.example.hpgarbageservice.repository.GarbageAccountRepository;

@Service
public class GarbageAccountService {

	@Autowired
	private GarbageAccountRepository repository;

	public List<GarbageAccount> create(GarbageAccountRequest createGarbageRequest) {

		List<GarbageAccount> garbageAccountsResponse = new ArrayList<>();
		
		if (!CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())) {
			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {

				// validate create grbg act
				validateGarbageAccount(garbageAccount);

				// enrich grbg act
				enrichCreateGarbageAccount(garbageAccount, createGarbageRequest.getRequestInfo());

				// create grbg act
				garbageAccountsResponse.add(repository.create(garbageAccount));

			});
		}
		
		return garbageAccountsResponse;
	}

	private void validateGarbageAccount(GarbageAccount garbageAccount) {

		if (null == garbageAccount
				|| null == garbageAccount.getMobileNumber()
				|| null == garbageAccount.getName()
				|| null == garbageAccount.getType()
				|| null == garbageAccount.getPropertyId()) {
			throw new RuntimeException("Provide garbage account details.");
		}

	}

	private void enrichCreateGarbageAccount(GarbageAccount garbageAccount, RequestInfo requestInfo) {

		AuditDetails auditDetails = null;

		if (null != requestInfo
				&& null != requestInfo.getUserInfo()) {
			auditDetails = AuditDetails.builder()
					.createdBy(requestInfo.getUserInfo().getUuid())
					.createdDate(new Date().getTime())
					.lastModifiedBy(requestInfo.getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
			garbageAccount.setAuditDetails(auditDetails);
		}

		// generate garbage_id
		garbageAccount.setGarbageId(System.currentTimeMillis());

	}

	private void enrichUpdateGarbageAccount(GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount, RequestInfo requestInfo) {

		AuditDetails auditDetails = null;
		if (null != requestInfo
				&& null != requestInfo.getUserInfo()) {
			auditDetails = AuditDetails.builder()
					.lastModifiedBy(requestInfo.getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
		}
		if (null != existingGarbageAccount.getAuditDetails()) {
			auditDetails.setCreatedBy(existingGarbageAccount.getAuditDetails().getCreatedBy());
			auditDetails.setCreatedDate(existingGarbageAccount.getAuditDetails().getCreatedDate());
		}

		newGarbageAccount.setAuditDetails(auditDetails);
		newGarbageAccount.setId(existingGarbageAccount.getId());
		newGarbageAccount.setGarbageId(existingGarbageAccount.getGarbageId());
	}

	public List<GarbageAccount> update(GarbageAccountRequest updateGarbageRequest) {

		List<GarbageAccount> garbageAccountsResponse = new ArrayList<>();
		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(updateGarbageRequest.getGarbageAccounts());
		Map<Long, GarbageAccount> existingGarbageAccountsMap = searchGarbageAccountMap(searchCriteriaGarbageAccount, updateGarbageRequest.getRequestInfo());
		
		if(!CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())) {
			updateGarbageRequest.getGarbageAccounts().forEach(newGarbageAccount -> {
				// search existing grbg acc
				GarbageAccount existingGarbageAccount = existingGarbageAccountsMap.get(newGarbageAccount.getGarbageId());

				// validate existing and new grbg acc
				validateUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount);

				// replicate existing grbg acc to history table

				// enrich new request
				enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount, updateGarbageRequest.getRequestInfo());

				// update garbage account
				repository.update(newGarbageAccount);
				garbageAccountsResponse.add(newGarbageAccount);
			});
		}
		
		return garbageAccountsResponse;
	}

	private Map<Long, GarbageAccount> searchGarbageAccountMap(
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, RequestInfo requestInfo) {
		
		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest.builder()
				.searchCriteriaGarbageAccount(searchCriteriaGarbageAccount)
				.requestInfo(requestInfo)
				.build();
		
		List<GarbageAccount> garbageAccounts = searchGarbageAccounts(searchCriteriaGarbageAccountRequest);
		
		Map<Long, GarbageAccount> existingGarbageAccountsMap = new HashMap<>();
		garbageAccounts.stream().forEach(account -> {
			existingGarbageAccountsMap.put(account.getGarbageId(), account);
		});
		
		return existingGarbageAccountsMap;
	}

	private SearchCriteriaGarbageAccount createSearchCriteriaByGarbageAccounts(
			List<GarbageAccount> garbageAccounts) {
		
		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = SearchCriteriaGarbageAccount.builder().build();
//		List<Long> ids = new ArrayList<>();
		List<Long> garbageIds = new ArrayList<>();
		
		garbageAccounts.stream().forEach(grbgAcc -> {
//			if(null != grbgAcc.getId() && 0 <= grbgAcc.getId()) {
//				ids.add(grbgAcc.getId());
//			}
			if(null != grbgAcc.getGarbageId() && 0 <= grbgAcc.getGarbageId()) {
				garbageIds.add(grbgAcc.getGarbageId());
			}
		});
		

//		if (!CollectionUtils.isEmpty(ids)) {
//			searchCriteriaGarbageAccount.setId(ids);
//		}
		if (!CollectionUtils.isEmpty(garbageIds)) {
			searchCriteriaGarbageAccount.setGarbageId(garbageIds);
		}
		
		
		return searchCriteriaGarbageAccount;
	}

	private void validateUpdateGarbageAccount(GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount) {
		if (null == existingGarbageAccount) {
			throw new RuntimeException("Provided garbage account doesn't exist.");
		}
		// validate grbg acc req
		validateGarbageAccount(newGarbageAccount);
	}

	public List<GarbageAccount> searchGarbageAccounts(SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		
		//validate search criteria
		validateSearchGarbageAccount(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());
		
		//search garbage account
		List<GarbageAccount> grbgAccs = repository.searchGarbageAccount(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());
		
		//search child garbage accounts
		grbgAccs.stream().forEach(grbgAccTemp -> {
			searchChildGarbageAccounts(grbgAccTemp);
		});
		
		return grbgAccs;
	}

	private void searchChildGarbageAccounts(GarbageAccount grbgAccTemp) {
		SearchCriteriaGarbageAccount searchCriteriaGarbageAccountNew = SearchCriteriaGarbageAccount.builder()
				.parentId(Collections.singletonList(grbgAccTemp.getId()))
				.build();
		//search child garbage account
		List<GarbageAccount> subAccs = repository.searchGarbageAccount(searchCriteriaGarbageAccountNew);
		grbgAccTemp.setChildGarbageAccounts(subAccs);
	}

	private void validateSearchGarbageAccount(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
		
		if(CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getParentId())) {
			throw new RuntimeException("Provide the parameters to search garbage accounts.");
		}
		
	}

}
