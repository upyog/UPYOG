package com.example.hpgarbageservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.GrbgApplication;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccountRequest;
import com.example.hpgarbageservice.model.contract.RequestInfo;
import com.example.hpgarbageservice.repository.GarbageAccountRepository;
import com.example.hpgarbageservice.repository.GrbgApplicationRepository;
import com.example.hpgarbageservice.repository.GrbgCommercialDetailsRepository;
import com.example.hpgarbageservice.repository.GrbgDocumentRepository;
import com.example.hpgarbageservice.util.ApplicationPropertiesAndConstant;

@Service
public class GarbageAccountService {

	@Autowired
	private GarbageAccountRepository garbageAccountRepository;

	@Autowired
	private GrbgApplicationRepository grbgApplicationRepository;

	@Autowired
	private GrbgCommercialDetailsRepository grbgCommercialDetailsRepository;

	@Autowired
	private GrbgDocumentRepository grbgDocumentRepository;

	public List<GarbageAccount> create(GarbageAccountRequest createGarbageRequest) {

		List<GarbageAccount> garbageAccountsResponse = new ArrayList<>();
		
		if (!CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())) {
			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {

				// validate create garbage account
				validateGarbageAccount(garbageAccount);

				// enrich create garbage account
				enrichCreateGarbageAccount(garbageAccount, createGarbageRequest.getRequestInfo());
				
				// enrich garbage document
//				enrichCreateGarbageDocuments(garbageAccount);

				// enrich create garbage application
				enrichCreateGarbageApplication(garbageAccount, createGarbageRequest.getRequestInfo());

				// create garbage account
				garbageAccountsResponse.add(garbageAccountRepository.create(garbageAccount));
				
				// create garbage documents
//				createGarbageDocuments(garbageAccount);
				
				// create garbage application
				grbgApplicationRepository.create(garbageAccount.getGrbgApplication());
				
			});
		}
		
		return garbageAccountsResponse;
	}


	private void createGarbageDocuments(GarbageAccount garbageAccount) {
		
		garbageAccount.getDocuments().stream().forEach(doc -> {
			grbgDocumentRepository.create(doc);
		});
		
	}


	private void enrichCreateGarbageDocuments(GarbageAccount garbageAccount) {
		
		garbageAccount.getDocuments().stream().forEach(doc -> {
			doc.setUuid(UUID.randomUUID().toString());
			if(StringUtils.equalsIgnoreCase(doc.getDocCategory(), ApplicationPropertiesAndConstant.DOCUMENT_ACCOUNT)) {
				doc.setTblRefUuid(garbageAccount.getUuid());
			}
		});
		
	}


	private void enrichCreateGarbageApplication(GarbageAccount garbageAccount, RequestInfo requestInfo) {
		
		GrbgApplication grbgApplication = GrbgApplication.builder()
				.uuid(UUID.randomUUID().toString())
				.applicationNo(ApplicationPropertiesAndConstant.APPLICATION_PREFIX.concat(garbageAccount.getGarbageId().toString()))
				.status(ApplicationPropertiesAndConstant.APPLICATION_STATUS_DRAFT)
				.garbageId(garbageAccount.getGarbageId())
				.build();
		
		garbageAccount.setGrbgApplication(grbgApplication);
	}


	private void validateGarbageAccount(GarbageAccount garbageAccount) {

		// validate nullability
		if (null == garbageAccount
				|| null == garbageAccount.getMobileNumber()
				|| null == garbageAccount.getName()
				|| null == garbageAccount.getType()
				|| null == garbageAccount.getPropertyId()) {
			throw new RuntimeException("Provide garbage account details.");
		}
		
		// validate duplicate owner with same properyId
		

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
		garbageAccount.setUuid(UUID.randomUUID().toString());
		garbageAccount.setGarbageId(System.currentTimeMillis());
		garbageAccount.setStatus(ApplicationPropertiesAndConstant.ACCOUNT_STATUS_DRAFT);

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

				// update garbage account
				if(!newGarbageAccount.equals(existingGarbageAccount))
				{
					updateGarbageAccount(updateGarbageRequest, newGarbageAccount, existingGarbageAccount);
				}
				
				
				// update other objects of garbage account
				
				// 1. update application
				if(null != newGarbageAccount.getGrbgApplication()
						&& !newGarbageAccount.getGrbgApplication().equals(existingGarbageAccount.getGrbgApplication()))
				{
					grbgApplicationRepository.update(newGarbageAccount.getGrbgApplication());
				}
				

				// 2. update bills
//				bills loop > make list of deleting, updating and creating bills
				
				
				// commercial details
				if(null != newGarbageAccount.getGrbgCommercialDetails()
						&& StringUtils.isEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())) {
				// 3. create commercial details
					grbgCommercialDetailsRepository.create(newGarbageAccount.getGrbgCommercialDetails());
				}
				else if(null != newGarbageAccount.getGrbgCommercialDetails()
						&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())
						&& !newGarbageAccount.getGrbgCommercialDetails().equals(existingGarbageAccount.getGrbgCommercialDetails())){
				// 4. update commercial details
					grbgCommercialDetailsRepository.update(newGarbageAccount.getGrbgCommercialDetails());
				}
				
				
				
				
				garbageAccountsResponse.add(newGarbageAccount);
			});
		}
		
		return garbageAccountsResponse;
	}


	private void updateGarbageAccount(GarbageAccountRequest updateGarbageRequest, GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount) {
		
		// validate existing and new grbg acc
		validateUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount);

		// replicate existing grbg acc to history table

		// enrich new request
		enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount, updateGarbageRequest.getRequestInfo());

		// update garbage account
		garbageAccountRepository.update(newGarbageAccount);
		
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
		List<GarbageAccount> grbgAccs = garbageAccountRepository.searchGarbageAccount(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());
		
//		//search child garbage accounts
//		grbgAccs.stream().forEach(grbgAccTemp -> {
//			searchChildGarbageAccounts(grbgAccTemp);
//		});
		
		return grbgAccs;
	}

//	private void searchChildGarbageAccounts(GarbageAccount grbgAccTemp) {
//		SearchCriteriaGarbageAccount searchCriteriaGarbageAccountNew = SearchCriteriaGarbageAccount.builder()
//				.parentId(Collections.singletonList(grbgAccTemp.getId()))
//				.build();
//		//search child garbage account
//		List<GarbageAccount> subAccs = repository.searchGarbageAccount(searchCriteriaGarbageAccountNew);
//		grbgAccTemp.setChildGarbageAccounts(subAccs);
//	}

	private void validateSearchGarbageAccount(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
		
		if(CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName()) &&
		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber()) &&
		        null == searchCriteriaGarbageAccount.getIsOwner()) {
//		        CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getParentId())) {
			throw new RuntimeException("Provide the parameters to search garbage accounts.");
		}
		
	}

}
