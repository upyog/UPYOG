package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.garbageservice.contract.bill.*;
import org.egov.garbageservice.contract.bill.Bill.StatusEnum;
import org.egov.garbageservice.contract.workflow.BusinessServiceResponse;
import org.egov.garbageservice.contract.workflow.ProcessInstance;
import org.egov.garbageservice.contract.workflow.ProcessInstanceRequest;
import org.egov.garbageservice.contract.workflow.ProcessInstanceResponse;
import org.egov.garbageservice.contract.workflow.State;
import org.egov.garbageservice.contract.workflow.WorkflowService;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountActionRequest;
import org.egov.garbageservice.model.GarbageAccountActionResponse;
import org.egov.garbageservice.model.GarbageAccountDetail;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.GrbgAddress;
import org.egov.garbageservice.model.GrbgApplication;
import org.egov.garbageservice.model.GrbgCollectionUnit;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.repository.GarbageAccountRepository;
import org.egov.garbageservice.repository.GrbgAddressRepository;
import org.egov.garbageservice.repository.GrbgApplicationRepository;
import org.egov.garbageservice.repository.GrbgCollectionUnitRepository;
import org.egov.garbageservice.repository.GrbgCommercialDetailsRepository;
import org.egov.garbageservice.repository.GrbgDocumentRepository;
import org.egov.garbageservice.repository.GrbgOldDetailsRepository;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	private GrbgConstants applicationPropertiesAndConstant;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private DemandService demandService;

	@Autowired
	private BillService billService;;
	
	public GarbageAccountResponse create(GarbageAccountRequest createGarbageRequest) {

		List<GarbageAccount> garbageAccounts = new ArrayList<>();
		

		List<String> propertyIds = createGarbageRequest.getGarbageAccounts().stream().map(account -> account.getPropertyId()).collect(Collectors.toList());
		// search existing account
		List<GarbageAccount> existingAccounts = garbageAccountRepository.searchGarbageAccount(SearchCriteriaGarbageAccount.builder().propertyId(propertyIds).parentAccount(null).build());
		
		
		if (!CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())) {
			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {

				// validate and enrich
				validateAndEnrichCreateGarbageAccount(createGarbageRequest.getRequestInfo(), garbageAccount,
						existingAccounts);

			});

			// call workflow
			ProcessInstanceResponse processInstanceResponse = callWfUpdate(createGarbageRequest);
			
			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {

				// create garbage account
				garbageAccounts.add(garbageAccountRepository.create(garbageAccount));

				// create garbage objects
				createGarbageAccountObjects(garbageAccount);

			});
			
			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {
				if(!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
					garbageAccount.getChildGarbageAccounts().stream().forEach(subAccount -> {
						// create garbage sub account
						garbageAccountRepository.create(subAccount);
						// create garbage objects
						createGarbageAccountObjects(subAccount);
					});
				}
			});
		}
		
		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(createGarbageRequest.getRequestInfo(), false))
				.garbageAccounts(garbageAccounts)
				.build();
		if(!CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccountResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(createGarbageRequest.getRequestInfo(), true));
		}
		
		return garbageAccountResponse;
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
			GarbageAccount garbageAccount, List<GarbageAccount> existingAccounts) {
		// validate create garbage account
		validateGarbageAccount(garbageAccount, existingAccounts);

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

		// enrich garbage sub accounts
		enrichCreateGarbageSubAccounts(garbageAccount);

		// enrich garbage sub account unit
		enrichCreateSubGarbageAccountUnits(garbageAccount);

		// enrich garbage sub account application
		enrichCreateSubGarbageAccountAddress(garbageAccount);

		// enrich garbage sub account address
		enrichCreateSubGarbageAccountApplication(garbageAccount);
	}
	
	

	private void enrichCreateSubGarbageAccountApplication(GarbageAccount garbageAccount) {
		
		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
			
			AtomicLong childCount = new AtomicLong(1L);
			garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {
				GrbgApplication grbgApplication = GrbgApplication.builder()
						.uuid(UUID.randomUUID().toString())
						.applicationNo(garbageAccount.getGrbgApplication().getApplicationNo().concat("/").concat(Long.toString(childCount.getAndIncrement())))
						.status(GrbgConstants.STATUS_INITIATED)
						.garbageId(subAccount.getGarbageId())
						.build();
				
				subAccount.setGrbgApplication(grbgApplication);
			});
		}
		
		
	}


	private void enrichCreateSubGarbageAccountAddress(GarbageAccount garbageAccount) {

		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())
				&& !CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {
				
				List<GrbgAddress> grbgAddresses = new ArrayList<>();
						for(GrbgAddress tempG : garbageAccount.getAddresses()) {
							grbgAddresses.add(objectMapper.convertValue(tempG, GrbgAddress.class));
						}
				subAccount.setAddresses(grbgAddresses);
				subAccount.getAddresses().stream().forEach(address -> {
					address.setUuid(UUID.randomUUID().toString());
//					address.setIsActive(true);
					address.setGarbageId(subAccount.getGarbageId());
//					address.setAddress1(garbageAccount.getAddresses().get(0).getAddress1());
//					address.setAddress2(garbageAccount.getAddresses().get(0).getAddress2());
//					address.setCity(garbageAccount.getAddresses().get(0).getCity());
//					address.setState(garbageAccount.getAddresses().get(0).getState());
//					address.setPincode(garbageAccount.getAddresses().get(0).getPincode());
//					address.setZone(garbageAccount.getAddresses().get(0).getZone());
//					address.setUlbName(garbageAccount.getAddresses().get(0).getUlbName());
//					address.setUlbType(garbageAccount.getAddresses().get(0).getUlbType());
//					address.setWardName(garbageAccount.getAddresses().get(0).getWardName());
//					address.setAdditionalDetail(garbageAccount.getAddresses().get(0).getAdditionalDetail());
				});
			});
		}
	}


	private void enrichCreateSubGarbageAccountUnits(GarbageAccount garbageAccount) {

		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
			garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {
//				if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
				subAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
						unit.setUuid(UUID.randomUUID().toString());
						unit.setIsActive(true);
						unit.setGarbageId(subAccount.getGarbageId());
					});
