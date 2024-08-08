package com.example.hpgarbageservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.contract.workflow.ProcessInstance;
import com.example.hpgarbageservice.contract.workflow.ProcessInstanceRequest;
import com.example.hpgarbageservice.contract.workflow.ProcessInstanceResponse;
import com.example.hpgarbageservice.contract.workflow.WorkflowService;
import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.GarbageAccountResponse;
import com.example.hpgarbageservice.model.GrbgAddress;
import com.example.hpgarbageservice.model.GrbgApplication;
import com.example.hpgarbageservice.model.GrbgCollectionUnit;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccountRequest;
import com.example.hpgarbageservice.repository.GarbageAccountRepository;
import com.example.hpgarbageservice.repository.GrbgAddressRepository;
import com.example.hpgarbageservice.repository.GrbgApplicationRepository;
import com.example.hpgarbageservice.repository.GrbgCollectionUnitRepository;
import com.example.hpgarbageservice.repository.GrbgCommercialDetailsRepository;
import com.example.hpgarbageservice.repository.GrbgDocumentRepository;
import com.example.hpgarbageservice.repository.GrbgOldDetailsRepository;
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

	@Autowired
	private GrbgAddressRepository grbgAddressRepository;

	@Autowired
	private GrbgOldDetailsRepository grbgOldDetailsRepository;

	@Autowired
	private GrbgCollectionUnitRepository grbgCollectionUnitRepository;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private ApplicationPropertiesAndConstant applicationPropertiesAndConstant;

	public List<GarbageAccount> create(GarbageAccountRequest createGarbageRequest) {

		List<GarbageAccount> garbageAccountsResponse = new ArrayList<>();
		
		if (!CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())) {
			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {

				// validate and enrich
				validateAndEnrichCreateGarbageAccount(createGarbageRequest.getRequestInfo(), garbageAccount);

				// create garbage account
				garbageAccountsResponse.add(garbageAccountRepository.create(garbageAccount));
				
				// create garbage objects
				createGarbageAccountObjects(garbageAccount);
				
			});
		}
		
		// call workflow
		callWfUpdate(createGarbageRequest);
		
		return garbageAccountsResponse;
	}


	private void createGarbageAccountObjects(GarbageAccount garbageAccount) {
		// create garbage application
		grbgApplicationRepository.create(garbageAccount.getGrbgApplication());

		// create garbage address
		createGarbageAddress(garbageAccount);

		// create old garbage details
		createGarbageOldDetails(garbageAccount);

		// create garbage unit
		createGarbageUnit(garbageAccount);
		
		// enrich garbage document
//				enrichCreateGarbageDocuments(garbageAccount);

		// create garbage documents
//				createGarbageDocuments(garbageAccount);
	}


	private void validateAndEnrichCreateGarbageAccount(RequestInfo requestInfo,
			GarbageAccount garbageAccount) {
		// validate create garbage account
		validateGarbageAccount(garbageAccount);

		// enrich create garbage account
		enrichCreateGarbageAccount(garbageAccount, requestInfo);
				
		// enrich garbage address
		validateAndsEnrichCreateGarbageAddress(garbageAccount);

		// enrich create garbage application
		enrichCreateGarbageApplication(garbageAccount, requestInfo);

		// enrich old garbage details
		enrichCreateGarbageOldDetails(garbageAccount);

		// enrich garbage unit
		enrichCreateGarbageUnit(garbageAccount);
	}


	private void createGarbageUnit(GarbageAccount garbageAccount) {
		if(!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				unit.setIsActive(true);
				grbgCollectionUnitRepository.create(unit);
			});
		}
	}


	private void enrichCreateGarbageUnit(GarbageAccount garbageAccount) {

		if(!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				unit.setUuid(UUID.randomUUID().toString());
				unit.setGarbageId(garbageAccount.getGarbageId());
			});
		}
	}


	private void enrichCreateGarbageOldDetails(GarbageAccount garbageAccount) {
		if(null != garbageAccount.getGrbgOldDetails()) {
			garbageAccount.getGrbgOldDetails().setUuid(UUID.randomUUID().toString());
			garbageAccount.getGrbgOldDetails().setGarbageId(garbageAccount.getGarbageId());
		}
	}


	private void createGarbageOldDetails(GarbageAccount garbageAccount) {
		
		if(null != garbageAccount.getGrbgOldDetails()) {
			grbgOldDetailsRepository.create(garbageAccount.getGrbgOldDetails());
		}
		
	}


	private void createGarbageAddress(GarbageAccount garbageAccount) {

		if(!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getAddresses().stream().forEach(address -> {
				address.setIsActive(true);
				grbgAddressRepository.create(address);
			});
		}		
	}


	private void validateAndsEnrichCreateGarbageAddress(GarbageAccount garbageAccount) {
		if(!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getAddresses().stream().forEach(address -> {
				
				//validate address
				if(StringUtils.isEmpty(address.getAddress1())
						&& StringUtils.isEmpty(address.getAddress1())) {
					throw new RuntimeException("Provide mendatory details of address.");
				}
				
				// enrich address
				address.setUuid(UUID.randomUUID().toString());
				address.setGarbageId(garbageAccount.getGarbageId());
			});
		}
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
				.status(ApplicationPropertiesAndConstant.STATUS_INITIATED)
				.garbageId(garbageAccount.getGarbageId())
				.build();
		
		garbageAccount.setGrbgApplication(grbgApplication);
	}


	private void validateGarbageAccount(GarbageAccount garbageAccount) {

		// validate nullability
		if (null == garbageAccount
				|| null == garbageAccount.getMobileNumber()
				|| null == garbageAccount.getName()) {
//				|| null == garbageAccount.getType()
//				|| null == garbageAccount.getPropertyId()) {
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
		garbageAccount.setStatus(ApplicationPropertiesAndConstant.STATUS_INITIATED);
		garbageAccount.setWorkflowAction(ApplicationPropertiesAndConstant.WORKFLOW_ACTION_INITIATE);

	}

	private void enrichUpdateGarbageAccount(GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount, RequestInfo requestInfo, Map<String, String> applicationNumberToCurrentStatus) {

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
		
		if (null != newGarbageAccount.getGrbgApplication()) {
			newGarbageAccount.setStatus(
					applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo()));
			newGarbageAccount.getGrbgApplication().setStatus(
					applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo()));
		}
	}

	public List<GarbageAccount> update(GarbageAccountRequest updateGarbageRequest) {

		List<GarbageAccount> garbageAccountsResponse = new ArrayList<>();

		// validate garbage account request
		updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {
			validateGarbageAccount(account);
		});


		// call workflow
		ProcessInstanceResponse processInstanceResponse = callWfUpdate(updateGarbageRequest);
		Map<String, String> applicationNumberToCurrentStatus = processInstanceResponse.getProcessInstances().stream()
								.collect(Collectors.toMap(ProcessInstance::getBusinessId, instance -> instance.getState().getApplicationStatus()));
		
		
		// search existing garbage accounts
		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(updateGarbageRequest.getGarbageAccounts());
		Map<Long, GarbageAccount> existingGarbageAccountsMap = searchGarbageAccountMap(searchCriteriaGarbageAccount, updateGarbageRequest.getRequestInfo());
		
		if (!CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())) {
			updateGarbageRequest.getGarbageAccounts().forEach(newGarbageAccount -> {

				// get existing garbage account from map
				GarbageAccount existingGarbageAccount = existingGarbageAccountsMap
						.get(newGarbageAccount.getGarbageId());

			// update garbage account
				if (!newGarbageAccount.equals(existingGarbageAccount)) {
					updateGarbageAccount(updateGarbageRequest, newGarbageAccount, existingGarbageAccount, applicationNumberToCurrentStatus);
				}

			// update other objects of garbage account
				updateGarbageAccountObjects(newGarbageAccount, existingGarbageAccount, applicationNumberToCurrentStatus);
			
				garbageAccountsResponse.add(newGarbageAccount);
			});
		}
		
		return garbageAccountsResponse;
	}


	private ProcessInstanceResponse callWfUpdate(GarbageAccountRequest updateGarbageRequest) {
		
		ProcessInstanceResponse processInstanceResponse = null;
		
		if (!CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())) {
			
			ProcessInstanceRequest processInstanceRequest = null;
			List<ProcessInstance> processInstances = new ArrayList<>();
					
			updateGarbageRequest.getGarbageAccounts().forEach(newGarbageAccount -> {
				// build process instance request
				if (null != newGarbageAccount.getGrbgApplication()) {
					processInstances.add(ProcessInstance.builder().tenantId(newGarbageAccount.getTenantId())
							.businessService(applicationPropertiesAndConstant.WORKFLOW_BUSINESS_SERVICE)
							.moduleName(applicationPropertiesAndConstant.WORKFLOW_MODULE_NAME)
							.businessId(newGarbageAccount.getGrbgApplication().getApplicationNo())
							.action(newGarbageAccount.getWorkflowAction()).build());
				}
				
			});
			
			processInstanceRequest = ProcessInstanceRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
					.processInstances(processInstances).build();
			
			// call workflow
			processInstanceResponse = workflowService.callWf(processInstanceRequest);
			
		}

		return processInstanceResponse;
	}


	private void updateGarbageAccountObjects(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount, Map<String, String> applicationNumberToCurrentStatus) {
		
		// 1. update application
		if(null != newGarbageAccount.getGrbgApplication()
				&& !newGarbageAccount.getGrbgApplication().equals(existingGarbageAccount.getGrbgApplication()))
		{
			grbgApplicationRepository.update(newGarbageAccount.getGrbgApplication());
		}
		
		// 2. update commercial details
		if(null != newGarbageAccount.getGrbgCommercialDetails()
				&& StringUtils.isEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())) {
			//create commercial details
			grbgCommercialDetailsRepository.create(newGarbageAccount.getGrbgCommercialDetails());
		}
		else if(null != newGarbageAccount.getGrbgCommercialDetails()
				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())
				&& !newGarbageAccount.getGrbgCommercialDetails().equals(existingGarbageAccount.getGrbgCommercialDetails())){
			//update commercial details
			grbgCommercialDetailsRepository.update(newGarbageAccount.getGrbgCommercialDetails());
		}
		

		// 3. update grbgOldDetails
		if(null != newGarbageAccount.getGrbgOldDetails()
				&& StringUtils.isEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())) {
			//create grbgOldDetails
			grbgOldDetailsRepository.create(newGarbageAccount.getGrbgOldDetails());
		}
		else if(null != newGarbageAccount.getGrbgOldDetails()
				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())
				&& !newGarbageAccount.getGrbgOldDetails().equals(existingGarbageAccount.getGrbgOldDetails())){
			//update grbgOldDetails
			grbgOldDetailsRepository.update(newGarbageAccount.getGrbgOldDetails());
		}
		

		// 4. update grbgCollectionUnits
		updateGrbgCollectionUnits(newGarbageAccount, existingGarbageAccount);

		// 5. update grbgCollectionUnits
		updateGrbgAddress(newGarbageAccount, existingGarbageAccount);
		

		// 2. update bills
