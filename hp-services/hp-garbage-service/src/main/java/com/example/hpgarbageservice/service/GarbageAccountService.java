package com.example.hpgarbageservice.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.repository.GarbageAccountRepository;

@Service
public class GarbageAccountService {

	@Autowired
	private GarbageAccountRepository repository;

	public GarbageAccount create(GarbageAccountRequest createGarbageRequest) {

		// validate create grbg act
		validateGarbageAccountRequest(createGarbageRequest);

		// enrich grbg act
		enrichCreateGarbageAccount(createGarbageRequest);

		// create grbg act
		return repository.create(createGarbageRequest.getGarbageAccount());
	}

	private void validateGarbageAccountRequest(GarbageAccountRequest createGarbageRequest) {

		if (null == createGarbageRequest.getGarbageAccount()
				&& null == createGarbageRequest.getGarbageAccount().getMobileNumber()
				&& null == createGarbageRequest.getGarbageAccount().getName()
				&& null == createGarbageRequest.getGarbageAccount().getType()
				&& null == createGarbageRequest.getGarbageAccount().getPropertyId()) {
			throw new RuntimeException("Provide garbage account details.");
		}

	}

	private void enrichCreateGarbageAccount(GarbageAccountRequest createGarbageRequest) {

		AuditDetails auditDetails = null;

		if (null != createGarbageRequest.getRequestInfo()
				&& null != createGarbageRequest.getRequestInfo().getUserInfo()) {
			auditDetails = AuditDetails.builder()
					.createdBy(createGarbageRequest.getRequestInfo().getUserInfo().getUuid())
					.createdDate(new Date().getTime())
					.lastModifiedBy(createGarbageRequest.getRequestInfo().getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
			createGarbageRequest.getGarbageAccount().setAuditDetails(auditDetails);
		}

		// generate garbage_id
		createGarbageRequest.getGarbageAccount().setGarbageId(System.currentTimeMillis());

	}

	private void enrichUpdateGarbageAccount(GarbageAccountRequest updateGarbageRequest,
			GarbageAccount existingGarbageAccount) {

		AuditDetails auditDetails = null;
		if (null != updateGarbageRequest.getRequestInfo()
				&& null != updateGarbageRequest.getRequestInfo().getUserInfo()) {
			auditDetails = AuditDetails.builder()
					.lastModifiedBy(updateGarbageRequest.getRequestInfo().getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
		}
		if (null != existingGarbageAccount.getAuditDetails()) {
			auditDetails.setCreatedBy(existingGarbageAccount.getAuditDetails().getCreatedBy());
			auditDetails.setCreatedDate(existingGarbageAccount.getAuditDetails().getCreatedDate());
		}

		updateGarbageRequest.getGarbageAccount().setAuditDetails(auditDetails);
		updateGarbageRequest.getGarbageAccount().setId(existingGarbageAccount.getId());
		updateGarbageRequest.getGarbageAccount().setGarbageId(existingGarbageAccount.getGarbageId());
	}

	public GarbageAccount update(GarbageAccountRequest updateGarbageRequest) {

		// search existing grbg acc
		GarbageAccount existingGarbageAccount = null;

		// validate existing and new grbg acc
		validateUpdateGarbageAccount(updateGarbageRequest, existingGarbageAccount);

		// replicate existing grbg acc to history table

		// enrich new request
		enrichUpdateGarbageAccount(updateGarbageRequest, existingGarbageAccount);

		// update garbage account
		repository.update(updateGarbageRequest.getGarbageAccount());
		return updateGarbageRequest.getGarbageAccount();
	}

	private void validateUpdateGarbageAccount(GarbageAccountRequest updateGarbageRequest,
			GarbageAccount existingGarbageAccount) {
		if (null == existingGarbageAccount) {
			throw new RuntimeException("Provided garbage account doesn't exist.");
		}
		// validate grbg acc req
		validateGarbageAccountRequest(updateGarbageRequest);
	}

	public List<GarbageAccount> searchGarbageAccounts(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
		
		//validate search criteria
		validateSearchGarbageAccount(searchCriteriaGarbageAccount);
		
		//search garbage account
		List<GarbageAccount> grbgAccs = repository.searchGarbageAccount(searchCriteriaGarbageAccount);
		
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
		grbgAccTemp.setGarbageAccounts(subAccs);
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