//				}
			});
		}
	}


	private void enrichCreateGarbageSubAccounts(GarbageAccount garbageAccount) {
		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
			AtomicInteger counter = new AtomicInteger(1);
			garbageAccount.getChildGarbageAccounts().stream().forEach(subAccount -> {
				subAccount.setId(garbageAccountRepository.getNextSequence());
				subAccount.setUuid(UUID.randomUUID().toString());
				subAccount.setPropertyId(garbageAccount.getPropertyId());
				subAccount.setTenantId(garbageAccount.getTenantId());
//				subAccount.setIsOwner(false);
				subAccount.setGarbageId(garbageAccount.getGarbageId()+(counter.getAndAdd(1)));
				subAccount.setStatus(GrbgConstants.STATUS_INITIATED);
				subAccount.setWorkflowAction(GrbgConstants.WORKFLOW_ACTION_INITIATE);
				subAccount.setAuditDetails(garbageAccount.getAuditDetails());
				subAccount.setParentAccount(garbageAccount.getUuid());
				subAccount.setIsActive(true);
			});
			garbageAccount.setSubAccountCount((long) counter.get());
		}
	}


	private void createGarbageUnit(GarbageAccount garbageAccount) {
		if(!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				grbgCollectionUnitRepository.create(unit);
			});
		}
	}


	private void enrichCreateGarbageUnit(GarbageAccount garbageAccount) {

		if(!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				unit.setUuid(UUID.randomUUID().toString());
				unit.setIsActive(true);
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
				grbgAddressRepository.create(address);
			});
		}
	}


	private void validateAndsEnrichCreateGarbageAddress(GarbageAccount garbageAccount) {
		if(!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getAddresses().stream().forEach(address -> {
				
				//validate address
				if(StringUtils.isEmpty(address.getAddress1())
						|| null == address.getAdditionalDetail()
						|| null == address.getAdditionalDetail().get("district")) {
					throw new CustomException("MISSING_ADDRESS_DETAILS","Provide mendatory details of address.");
				}
				
				// enrich address
				address.setUuid(UUID.randomUUID().toString());
				address.setIsActive(true);
				address.setGarbageId(garbageAccount.getGarbageId());
			});
		}else {
			throw new CustomException("MISSING_ADDRESS","Provide address.");
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
			if(StringUtils.equalsIgnoreCase(doc.getDocCategory(), GrbgConstants.DOCUMENT_ACCOUNT)) {
				doc.setTblRefUuid(garbageAccount.getUuid());
			}
		});
		
	}


	private void enrichCreateGarbageApplication(GarbageAccount garbageAccount, RequestInfo requestInfo) {
		
		// get application number format
		String applicationNumber = GrbgConstants.generateApplicationNumberFormat(String.valueOf(garbageAccount.getId())
													, garbageAccount.getAddresses().get(0).getUlbName()
													, garbageAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText());
		
		GrbgApplication grbgApplication = GrbgApplication.builder()
				.uuid(UUID.randomUUID().toString())
				.applicationNo(applicationNumber)
				.status(GrbgConstants.STATUS_INITIATED)
				.garbageId(garbageAccount.getGarbageId())
				.build();
		
		garbageAccount.setGrbgApplication(grbgApplication);
	}


	private void validateGarbageAccount(GarbageAccount garbageAccount, List<GarbageAccount> existingAccounts) {

		// validate nullability
		if (null == garbageAccount
				|| null == garbageAccount.getMobileNumber()
				|| null == garbageAccount.getName()) {
//				|| null == garbageAccount.getType()
//				|| null == garbageAccount.getPropertyId()) {
			throw new CustomException("MISSING_GARBAGE_ACCOUNT_DETAILS","Provide garbage account details.");
		}
		
		// validate duplicate owner with same properyId
		
//		if(BooleanUtils.isTrue(duplicateOwner)) {
//			throw new CustomException("DUPLICATE_OWNER","Duplicate Owner Found for given property.");
//		}
		if(StringUtils.isEmpty(garbageAccount.getUuid())			// create account condition
				&& !CollectionUtils.isEmpty(existingAccounts)) {
			throw new CustomException("DUPLICATE_OWNER","Can't create Duplicate Owner for given property which is already present.");
		}else 
			if(StringUtils.isNotEmpty(garbageAccount.getUuid()))	// update account condition
		{
			List<GarbageAccount> existingAccounts1 = existingAccounts.stream().filter(account -> StringUtils.equals(garbageAccount.getUuid(), account.getUuid())).collect(Collectors.toList());
			
			if(CollectionUtils.isEmpty(existingAccounts1)) {
				throw new CustomException("GARBAGE_ACCOUNT_NOT_FOUND","Not able to find garbage account.");
			}else if(existingAccounts1.size()>1) {
				throw new CustomException("DUPLICATE_GARBAGE_ACCOUNT_FOUND","Duplicate Garbage account found.");
			}
			if(!StringUtils.isEmpty(existingAccounts1.get(0).getPropertyId())
					&& !StringUtils.equals(existingAccounts1.get(0).getPropertyId(), garbageAccount.getPropertyId())) {
				throw new CustomException("NO_DATA_CAN_BE_CHANGE","Some of the data is not matching and can't be updated.");
			}
			
			
			if(CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
				// validate child garbage account
				garbageAccount.getChildGarbageAccounts().stream().forEach(childAcc -> {
					Optional<GarbageAccount> matchingChildAccount = existingAccounts1.get(0).getChildGarbageAccounts().stream().filter(
							existingChildAcc -> StringUtils.equals(existingChildAcc.getUuid(), childAcc.getUuid()))
							.findFirst();
					if(!matchingChildAccount.isPresent()) {
						throw new CustomException("CHILD_GARBAGE_ACCOUNT_NOT_FOUND", "Provide correct uuid for child garbage account.");
					}
				});
			}
			
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
		garbageAccount.setId(garbageAccountRepository.getNextSequence());
		garbageAccount.setUuid(UUID.randomUUID().toString());
		garbageAccount.setGarbageId(System.currentTimeMillis());
		garbageAccount.setStatus(GrbgConstants.STATUS_INITIATED);
		garbageAccount.setWorkflowAction(GrbgConstants.WORKFLOW_ACTION_INITIATE);
		garbageAccount.setParentAccount(null);
		garbageAccount.setIsActive(true);
		garbageAccount.setSubAccountCount(Optional.ofNullable(garbageAccount.getChildGarbageAccounts())
	            .map(List::size).map(Integer::longValue)
	            .orElse(0L));

	}

	private void enrichUpdateGarbageAccount(GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount, RequestInfo requestInfo, Map<String, String> applicationNumberToCurrentStatus) {

		AuditDetails auditDetails = AuditDetails.builder().build();
		if (null != requestInfo
				&& null != requestInfo.getUserInfo()) {
			auditDetails.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
			auditDetails.setLastModifiedDate(new Date().getTime());
		}
		if (null != existingGarbageAccount.getAuditDetails()) {
			auditDetails.setCreatedBy(existingGarbageAccount.getAuditDetails().getCreatedBy());
			auditDetails.setCreatedDate(existingGarbageAccount.getAuditDetails().getCreatedDate());
		}

		// enrich parent account
		newGarbageAccount.setAuditDetails(auditDetails);
		newGarbageAccount.setId(existingGarbageAccount.getId());
		newGarbageAccount.setGarbageId(existingGarbageAccount.getGarbageId());
		
		
		// enrich child accounts
		if(!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
			
			newGarbageAccount.getChildGarbageAccounts().stream().forEach(childAccount -> {
				
				// update case
				if(StringUtils.isNotEmpty(childAccount.getUuid())) {

					Optional<GarbageAccount> matchingChildAccount = existingGarbageAccount.getChildGarbageAccounts().stream().filter(
							existingChildAcc -> StringUtils.equals(existingChildAcc.getUuid(), childAccount.getUuid()))
							.findFirst();
					
					childAccount.setAuditDetails(AuditDetails.builder()
							.createdBy(matchingChildAccount.get().getAuditDetails().getCreatedBy())
							.createdDate(matchingChildAccount.get().getAuditDetails().getCreatedDate())
							.lastModifiedBy(auditDetails.getLastModifiedBy())
							.lastModifiedDate(auditDetails.getLastModifiedDate()).build());
				}else {
					// create case
					childAccount.setAuditDetails(AuditDetails.builder()
							.createdBy(auditDetails.getCreatedBy())
							.createdDate(new Date().getTime()).build());
				}
				
			});
		}
		
//		if (null != newGarbageAccount.getGrbgApplication()) {
//			
////			newGarbageAccount.setStatus(
////					applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo()));
//		}
	}

	public GarbageAccountResponse update(GarbageAccountRequest updateGarbageRequest) {

		List<GarbageAccount> garbageAccounts = new ArrayList<>();


		// search existing garbage accounts
		Map<Long, GarbageAccount> existingGarbageIdAccountsMap;
		Map<String, GarbageAccount> existingGarbageApplicationAccountsMap;
		try {
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(updateGarbageRequest.getGarbageAccounts());
			existingGarbageIdAccountsMap = searchGarbageAccountMap(searchCriteriaGarbageAccount, updateGarbageRequest.getRequestInfo());
			existingGarbageApplicationAccountsMap = existingGarbageIdAccountsMap.entrySet().stream()
					.collect(Collectors.toMap(a -> a.getValue().getGrbgApplication().getApplicationNo(), b -> b.getValue()));
		} catch (Exception e) {
			throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS","Search Garbage account details failed.");
		}
		
		
		// load garbage account from backend if workflow = true
		GarbageAccountRequest garbageAccountRequest = loadUpdateGarbageAccountRequestFromMap(updateGarbageRequest, existingGarbageApplicationAccountsMap);
		
		
		// call workflow
		ProcessInstanceResponse processInstanceResponse = callWfUpdate(garbageAccountRequest);
		Map<String, String> applicationNumberToCurrentStatus = processInstanceResponse.getProcessInstances().stream()
								.collect(Collectors.toMap(ProcessInstance::getBusinessId, instance -> instance.getState().getApplicationStatus()));
		
		
		// update garbage account
		if (!CollectionUtils.isEmpty(garbageAccountRequest.getGarbageAccounts())) {
			garbageAccountRequest.getGarbageAccounts().stream()
			.forEach(newGarbageAccount -> {

//				if(!newGarbageAccount.getIsOnlyWorkflowCall()) {
					// validate garbage account request
					validateGarbageAccount(newGarbageAccount, existingGarbageIdAccountsMap.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList()));
					
			});
			
			
			garbageAccountRequest.getGarbageAccounts().stream().forEach(newGarbageAccount -> {

				// get existing garbage account from map
				GarbageAccount existingGarbageAccount = existingGarbageIdAccountsMap.get(newGarbageAccount.getGarbageId());

				// enrich garbage account
				enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount, updateGarbageRequest.getRequestInfo(), applicationNumberToCurrentStatus);

				// update garbage account
				if (!newGarbageAccount.equals(existingGarbageAccount)) {
					updateGarbageAccount(updateGarbageRequest, newGarbageAccount, existingGarbageAccount, applicationNumberToCurrentStatus);
				}

				// update other objects of garbage account
				updateAndEnrichGarbageAccountObjects(newGarbageAccount, existingGarbageAccount,
						applicationNumberToCurrentStatus);

				garbageAccounts.add(newGarbageAccount);
			});
			
		}
		
		
		// generate demand and fetch bill
		generateDemandAndBill(garbageAccountRequest);
		
		// RESPONSE builder
		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), false))
				.garbageAccounts(garbageAccounts)
				.build();
		if(!CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccountResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), true));
		}
		
		return garbageAccountResponse;
	}




	private void generateDemandAndBill(GarbageAccountRequest updateGarbageRequest) {
		updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {
			
			if(StringUtils.equalsIgnoreCase(GrbgConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, account.getWorkflowAction())) {
				
				List<Demand> savedDemands = new ArrayList<>();
            	// generate demand
				savedDemands = demandService.generateDemand(updateGarbageRequest.getRequestInfo(), account, GrbgConstants.BUSINESS_SERVICE);
	            

		        if(CollectionUtils.isEmpty(savedDemands)) {
		            throw new CustomException("INVALID_CONSUMERCODE","Bill not generated due to no Demand found for the given consumerCode");
		        }

				// fetch/create bill
	            GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
	            									.tenantId(account.getTenantId())
	            									.businessService(applicationPropertiesAndConstant.BUSINESS_SERVICE)
	            									.consumerCode(account.getGrbgApplicationNumber()).build();
	            BillResponse billResponse = billService.generateBill(updateGarbageRequest.getRequestInfo(),billCriteria);
	            
			}
		});
	}


	private GarbageAccountRequest loadUpdateGarbageAccountRequestFromMap(GarbageAccountRequest updateGarbageRequest,
			Map<String, GarbageAccount> existingGarbageApplicationAccountsMap) {
		

		GarbageAccountRequest garbageAccountRequestTemp = GarbageAccountRequest.builder()
				.requestInfo(updateGarbageRequest.getRequestInfo())
				.garbageAccounts(new ArrayList<>())
				.build();
		
		updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {
			
			
			if(BooleanUtils.isTrue(account.getIsOnlyWorkflowCall())) {

				Boolean tempBol = account.getIsOnlyWorkflowCall();
				String tempApplicationNo = account.getGrbgApplicationNumber();
				String action = account.getWorkflowAction();
				String status = getStatusOrAction(action, true);
				String comment = account.getWorkflowComment();

				GarbageAccount accountTemp = objectMapper.convertValue(existingGarbageApplicationAccountsMap.get(account.getGrbgApplicationNumber()), GarbageAccount.class);
				if(null == accountTemp) {
					throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS","Garbage Account not found to run workflow.");
				}
				
				accountTemp.setIsOnlyWorkflowCall(tempBol);
				accountTemp.setGrbgApplicationNumber(tempApplicationNo);
				accountTemp.setWorkflowAction(action);
				accountTemp.setWorkflowComment(comment);
				accountTemp.setStatus(status);
				accountTemp.getGrbgApplication().setStatus(status);
//				accountTemp.setChildGarbageAccounts(null);			// at a time only 1 app no provided for WF
				if(!CollectionUtils.isEmpty(accountTemp.getChildGarbageAccounts())) {
					accountTemp.getChildGarbageAccounts().stream().forEach(child -> {
						child.setWorkflowAction(action);
						child.setStatus(status);
					});
				}
				
				garbageAccountRequestTemp.getGarbageAccounts().add(accountTemp);
			}else if(StringUtils.equals(account.getWorkflowAction(), GrbgConstants.WORKFLOW_ACTION_INITIATE)){
				// this block will work only when update Account and action is INITIATE
				GarbageAccount accountTemp = objectMapper.convertValue(existingGarbageApplicationAccountsMap.get(account.getGrbgApplication().getApplicationNo()), GarbageAccount.class);
				if(null == accountTemp) {
					throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS","Garbage Account not found to update.");
				}
				account.setGrbgApplication(accountTemp.getGrbgApplication());
				garbageAccountRequestTemp.getGarbageAccounts().add(account);
			}else {
				throw new CustomException("WRONG_INPUTS","Input fields for workflow flag and action is incorrect.");
			}
			
			
		});
		

		return garbageAccountRequestTemp;
	}


	public String getStatusOrAction(String action, Boolean fetchValue) {
		
		Map<String, String> map = new HashMap<>();
		
		map.put(GrbgConstants.WORKFLOW_ACTION_INITIATE, GrbgConstants.STATUS_INITIATED);
        map.put(GrbgConstants.WORKFLOW_ACTION_FORWARD_TO_VERIFIER, GrbgConstants.STATUS_PENDINGFORVERIFICATION);
        map.put(GrbgConstants.WORKFLOW_ACTION_VERIFY, GrbgConstants.STATUS_PENDINGFORAPPROVAL);
        map.put(GrbgConstants.WORKFLOW_ACTION_APPROVE, GrbgConstants.STATUS_APPROVED);
        map.put(GrbgConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, GrbgConstants.STATUS_PENDINGFORPAYMENT);
        map.put(GrbgConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR, GrbgConstants.STATUS_PENDINGFORMODIFICATION);
        map.put(GrbgConstants.WORKFLOW_ACTION_FORWARD_TO_APPROVER, GrbgConstants.STATUS_PENDINGFORAPPROVAL);
        map.put(GrbgConstants.STATUS_APPROVED, GrbgConstants.STATUS_APPROVED);
		
		if(!fetchValue){
			// return key
			for (Map.Entry<String, String> entry : map.entrySet()) {
		        if (entry.getValue().equals(action)) {
		            return entry.getKey();
		        }
		    }
		}
		// return value
		return map.get(action);
	}


	private ProcessInstanceResponse callWfUpdate(GarbageAccountRequest updateGarbageRequest) {
		
		ProcessInstanceResponse processInstanceResponse = null;
		
		if (!CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())) {
			
			ProcessInstanceRequest processInstanceRequest = null;
			List<ProcessInstance> processInstances = new ArrayList<>();
					
			updateGarbageRequest.getGarbageAccounts().forEach(newGarbageAccount -> {
				
				ProcessInstance parentProcessInstance = ProcessInstance.builder().tenantId(newGarbageAccount.getTenantId())
						.businessService(applicationPropertiesAndConstant.WORKFLOW_BUSINESS_SERVICE)
						.moduleName(applicationPropertiesAndConstant.WORKFLOW_MODULE_NAME)
						.businessId(newGarbageAccount.getGrbgApplication().getApplicationNo())
						.action(null != newGarbageAccount.getWorkflowAction() ? newGarbageAccount.getWorkflowAction() : getStatusOrAction(newGarbageAccount.getStatus(), false))
						.comment(newGarbageAccount.getWorkflowComment()).build();
				
				processInstances.add(parentProcessInstance);
				
				if(!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
					newGarbageAccount.getChildGarbageAccounts().stream().forEach(subAccount -> {
						ProcessInstance subProcessInstance = ProcessInstance.builder().tenantId(subAccount.getTenantId())
								.businessService(applicationPropertiesAndConstant.WORKFLOW_BUSINESS_SERVICE)
								.moduleName(applicationPropertiesAndConstant.WORKFLOW_MODULE_NAME)
								.businessId(subAccount.getGrbgApplication().getApplicationNo())
								.action(null != subAccount.getWorkflowAction() ? subAccount.getWorkflowAction() : getStatusOrAction(subAccount.getStatus(), false))
								.comment(subAccount.getWorkflowComment()).build();
						
						processInstances.add(subProcessInstance);
					});
				}
				
				
//				// build process instance request
//				if(BooleanUtils.isTrue(newGarbageAccount.getIsOnlyWorkflowCall())) {
//					processInstances.add(ProcessInstance.builder().tenantId(newGarbageAccount.getTenantId())
//							.businessService(applicationPropertiesAndConstant.WORKFLOW_BUSINESS_SERVICE)
//							.moduleName(applicationPropertiesAndConstant.WORKFLOW_MODULE_NAME)
//							.businessId(newGarbageAccount.getGrbgApplicationNumber())
//							.action(newGarbageAccount.getWorkflowAction())
//							.comment(newGarbageAccount.getWorkflowComment()).build());
//					
//				}else if (null != newGarbageAccount.getGrbgApplication()) {			// create garbage account case
//					
//					processInstances.add(ProcessInstance.builder().tenantId(newGarbageAccount.getTenantId())
//							.businessService(applicationPropertiesAndConstant.WORKFLOW_BUSINESS_SERVICE)
//							.moduleName(applicationPropertiesAndConstant.WORKFLOW_MODULE_NAME)
//							.businessId(newGarbageAccount.getGrbgApplication().getApplicationNo())
//							.action(null != newGarbageAccount.getWorkflowAction() ? newGarbageAccount.getWorkflowAction() : getStatusOrAction(newGarbageAccount.getStatus(), false))
//							.comment(newGarbageAccount.getWorkflowComment()).build());
//				}
				
			});
			
			processInstanceRequest = ProcessInstanceRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
					.processInstances(processInstances).build();
			
			// call workflow
			processInstanceResponse = workflowService.callWf(processInstanceRequest);
			
		}

		return processInstanceResponse;
	}


	private void updateAndEnrichGarbageAccountObjects(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount, Map<String, String> applicationNumberToCurrentStatus) {
		
		// 1. update application
		if(null != newGarbageAccount.getGrbgApplication()
				&& !newGarbageAccount.getGrbgApplication().equals(existingGarbageAccount.getGrbgApplication()))
		{
			// enrich application
			newGarbageAccount.getGrbgApplication().setUuid(existingGarbageAccount.getGrbgApplication().getUuid());
			newGarbageAccount.getGrbgApplication().setStatus(applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo()));
			// update application
			grbgApplicationRepository.update(newGarbageAccount.getGrbgApplication());
		}
		
		// 2. update commercial details
		if(null != newGarbageAccount.getGrbgCommercialDetails()
				&& StringUtils.isEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())) {
			//create commercial details
			grbgCommercialDetailsRepository.create(newGarbageAccount.getGrbgCommercialDetails());
		}
		else 
		if(null != newGarbageAccount.getGrbgCommercialDetails()
				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())
				&& !newGarbageAccount.getGrbgCommercialDetails().equals(existingGarbageAccount.getGrbgCommercialDetails())){
			// enrich
//			newGarbageAccount.getGrbgCommercialDetails().setUuid(existingGarbageAccount.getGrbgCommercialDetails().getUuid());
			newGarbageAccount.getGrbgCommercialDetails().setGarbageId(existingGarbageAccount.getGrbgCommercialDetails().getGarbageId());
			//update commercial details
			grbgCommercialDetailsRepository.update(newGarbageAccount.getGrbgCommercialDetails());
		}
		

		// 3. update grbgOldDetails
		if(null != newGarbageAccount.getGrbgOldDetails()
				&& StringUtils.isEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())) {
			//create grbgOldDetails
			grbgOldDetailsRepository.create(newGarbageAccount.getGrbgOldDetails());
		}
		else 
		if(null != newGarbageAccount.getGrbgOldDetails()
				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())
				&& !newGarbageAccount.getGrbgOldDetails().equals(existingGarbageAccount.getGrbgOldDetails())){
			// enrich
			newGarbageAccount.getGrbgOldDetails().setUuid(existingGarbageAccount.getGrbgOldDetails().getUuid());
			newGarbageAccount.getGrbgOldDetails().setGarbageId(existingGarbageAccount.getGrbgOldDetails().getGarbageId());
			//update grbgOldDetails
			grbgOldDetailsRepository.update(newGarbageAccount.getGrbgOldDetails());
		}
		

		// 4. update grbgCollectionUnits
		updateGrbgCollectionUnits(newGarbageAccount, existingGarbageAccount);

		// 5. update grbgAddresses
		updateGrbgAddress(newGarbageAccount, existingGarbageAccount);
		
		// 6. update child garbage account
		updateChildGarbageAccounts(newGarbageAccount);
		
		// 2. update bills