//				bills loop > make list of deleting, updating and creating bills
		
		
		
	}


	private void updateGrbgAddress(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount) {
	    // Identify addresses to deactivate
	    Map<String, GrbgAddress> grbgAddressesToDeactivate = existingGarbageAccount.getAddresses().stream()
	        .filter(existingAddress -> newGarbageAccount.getAddresses().stream()
	            .noneMatch(newAddress -> StringUtils.equals(existingAddress.getUuid(), newAddress.getUuid())))
	        .collect(Collectors.toMap(GrbgAddress::getUuid, existingAddress -> existingAddress));

	    // Deactivate grbgAddressesToDeactivate
	    grbgAddressesToDeactivate.values().forEach(grbgAddress -> {
	        grbgAddress.setIsActive(false);
	        grbgAddressRepository.update(grbgAddress);
	    });

	    // Update new GrbgAddresses
	    if (!CollectionUtils.isEmpty(newGarbageAccount.getAddresses())) {
	        newGarbageAccount.getAddresses().forEach(address -> {
	            grbgAddressRepository.update(address);
	        });
	    }
	}



	private void updateGrbgCollectionUnits(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount) {
		Map<String, GrbgCollectionUnit> grbgCollectionUnitsToDeactivate = existingGarbageAccount.getGrbgCollectionUnits().stream()
			    .filter(existingUnit -> newGarbageAccount.getGrbgCollectionUnits().stream()
			        .noneMatch(newUnit -> StringUtils.equals(existingUnit.getUuid(), newUnit.getUuid())))
			    .collect(Collectors.toMap(GrbgCollectionUnit::getUuid, existingUnit -> existingUnit));

		// deactivate grbgCollectionUnitsToDeactivate
		grbgCollectionUnitsToDeactivate.entrySet().stream().forEach(map -> {
			GrbgCollectionUnit grbgCollectionUnit = map.getValue();
			grbgCollectionUnit.setIsActive(false);
			grbgCollectionUnitRepository.update(grbgCollectionUnit);
		});
		
		// update new GrbgCollectionUnits
		if(!CollectionUtils.isEmpty(newGarbageAccount.getGrbgCollectionUnits())) {
			newGarbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				grbgCollectionUnitRepository.update(unit);
			});
		}
	}


	private void updateGarbageAccount(GarbageAccountRequest updateGarbageRequest, GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount, Map<String, String> applicationNumberToCurrentStatus) {
		
		// replicate existing grbg acc to history table

		// enrich new request
		enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount, updateGarbageRequest.getRequestInfo(), applicationNumberToCurrentStatus);

		// update garbage account
		garbageAccountRepository.update(newGarbageAccount);
		
	}

	private Map<Long, GarbageAccount> searchGarbageAccountMap(
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, RequestInfo requestInfo) {
		
		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest.builder()
				.searchCriteriaGarbageAccount(searchCriteriaGarbageAccount)
				.requestInfo(requestInfo)
				.build();
		
		GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteriaGarbageAccountRequest);
		
		Map<Long, GarbageAccount> existingGarbageAccountsMap = new HashMap<>();
		garbageAccountResponse.getGarbageAccounts().stream().forEach(account -> {
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


	public GarbageAccountResponse searchGarbageAccounts(SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		
		//validate search criteria
		validateAndSearchSearchGarbageAccount(searchCriteriaGarbageAccountRequest);
		
		//search garbage account
		List<GarbageAccount> grbgAccs = garbageAccountRepository.searchGarbageAccount(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());
		
//		//search child garbage accounts
//		grbgAccs.stream().forEach(grbgAccTemp -> {
//			searchChildGarbageAccounts(grbgAccTemp);
//		});
		
		GarbageAccountResponse garbageAccountResponse = getSearchResponseFromAccounts(grbgAccs);
		return garbageAccountResponse;
	}


	private GarbageAccountResponse getSearchResponseFromAccounts(List<GarbageAccount> grbgAccs) {
		
		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
				.garbageAccounts(grbgAccs)
				.build();
		
		processResponse(garbageAccountResponse);
		
		return garbageAccountResponse;
	}

//	private void searchChildGarbageAccounts(GarbageAccount grbgAccTemp) {
//		SearchCriteriaGarbageAccount searchCriteriaGarbageAccountNew = SearchCriteriaGarbageAccount.builder()
//				.parentId(Collections.singletonList(grbgAccTemp.getId()))
//				.build();
//		//search child garbage account
//		List<GarbageAccount> subAccs = repository.searchGarbageAccount(searchCriteriaGarbageAccountNew);
//		grbgAccTemp.setChildGarbageAccounts(subAccs);
//	}

	private void validateAndSearchSearchGarbageAccount(SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		RequestInfo requestInfo = searchCriteriaGarbageAccountRequest.getRequestInfo();
		
		if(null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
			if(CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getId()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getGarbageId()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getPropertyId()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getType()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getName()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getMobileNumber()) &&
			        null == searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getIsOwner()) {
	
					if(null != requestInfo && null != requestInfo.getUserInfo()
							&& StringUtils.equalsIgnoreCase(requestInfo.getUserInfo().getType(), "CITIZEN")) {
						searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setCreatedBy(Collections.singletonList(requestInfo.getUserInfo().getUuid()));
					}else {
						throw new RuntimeException("Provide the parameters to search garbage accounts.");
					}
			}
		}else if(null != requestInfo && null != requestInfo.getUserInfo()
				&& StringUtils.equalsIgnoreCase(requestInfo.getUserInfo().getType(), "CITIZEN")) {
			searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(
					SearchCriteriaGarbageAccount.builder().createdBy(Collections.singletonList(
									requestInfo.getUserInfo().getUuid())).build());
		}
		
	}
	


	public void processResponse(GarbageAccountResponse response) {
		
		// categorize each accounts
		if (!CollectionUtils.isEmpty(response.getGarbageAccounts())
				) {
			response.setApplicationInitiated((int) response.getGarbageAccounts().stream()
					.filter(account -> StringUtils.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_INITIATED, account.getStatus())).count());
			response.setApplicationApplied((int) response.getGarbageAccounts().stream()
					.filter(account -> StringUtils.equalsAnyIgnoreCase(account.getStatus()
							, applicationPropertiesAndConstant.STATUS_PENDINGFORVERIFICATION
							, applicationPropertiesAndConstant.STATUS_PENDINGFORAPPROVAL
							, applicationPropertiesAndConstant.STATUS_PENDINGFORMODIFICATION)).count());
			response.setApplicationPendingForPayment((int) response.getGarbageAccounts().stream()
					.filter(account -> StringUtils.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_PENDINGFORPAYMENT, account.getStatus())).count());
			response.setApplicationRejected((int) response.getGarbageAccounts().stream()
					.filter(account -> StringUtils.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_REJECTED, account.getStatus())).count());
			response.setApplicationApproved((int) response.getGarbageAccounts().stream()
					.filter(account -> StringUtils.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_APPROVED, account.getStatus()))
					.count());
		}
		
		
	}

}