//				bills loop > make list of deleting, updating and creating bills
		
		
		
	}


	private void updateChildGarbageAccounts(GarbageAccount newGarbageAccount) {
		if(!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
			newGarbageAccount.getChildGarbageAccounts().stream().forEach(child -> {
				garbageAccountRepository.update(child);
			});	
		}
	}


	private void enrichChildGarbageAccounts(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount) {
		
		enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount, null, null);
		
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
		List<String> applicationNos = new ArrayList<>();
		
		garbageAccounts.stream().forEach(grbgAcc -> {
//			if(null != grbgAcc.getId() && 0 <= grbgAcc.getId()) {
//				ids.add(grbgAcc.getId());
//			}
			if(null != grbgAcc.getGarbageId() && 0 <= grbgAcc.getGarbageId()) {
				garbageIds.add(grbgAcc.getGarbageId());
			}
			if(!StringUtils.isEmpty(grbgAcc.getGrbgApplicationNumber())) {
				applicationNos.add(grbgAcc.getGrbgApplicationNumber());
			}
		});
		

		if (!CollectionUtils.isEmpty(applicationNos)) {
			searchCriteriaGarbageAccount.setApplicationNumber(applicationNos);
		}
		if (!CollectionUtils.isEmpty(garbageIds)) {
			searchCriteriaGarbageAccount.setGarbageId(garbageIds);
		}
		
		
		return searchCriteriaGarbageAccount;
	}


	public GarbageAccountResponse searchGarbageAccounts(SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		
		//validate search criteria
		validateAndEnrichSearchGarbageAccount(searchCriteriaGarbageAccountRequest);
		
		//search garbage account
		List<GarbageAccount> grbgAccs = garbageAccountRepository.searchGarbageAccount(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());
		
//		//search child garbage accounts
//		grbgAccs.stream().forEach(grbgAccTemp -> {
//			searchChildGarbageAccounts(grbgAccTemp);
//		});
		
		GarbageAccountResponse garbageAccountResponse = getSearchResponseFromAccounts(grbgAccs);
		
		if(CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
			garbageAccountResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo(), false));
		}else {
			garbageAccountResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo(), true));
		}
		
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

	private void validateAndEnrichSearchGarbageAccount(SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		RequestInfo requestInfo = searchCriteriaGarbageAccountRequest.getRequestInfo();
		
		
		if(null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
			if(CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getId()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getGarbageId()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getPropertyId()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getType()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getName()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getMobileNumber()) &&
			        CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getApplicationNumber()) &&
			        null == searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getIsOwner()) {
	
					if(null != requestInfo && null != requestInfo.getUserInfo()
							&& StringUtils.equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_CITIZEN)) {
						searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setCreatedBy(Collections.singletonList(requestInfo.getUserInfo().getUuid()));
					}else if(null != requestInfo && null != requestInfo.getUserInfo()
							&& StringUtils.equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_EMPLOYEE)) {
						
						List<String> listOfStatus = getAccountStatusListByRoles(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getTenantId(), requestInfo.getUserInfo().getRoles());
						if(CollectionUtils.isEmpty(listOfStatus)) {
							throw new CustomException("SEARCH_ACCOUNT_BY_ROLES","Search can't be performed by this Employee due to lack of roles.");
						}
						searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setStatus(listOfStatus);
					}else {
						throw new CustomException("MISSING_SEARCH_PARAMETER","Provide the parameters to search garbage accounts.");
					}
			}
		}else if(null != requestInfo && null != requestInfo.getUserInfo()
				&& StringUtils.equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_CITIZEN)) {
			searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(
					SearchCriteriaGarbageAccount.builder().createdBy(Collections.singletonList(
									requestInfo.getUserInfo().getUuid())).build());
		}
		
	}
	
	private List<String> getAccountStatusListByRoles(String tenantId, List<Role> roles) {
	
	List<String> rolesWithinTenant = getRolesByTenantId(tenantId, roles);	
	Set<String> statusWithRoles = new HashSet();
	
	rolesWithinTenant.stream().forEach(role -> {
		
		if(StringUtils.equalsIgnoreCase(role, GrbgConstants.USER_ROLE_GB_VERIFIER)) {
			statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORVERIFICATION);
		}else if(StringUtils.equalsIgnoreCase(role, GrbgConstants.USER_ROLE_GB_APPROVER)) {
			statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORAPPROVAL);
		}
		
	});
	
	return new ArrayList<>(statusWithRoles);
}


	private List<String> getRolesByTenantId(String tenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
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


	public GarbageAccountActionResponse getApplicationDetails(GarbageAccountActionRequest garbageAccountActionRequest) {
		
		SearchCriteriaGarbageAccount criteria = SearchCriteriaGarbageAccount.builder().build();
		GarbageAccountActionResponse garbageAccountActionResponse = GarbageAccountActionResponse.builder()
				.applicationDetails(new ArrayList<>())
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(garbageAccountActionRequest.getRequestInfo(), true))
				.build();

		if(CollectionUtils.isEmpty(garbageAccountActionRequest.getApplicationNumbers())) {
			if(null != garbageAccountActionRequest.getRequestInfo()
					&& null != garbageAccountActionRequest.getRequestInfo().getUserInfo()
					&& !StringUtils.isEmpty(garbageAccountActionRequest.getRequestInfo().getUserInfo().getUuid())) {
				criteria.setCreatedBy(Collections.singletonList(garbageAccountActionRequest.getRequestInfo().getUserInfo().getUuid()));
			}else {
				throw new CustomException("INVALID REQUEST","Provide Application Number.");
			}
		}else {
			criteria.setApplicationNumber(garbageAccountActionRequest.getApplicationNumbers());
		}
		
		// search application number
		List<GarbageAccount> accounts = garbageAccountRepository.searchGarbageAccount(criteria);

		List<GarbageAccountDetail> applicationDetails = getApplicationBillUserDetail(accounts, garbageAccountActionRequest.getRequestInfo());
		
		garbageAccountActionResponse.setApplicationDetails(applicationDetails);
		
		return garbageAccountActionResponse;
	}


	private List<GarbageAccountDetail> getApplicationBillUserDetail(List<GarbageAccount> accounts, RequestInfo requestInfo) {
		
		List<GarbageAccountDetail> garbageAccountDetails = new ArrayList<>();
		
		accounts.stream().forEach(account -> {
			GarbageAccountDetail garbageAccountDetail = GarbageAccountDetail.builder().applicationNumber(account.getGrbgApplication().getApplicationNo()).build();
			
			// search bill Details
			BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
					.tenantId(account.getTenantId())
					.consumerCode(Collections.singleton(account.getGrbgApplication().getApplicationNo()))
					.service("GB")// business service
					.build();
			BillResponse billResponse = billService.searchBill(billSearchCriteria,requestInfo);
			Map<Object, Object> billDetailsMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(billResponse.getBill())) {
				// enrich all bills
				garbageAccountDetail.setBills(billResponse.getBill());
				Optional<Bill> activeBill = billResponse.getBill().stream()
						.filter(bill -> StatusEnum.ACTIVE.name().equalsIgnoreCase(bill.getStatus().name()))
			            .findFirst();
				activeBill.ifPresent(bill -> {
					// enrich active bill details
					billDetailsMap.put("billId", bill.getId());
					garbageAccountDetail.setTotalPayableAmount(bill.getTotalAmount());
				});
					
			}else {
				garbageAccountDetail.setTotalPayableAmount(new BigDecimal(100.00));
			}
			garbageAccountDetail.setBillDetails(billDetailsMap);
			
			
			// enrich formula
			if(!CollectionUtils.isEmpty(account.getGrbgCollectionUnits())) {
				garbageAccountDetail.setFeeCalculationFormula("category: ("+account.getGrbgCollectionUnits().get(0).getCategory()+"), SubCategory: ("+account.getGrbgCollectionUnits().get(0).getSubCategory()+")");
			}
			
			
			// enrich userDetails
			Map<Object, Object> userDetails = new HashMap<>();
			userDetails.put("UserName", account.getName());
			userDetails.put("MobileNo", account.getMobileNumber());
			userDetails.put("Email", account.getEmailId());
			userDetails.put("Address", new String(account.getAddresses().get(0).getAddress1().concat(", "))
										.concat(account.getAddresses().get(0).getPincode().concat(", "))
										.concat(account.getAddresses().get(0).getZone().concat(", "))
										.concat(account.getAddresses().get(0).getUlbName().concat(", "))
										.concat(account.getAddresses().get(0).getWardName().concat(", "))
										.concat(account.getAddresses().get(0).getAdditionalDetail().get("district").asText()));

			garbageAccountDetail.setUserDetails(userDetails);
			
			
			
			
			garbageAccountDetails.add(garbageAccountDetail);
		});
		
		return garbageAccountDetails;
	}


	public GarbageAccountActionResponse getActionsOnApplication(GarbageAccountActionRequest garbageAccountActionRequest) {
		
		if(CollectionUtils.isEmpty(garbageAccountActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number.");
		}
		
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		
		// search garbage accounts by application numbers
		SearchCriteriaGarbageAccount criteria = SearchCriteriaGarbageAccount.builder()
				.applicationNumber(garbageAccountActionRequest.getApplicationNumbers())
				.build();
		List<GarbageAccount> accounts = garbageAccountRepository.searchGarbageAccount(criteria);
		if(CollectionUtils.isEmpty(accounts)) {
			throw new CustomException("GARBAGE_ACCOUNT_NOT_FOUND","No Garbage Account found with given application number.");
		}
		Map<String, GarbageAccount> mapAccounts = accounts.stream().collect(Collectors.toMap(acc->acc.getGrbgApplication().getApplicationNo(), acc->acc));
		
		
		String applicationTenantId = accounts.get(0).getTenantId();
		String applicationBusinessId = GrbgConstants.WORKFLOW_BUSINESS_SERVICE;
		
		// fetch business service search
		BusinessServiceResponse businessServiceResponse = workflowService.businessServiceSearch(garbageAccountActionRequest, applicationTenantId,
				applicationBusinessId);
		
		if(null == businessServiceResponse || CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
			throw new CustomException("NO_BUSINESS_SERVICE_FOUND","Business service not found for application numbers: "+garbageAccountActionRequest.getApplicationNumbers().toString());
		}
		
		
		List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId, garbageAccountActionRequest.getRequestInfo().getUserInfo().getRoles());
		
		
		
		garbageAccountActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
		
			String status = mapAccounts.get(applicationNumber).getGrbgApplication().getStatus();
			List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
					.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), status)
										&& !StringUtils.equalsAnyIgnoreCase(state.getApplicationStatus(), applicationPropertiesAndConstant.STATUS_APPROVED)
										).collect(Collectors.toList());
			
			// filtering actions based on roles
			List<String> actions = new ArrayList<>();
			stateList.stream().forEach(state -> {
				state.getActions().stream()
				.filter(action -> action.getRoles().stream().anyMatch(role -> rolesWithinTenant.contains(role)))
				.forEach(action -> {
					actions.add(action.getAction());
				});
			}) ;
			
			
			applicationActionMaps.put(applicationNumber, actions);
		});
		
		List<GarbageAccountDetail> garbageDetailList = new ArrayList<>();
		applicationActionMaps.entrySet().stream().forEach(entry -> {
			garbageDetailList.add(GarbageAccountDetail.builder().applicationNumber(entry.getKey()).action(entry.getValue()).build());
		});
		
		// build response
		GarbageAccountActionResponse garbageAccountActionResponse = GarbageAccountActionResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(garbageAccountActionRequest.getRequestInfo(), true))
				.applicationDetails(garbageDetailList).build();
		return garbageAccountActionResponse;
	
	}

	private List<String> getRolesWithinTenant(String tenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}


}
