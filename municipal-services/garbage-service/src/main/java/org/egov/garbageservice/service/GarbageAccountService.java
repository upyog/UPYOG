package org.egov.garbageservice.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.egov.garbageservice.model.GenrateArrearRequest;
import org.egov.garbageservice.contract.bill.DemandRepository;

import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.contract.bill.Bill.StatusEnum;
import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.BillSearchCriteria;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.GenerateBillCriteria;
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
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.model.GrbgAddress;
import org.egov.garbageservice.model.GrbgApplication;
import org.egov.garbageservice.model.GrbgBillFailure;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerRequest;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.model.GrbgCollectionUnit;
import org.egov.garbageservice.model.PayNowRequest;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.model.TotalCountRequest;
import org.egov.garbageservice.model.UserSearchRequest;
import org.egov.garbageservice.model.UserSearchResponse;
import org.egov.garbageservice.model.contract.DmsRequest;
import org.egov.garbageservice.model.contract.OwnerInfo;
import org.egov.garbageservice.model.contract.PDFRequest;
import org.egov.garbageservice.repository.GarbageAccountRepository;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.egov.garbageservice.repository.GrbgAddressRepository;
import org.egov.garbageservice.repository.GrbgApplicationRepository;
import org.egov.garbageservice.repository.GrbgCollectionUnitRepository;
import org.egov.garbageservice.repository.GrbgCommercialDetailsRepository;
import org.egov.garbageservice.repository.GrbgDocumentRepository;
import org.egov.garbageservice.repository.GrbgOldDetailsRepository;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.GrbgUtils;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.util.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageAccountService {

	@Autowired
	private GarbageAccountRepository garbageAccountRepository;

	@Autowired
	private AlfrescoService alfrescoService;

	@Autowired
	private GrbgApplicationRepository grbgApplicationRepository;

	@Autowired
	private GrbgCommercialDetailsRepository grbgCommercialDetailsRepository;

	@Autowired
	private GrbgDocumentRepository grbgDocumentRepository;

	@Autowired
	private PDFRequestGenerator pdfRequestGenerator;

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
	private DemandRepository demandRepository;

	@Autowired
	private ReportService reportService;

	@Autowired
	private BillService billService;

	@Autowired
	private UserService userService;

	@Autowired
	private GrbgUtils grbgUtils;

	@Autowired
	private GarbageBillTrackerRepository garbageBillTrackerRepository;

	@Autowired
	private GarbageAccountSchedulerService garbageAccountSchedulerService;

	public GarbageAccountResponse create(GarbageAccountRequest createGarbageRequest) {

		RequestInfo info = createGarbageRequest.getRequestInfo();
		List<GarbageAccount> garbageAccounts = new ArrayList<>();

		List<String> propertyIds = createGarbageRequest.getGarbageAccounts().stream()
				.map(account -> account.getPropertyId()).collect(Collectors.toList());
		List<GarbageAccount> existingAccounts = new ArrayList<>();
//		if (!createGarbageRequest.getCreateChildAccountOnly()) {
//			// search existing account
//			existingAccounts = garbageAccountRepository.searchGarbageAccount(
//					SearchCriteriaGarbageAccount.builder().propertyId(propertyIds).parentAccount(null).build());
//		}

		if (!CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())) {
			for (GarbageAccount garbageAccount : createGarbageRequest.getGarbageAccounts()) {
				// validate and enrich
				validateAndEnrichCreateGarbageAccount(createGarbageRequest, garbageAccount, existingAccounts);
			}

			if (createGarbageRequest != null && !CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())
					&& createGarbageRequest.getGarbageAccounts().stream()
							.noneMatch(account -> StringUtils.isEmpty(account.getMobileNumber()))) {
				// create user if not exists
				createGarbageRequest = userService.createUser(createGarbageRequest);
			}

			// call workflow
			ProcessInstanceResponse processInstanceResponse = callWfUpdate(createGarbageRequest);

			if (!createGarbageRequest.getCreateChildAccountOnly()) {
				createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {

					// create garbage account
					garbageAccounts.add(garbageAccountRepository.create(garbageAccount));

					// create garbage objects
					createGarbageAccountObjects(garbageAccount);

				});
			}

			createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {
				if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
					garbageAccount.getChildGarbageAccounts().stream().forEach(subAccount -> {
						subAccount.setBusinessService(garbageAccount.getBusinessService());
						org.egov.garbageservice.model.contract.Role role = org.egov.garbageservice.model.contract.Role.builder()
								.code("CITIZEN").name("Citizen").build();
						// map user uuid
						userService.processGarbageAccount(info,role, subAccount);
						// create garbage sub account
						garbageAccountRepository.create(subAccount);
						// create garbage objects
						createGarbageAccountObjects(subAccount);
					});
				}
			});
		}

		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(createGarbageRequest.getRequestInfo(), false))
				.garbageAccounts(garbageAccounts).build();
		if (!CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccountResponse.setResponseInfo(
					responseInfoFactory.createResponseInfoFromRequestInfo(createGarbageRequest.getRequestInfo(), true));
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

	private void validateAndEnrichCreateGarbageAccount(GarbageAccountRequest createGarbageRequest,
			GarbageAccount garbageAccount, List<GarbageAccount> existingAccounts) {
		List<GarbageAccount> parentAccount = new ArrayList<>();
		if (!createGarbageRequest.getCreateChildAccountOnly()) {
			// validate create garbage account
			validateGarbageAccount(garbageAccount, existingAccounts);

			// enrich create garbage account
			enrichCreateGarbageAccount(garbageAccount, createGarbageRequest.getRequestInfo());

			// enrich garbage address
			validateAndsEnrichCreateGarbageAddress(garbageAccount);

			// enrich create garbage application
			enrichCreateGarbageApplication(garbageAccount, createGarbageRequest.getRequestInfo());

			// enrich old garbage details
			enrichCreateGarbageOldDetails(garbageAccount);

			// enrich garbage unit
			enrichCreateGarbageUnit(garbageAccount);
		} else {
			parentAccount = garbageAccountRepository.searchGarbageAccount(SearchCriteriaGarbageAccount.builder()
					.garbageId(Collections.singletonList(garbageAccount.getGarbageId()))
					.parentAccount(garbageAccount.getParentAccount()).build(), null);
		}

		// enrich garbage sub accounts
		enrichCreateGarbageSubAccounts(garbageAccount, parentAccount);

		// enrich garbage sub account unit
		enrichCreateSubGarbageAccountUnits(garbageAccount);

		// enrich garbage sub account application
		enrichCreateSubGarbageAccountAddress(garbageAccount);

		// enrich garbage sub account address
		enrichCreateSubGarbageAccountApplication(garbageAccount, parentAccount);
	}

	private void enrichCreateSubGarbageAccountApplication(GarbageAccount garbageAccount,
			List<GarbageAccount> parentAccount) {

		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
			AtomicLong childCount = new AtomicLong(1L);

			if (!CollectionUtils.isEmpty(parentAccount)
					&& !CollectionUtils.isEmpty(parentAccount.get(0).getChildGarbageAccounts())) {
				parentAccount.get(0).getChildGarbageAccounts().stream().map(ca -> {
					// Split the application number and get the part after the concatenation
					String[] parts = ca.getGrbgApplication().getApplicationNo()
							.split(garbageAccount.getGrbgApplication().getApplicationNo().concat("/"));
					return parts.length > 1 ? parts[1] : ""; // Return the second part if available, otherwise empty
				}).mapToLong(str -> {
					try {
						return Long.parseLong(str); // Try parsing the extracted part as a Long
					} catch (NumberFormatException e) {
						return Long.MIN_VALUE; // If parsing fails, return the smallest possible value
					}
				}).forEach(value -> childCount.updateAndGet(v -> Math.max(v, value))); // Update the AtomicLong with the
																						// maximum value

				childCount.getAndIncrement();
			}

			garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {
				GrbgApplication grbgApplication = GrbgApplication.builder().uuid(UUID.randomUUID().toString())
						.applicationNo(garbageAccount.getGrbgApplication().getApplicationNo().concat("/")
								.concat(Long.toString(childCount.getAndIncrement())))
						.status(GrbgConstants.STATUS_INITIATED).garbageId(subAccount.getGarbageId()).build();

				subAccount.setGrbgApplication(grbgApplication);
			});
		}

	}

	private void enrichCreateSubGarbageAccountAddress(GarbageAccount garbageAccount) {

		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())
				&& !CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {

				List<GrbgAddress> grbgAddresses = new ArrayList<>();
				for (GrbgAddress tempG : garbageAccount.getAddresses()) {
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

	private void enrichCreateGarbageSubAccounts(GarbageAccount garbageAccount, List<GarbageAccount> parentAccount) {
		if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
//			Long maxGarbageId = garbageAccount.getGarbageId();
//
//			if (!CollectionUtils.isEmpty(parentAccount)
//					&& !CollectionUtils.isEmpty(parentAccount.get(0).getChildGarbageAccounts())) {
//				maxGarbageId = parentAccount.get(0).getChildGarbageAccounts().stream()
//						.mapToLong(childAccount -> childAccount.getGarbageId()).max()
//						.orElse(garbageAccount.getGarbageId()); // or provide a default value if preferred
//			}

			AtomicInteger counter = new AtomicInteger(1);
			for (GarbageAccount subAccount : garbageAccount.getChildGarbageAccounts()) {
				subAccount.setId(garbageAccountRepository.getNextSequence());
				subAccount.setUuid(UUID.randomUUID().toString());
				subAccount.setPropertyId(garbageAccount.getPropertyId());
				subAccount.setTenantId(garbageAccount.getTenantId());
				subAccount.setAdditionalDetail(garbageAccount.getAdditionalDetail());
//				subAccount.setIsOwner(false);
				subAccount.setGarbageId(garbageAccountRepository.getNextGarbageId());
				subAccount.setStatus(GrbgConstants.STATUS_INITIATED);
				subAccount.setWorkflowAction(GrbgConstants.WORKFLOW_ACTION_INITIATE);
				subAccount.setAuditDetails(garbageAccount.getAuditDetails());
				subAccount.setParentAccount(garbageAccount.getUuid());
				subAccount.setIsActive(true);
			}
			garbageAccount.setSubAccountCount((long) counter.get());
		}
	}

	private void createGarbageUnit(GarbageAccount garbageAccount) {
		if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				grbgCollectionUnitRepository.create(unit);
			});
		}
	}

	private void enrichCreateGarbageUnit(GarbageAccount garbageAccount) {

		if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
				unit.setUuid(UUID.randomUUID().toString());
				unit.setIsActive(true);
				unit.setGarbageId(garbageAccount.getGarbageId());
			});
		}
	}

	private void enrichCreateGarbageOldDetails(GarbageAccount garbageAccount) {
		if (null != garbageAccount.getGrbgOldDetails()) {
			garbageAccount.getGrbgOldDetails().setUuid(UUID.randomUUID().toString());
			garbageAccount.getGrbgOldDetails().setGarbageId(garbageAccount.getGarbageId());
		}
	}

	private void createGarbageOldDetails(GarbageAccount garbageAccount) {

		if (null != garbageAccount.getGrbgOldDetails()) {
			grbgOldDetailsRepository.create(garbageAccount.getGrbgOldDetails());
		}

	}

	private void createGarbageAddress(GarbageAccount garbageAccount) {

		if (!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getAddresses().stream().forEach(address -> {
				grbgAddressRepository.create(address);
			});
		}
	}

	private void validateAndsEnrichCreateGarbageAddress(GarbageAccount garbageAccount) {
		if (!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			garbageAccount.getAddresses().stream().forEach(address -> {

				// validate address
				if (StringUtils.isEmpty(address.getAddress1()) || null == address.getAdditionalDetail()
						|| null == address.getAdditionalDetail().get("district")) {
					throw new CustomException("MISSING_ADDRESS_DETAILS", "Provide mendatory details of address.");
				}

				// enrich address
				address.setUuid(UUID.randomUUID().toString());
				address.setIsActive(true);
				address.setGarbageId(garbageAccount.getGarbageId());
			});
		} else {
			throw new CustomException("MISSING_ADDRESS", "Provide address.");
		}
	}

//	private void createGarbageDocuments(GarbageAccount garbageAccount) {
//
//		garbageAccount.getDocuments().stream().forEach(doc -> {
//			grbgDocumentRepository.create(doc);
//		});
//
//	}

//	private void enrichCreateGarbageDocuments(GarbageAccount garbageAccount) {
//
//		garbageAccount.getDocuments().stream().forEach(doc -> {
//			doc.setUuid(UUID.randomUUID().toString());
//			if (StringUtils.equalsIgnoreCase(doc.getDocCategory(), GrbgConstants.DOCUMENT_ACCOUNT)) {
//				doc.setTblRefUuid(garbageAccount.getUuid());
//			}
//		});
//
//	}

	private void enrichCreateGarbageApplication(GarbageAccount garbageAccount, RequestInfo requestInfo) {

		// get application number format
		String applicationNumber = GrbgConstants.generateApplicationNumberFormat(String.valueOf(garbageAccount.getId()),
				garbageAccount.getAddresses().get(0).getUlbName(),
				garbageAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText());

		GrbgApplication grbgApplication = GrbgApplication.builder().uuid(UUID.randomUUID().toString())
				.applicationNo(applicationNumber).status(GrbgConstants.STATUS_INITIATED)
				.garbageId(garbageAccount.getGarbageId()).build();

		garbageAccount.setGrbgApplication(grbgApplication);
	}

	private void validateGarbageAccount(GarbageAccount garbageAccount, List<GarbageAccount> existingAccounts) {

		// validate nullability
		if (null == garbageAccount || null == garbageAccount.getMobileNumber() || null == garbageAccount.getName()) {
//				|| null == garbageAccount.getType()
//				|| null == garbageAccount.getPropertyId()) {
			throw new CustomException("MISSING_GARBAGE_ACCOUNT_DETAILS", "Provide garbage account details.");
		}

		// validate duplicate owner with same properyId

//		if(BooleanUtils.isTrue(duplicateOwner)) {
//			throw new CustomException("DUPLICATE_OWNER","Duplicate Owner Found for given property.");
//		}
//		if(StringUtils.isEmpty(garbageAccount.getUuid())			// create account condition
//				&& !CollectionUtils.isEmpty(existingAccounts)) {
//			throw new CustomException("DUPLICATE_OWNER","Can't create Duplicate Owner for given property which is already present.");
//		}else 
		if (StringUtils.isNotEmpty(garbageAccount.getUuid())) // update account condition
		{
//			log.info("existingAccounts issue. {} {}", existingAccounts,garbageAccount);

			List<GarbageAccount> existingAccounts1 = existingAccounts.stream()
					.filter(account -> StringUtils.equals(garbageAccount.getUuid(), account.getUuid()))
					.collect(Collectors.toList());

			if (CollectionUtils.isEmpty(existingAccounts1)) {
				throw new CustomException("GARBAGE_ACCOUNT_NOT_FOUND", "Not able to find garbage account.");
			} else if (existingAccounts1.size() > 1) {
				throw new CustomException("DUPLICATE_GARBAGE_ACCOUNT_FOUND", "Duplicate Garbage account found.");
			}
//			if (!StringUtils.isEmpty(existingAccounts1.get(0).getPropertyId())
//					&& !StringUtils.equals(existingAccounts1.get(0).getPropertyId(), garbageAccount.getPropertyId())) {
//				throw new CustomException("NO_DATA_CAN_BE_CHANGE",
//						"Some of the data is not matching and can't be updated.");
//			}

			if (CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
				// validate child garbage account
				garbageAccount.getChildGarbageAccounts().stream().forEach(childAcc -> {
					Optional<GarbageAccount> matchingChildAccount = existingAccounts1.get(0).getChildGarbageAccounts()
							.stream().filter(existingChildAcc -> StringUtils.equals(existingChildAcc.getUuid(),
									childAcc.getUuid()))
							.findFirst();
					if (!matchingChildAccount.isPresent()) {
						throw new CustomException("CHILD_GARBAGE_ACCOUNT_NOT_FOUND",
								"Provide correct uuid for child garbage account.");
					}
				});
			}

		}

	}

	private void enrichCreateGarbageAccount(GarbageAccount garbageAccount, RequestInfo requestInfo) {

		AuditDetails auditDetails = null;
		if (null != requestInfo && null != requestInfo.getUserInfo()) {
			auditDetails = AuditDetails.builder().createdBy(requestInfo.getUserInfo().getUuid())
					.createdDate(new Date().getTime()).lastModifiedBy(requestInfo.getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
			garbageAccount.setAuditDetails(auditDetails);
		}

		// generate garbage_id
		garbageAccount.setId(garbageAccountRepository.getNextSequence());
		garbageAccount.setUuid(UUID.randomUUID().toString());
		garbageAccount.setGarbageId(garbageAccountRepository.getNextGarbageId());
		garbageAccount.setStatus(GrbgConstants.STATUS_INITIATED);
		garbageAccount.setWorkflowAction(GrbgConstants.WORKFLOW_ACTION_INITIATE);
		garbageAccount.setParentAccount(null);
		garbageAccount.setIsActive(true);
		garbageAccount.setSubAccountCount(Optional.ofNullable(garbageAccount.getChildGarbageAccounts()).map(List::size)
				.map(Integer::longValue).orElse(0L));

	}

	private void enrichUpdateGarbageAccount(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount,
			RequestInfo requestInfo, Map<String, String> applicationNumberToCurrentStatus) {

		AuditDetails auditDetails = AuditDetails.builder().build();
		if (null != requestInfo && null != requestInfo.getUserInfo()) {
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
		newGarbageAccount.setBusinessService(existingGarbageAccount.getBusinessService());
		newGarbageAccount.setChannel(existingGarbageAccount.getChannel());
		;

		// enrich child accounts
		if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {

			newGarbageAccount.getChildGarbageAccounts().stream().forEach(childAccount -> {

				// update case
				if (StringUtils.isNotEmpty(childAccount.getUuid())) {

					Optional<GarbageAccount> matchingChildAccount = existingGarbageAccount.getChildGarbageAccounts()
							.stream().filter(existingChildAcc -> StringUtils.equals(existingChildAcc.getUuid(),
									childAccount.getUuid()))
							.findFirst();

					childAccount.setAuditDetails(AuditDetails.builder()
							.createdBy(matchingChildAccount.get().getAuditDetails().getCreatedBy())
							.createdDate(matchingChildAccount.get().getAuditDetails().getCreatedDate())
							.lastModifiedBy(auditDetails.getLastModifiedBy())
							.lastModifiedDate(auditDetails.getLastModifiedDate()).build());
					childAccount.setChannel(matchingChildAccount.get().getChannel());

				} else {
					// create case
					childAccount.setAuditDetails(AuditDetails.builder().createdBy(auditDetails.getCreatedBy())
							.createdDate(new Date().getTime()).build());
				}

				childAccount.setBusinessService(newGarbageAccount.getBusinessService());
			});
		}

//		if (null != newGarbageAccount.getGrbgApplication()) {
//			
//			newGarbageAccount.setStatus(
//					applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo()));
//			Optional.ofNullable(newGarbageAccount.getChildGarbageAccounts())
//					.ifPresent(childGarbageAccounts -> childGarbageAccounts.forEach(childGarbageAccount -> {
//						String status = applicationNumberToCurrentStatus
//								.get(childGarbageAccount.getGrbgApplication().getApplicationNo());
//						childGarbageAccount.setStatus(status);
//
//						Optional.ofNullable(childGarbageAccount.getGrbgApplication())
//								.ifPresent(grbgApplication -> grbgApplication.setStatus(status));
//					}));
//		}
	}

	public GarbageAccountResponse update(GarbageAccountRequest updateGarbageRequest) {

		// remove child garbage account if not in request
		removeChildGarbageAccount(updateGarbageRequest);

		// create child garbage account if new in request
		createChildGarbageAccount(updateGarbageRequest);

		List<GarbageAccount> garbageAccounts = new ArrayList<>();

		// search existing garbage accounts
		Map<Long, GarbageAccount> existingGarbageIdAccountsMap;
		Map<String, GarbageAccount> existingGarbageApplicationAccountsMap;
		try {
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(
					updateGarbageRequest.getGarbageAccounts());
			existingGarbageIdAccountsMap = searchGarbageAccountMap(searchCriteriaGarbageAccount,
					updateGarbageRequest.getRequestInfo());
			existingGarbageApplicationAccountsMap = existingGarbageIdAccountsMap.entrySet().stream().collect(
					Collectors.toMap(a -> a.getValue().getGrbgApplication().getApplicationNo(), b -> b.getValue()));
		} catch (Exception e) {
			throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS", "Search Garbage account details failed.");
		}

		// load garbage account from backend if workflow = true
		GarbageAccountRequest garbageAccountRequest = loadUpdateGarbageAccountRequestFromMap(updateGarbageRequest,
				existingGarbageApplicationAccountsMap);

		ProcessInstanceResponse processInstanceResponse = null;
		// call workflow
		if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
				&& updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
			processInstanceResponse = callWfUpdate(garbageAccountRequest);
		}
		Map<String, String> applicationNumberToCurrentStatus = new HashMap<>();
		if (null != processInstanceResponse) {
			applicationNumberToCurrentStatus = processInstanceResponse.getProcessInstances().stream().collect(Collectors
					.toMap(ProcessInstance::getBusinessId, instance -> instance.getState().getApplicationStatus()));
		}

		// update garbage account
		if (!CollectionUtils.isEmpty(garbageAccountRequest.getGarbageAccounts())) {
			garbageAccountRequest.getGarbageAccounts().stream().forEach(newGarbageAccount -> {

//				if(!newGarbageAccount.getIsOnlyWorkflowCall()) {
				// validate garbage account request
				validateGarbageAccount(newGarbageAccount, existingGarbageIdAccountsMap.entrySet().stream()
						.map(entry -> entry.getValue()).collect(Collectors.toList()));

			});

			for (GarbageAccount newGarbageAccount : garbageAccountRequest.getGarbageAccounts()) {
//			garbageAccountRequest.getGarbageAccounts().stream().forEach(newGarbageAccount -> {

				// get existing garbage account from map
				GarbageAccount existingGarbageAccount = existingGarbageIdAccountsMap
						.get(newGarbageAccount.getGarbageId());

				// enrich garbage account
				enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount,
						updateGarbageRequest.getRequestInfo(), applicationNumberToCurrentStatus);

				// update garbage account
				if (!newGarbageAccount.equals(existingGarbageAccount)) {
					updateGarbageAccount(updateGarbageRequest, newGarbageAccount, existingGarbageAccount,
							applicationNumberToCurrentStatus);
				}

				// update other objects of garbage account
				updateAndEnrichGarbageAccountObjects(newGarbageAccount, existingGarbageAccount,
						applicationNumberToCurrentStatus);

				garbageAccounts.add(newGarbageAccount);
			}

		}

		if (!updateGarbageRequest.getFromMigration()) {
			// generate certificate and upload

			createAndUploadPDF(garbageAccountRequest, updateGarbageRequest.getRequestInfo());

			// generate demand and fetch bill
			generateDemandAndBill(garbageAccountRequest);
		}

		// RESPONSE builder
		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), false))
				.garbageAccounts(garbageAccounts).build();
		if (!CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccountResponse.setResponseInfo(responseInfoFactory
					.createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), true));
		}

		return garbageAccountResponse;
	}

	private GarbageAccountRequest createChildGarbageAccount(GarbageAccountRequest updateGarbageRequest) {
		if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
				&& updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
			return null;
		}
		// Check if there are any child garbage accounts with an empty UUID
		boolean hasChildAccountsWithEmptyUuid = updateGarbageRequest.getGarbageAccounts().stream()
				.flatMap(grbgAccount -> grbgAccount.getChildGarbageAccounts().stream())
				.anyMatch(childGrbgAccount -> StringUtils.isEmpty(childGrbgAccount.getUuid()));

		if (hasChildAccountsWithEmptyUuid) {
			// Create a new GarbageAccountRequest, leaving the original
			// `updateGarbageRequest` unchanged
			List<GarbageAccount> updatedGarbageAccounts = updateGarbageRequest.getGarbageAccounts().stream()
					.map(grbgAccount -> {
						// Filter child accounts to only include those with an empty UUID
						List<GarbageAccount> filteredChildGarbageAccounts = grbgAccount.getChildGarbageAccounts()
								.stream().filter(childGrbgAccount -> StringUtils.isEmpty(childGrbgAccount.getUuid()))
								.collect(Collectors.toList());

						// Create a new GarbageAccount instance with the updated child garbage accounts
						return grbgAccount.toBuilder().childGarbageAccounts(filteredChildGarbageAccounts).build();
					}).collect(Collectors.toList());

			// Build the new request object with the modified child garbage accounts
			GarbageAccountRequest createChildGarbageAccountRequest = GarbageAccountRequest.builder()
					.garbageAccounts(updatedGarbageAccounts).requestInfo(updateGarbageRequest.getRequestInfo())
					.createChildAccountOnly(true).build();

			// Perform the create operation with the new request object
			create(createChildGarbageAccountRequest);
		}

		// Return the original updateGarbageRequest unchanged
		return updateGarbageRequest;
	}

	private GarbageAccountResponse removeChildGarbageAccount(GarbageAccountRequest updateGarbageRequest) {
		if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
				&& updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
			return null;
		}

		// Extract all child UUIDs from the updateGarbageRequest
		Set<String> requestChildUuids = updateGarbageRequest.getGarbageAccounts().stream()
				.flatMap(grbgAccount -> grbgAccount.getChildGarbageAccounts().stream()).map(GarbageAccount::getUuid)
				.filter(Objects::nonNull) // Filter out null UUIDs
				.collect(Collectors.toSet());

		// Build the search criteria request
		SearchCriteriaGarbageAccountRequest searchCriteria = SearchCriteriaGarbageAccountRequest.builder()
				.requestInfo(updateGarbageRequest.getRequestInfo()).searchCriteriaGarbageAccount(
						createSearchCriteriaByGarbageAccounts(updateGarbageRequest.getGarbageAccounts()))
				.build();

		// Get the response from the database
		GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteria, false);

		// Map child garbage account UUIDs from the database response
		Map<String, GarbageAccount> dbChildGarbageAccountsMap = garbageAccountResponse.getGarbageAccounts().stream()
				.flatMap(grbgAccount -> grbgAccount.getChildGarbageAccounts().stream())
				.collect(Collectors.toMap(GarbageAccount::getUuid, Function.identity()));

		// Create a set of UUIDs that need to be removed
		Set<String> uuidsToRemove = new HashSet<>(dbChildGarbageAccountsMap.keySet());
		uuidsToRemove.removeAll(requestChildUuids); // Remove those present in the request

		// Create the list of GarbageAccounts to remove based on the UUIDs
		List<GarbageAccount> removeChildGarbageAccounts = uuidsToRemove.stream().map(dbChildGarbageAccountsMap::get)
				.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(removeChildGarbageAccounts)) {
			delete(GarbageAccountRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
					.garbageAccounts(removeChildGarbageAccounts).build());
		}
		return garbageAccountResponse;
	}

	private void createAndUploadPDF(GarbageAccountRequest updateGarbageRequest, RequestInfo requestInfo) {
		List<GarbageAccount> GarbageAccounts = updateGarbageRequest.getGarbageAccounts();

		if (!CollectionUtils.isEmpty((GarbageAccounts))) {
			for (GarbageAccount GarbageAccount : GarbageAccounts) {
				if (StringUtils.equalsIgnoreCase(GarbageAccount.getWorkflowAction(),
						GrbgConstants.WORKFLOW_ACTION_APPROVE)) {
					saveGrbCertificate(GarbageAccount, requestInfo);
				}
			}
		}
	}

	public void saveGrbCertificate(GarbageAccount GarbageAccount, RequestInfo requestInfo) {

		// validate trade license
		validateGrbCertificateGeneration(GarbageAccount);

		// create pdf
		Resource resource = createNoSavePDF(GarbageAccount, requestInfo);

		// upload pdf
		DmsRequest dmsRequest = generateDmsRequestByGarbage(resource, GarbageAccount, requestInfo);
		try {
			String documentReferenceId = alfrescoService.uploadAttachment(dmsRequest, requestInfo);
		} catch (IOException e) {
			throw new CustomException("UPLOAD_ATTACHMENT_FAILED", "Upload Attachment failed." + e.getMessage());
		}

	}

	private DmsRequest generateDmsRequestByGarbage(Resource resource, GarbageAccount GarbageAccount,
			RequestInfo requestInfo) {

		DmsRequest dmsRequest = DmsRequest.builder().userId(requestInfo.getUserInfo().getId().toString())
				.objectId(GarbageAccount.getUuid()).description(GrbgConstants.ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION)
				.id(GrbgConstants.ALFRESCO_COMMON_CERTIFICATE_ID).type(GrbgConstants.ALFRESCO_COMMON_CERTIFICATE_TYPE)
				.objectName(GarbageAccount.getBusinessService()).comments(GrbgConstants.ALFRESCO_TL_CERTIFICATE_COMMENT)
				.status(GrbgConstants.STATUS_APPROVED).file(resource).servicetype(GarbageAccount.getBusinessService())
				.documentType(GrbgConstants.ALFRESCO_DOCUMENT_TYPE)
				.documentId(GrbgConstants.ALFRESCO_COMMON_DOCUMENT_ID).build();

		return dmsRequest;
	}

	public Resource createNoSavePDF(GarbageAccount GarbageAccount, RequestInfo requestInfo) {

		PDFRequest pdfRequest = generatePdfRequestByGarbage(GarbageAccount, requestInfo);
		ResponseEntity<Resource> resource = reportService.createNoSavePDF(pdfRequest);

		return resource.getBody();
	}

	private PDFRequest generatePdfRequestByGarbage(GarbageAccount GarbageAccount, RequestInfo requestInfo) {

		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map2 = generateDataForGarbagePdfCreate(GarbageAccount, requestInfo);

		map.put("gb", map2);

		PDFRequest pdfRequest = PDFRequest.builder().RequestInfo(requestInfo).key("GarbageRegistrationCertificate")
				.tenantId(GarbageAccount.getTenantId()).data(map).build();

		return pdfRequest;
	}

	private Map<String, Object> generateDataForGarbagePdfCreate(GarbageAccount GarbageAccount,
			RequestInfo requestInfo) {

		Map<String, Object> grbObject = new HashMap<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		// map variables and values
		grbObject.put("applicationNumber", GarbageAccount.getGrbgApplicationNumber());// garbage Application No
//		tlObject.put("tradeRegistrationNo", GarbageAccount.getApplicationNumber()); // Trade Registration No
		grbObject.put("ownerName", GarbageAccount.getName());// owner Name
		grbObject.put("address",
				GarbageAccount.getAddresses().get(0).getAddress1().concat(", ")
						.concat(GarbageAccount.getAddresses().get(0).getWardName()).concat(", ")
						.concat(GarbageAccount.getAddresses().get(0).getUlbName()).concat(" (")
						.concat(GarbageAccount.getAddresses().get(0).getUlbType()).concat(") ")
						.concat(GarbageAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText())
						.concat(", ").concat(GarbageAccount.getAddresses().get(0).getPincode()));
		// Applicant
		// Name
		grbObject.put("mobileNumber", GarbageAccount.getMobileNumber());
		// Contact // No
		grbObject.put("propertyId", GarbageAccount.getPropertyId());

		grbObject.put("createdTime", "sjgjkhd");

		grbObject.put("ulbType", GarbageAccount.getAddresses().get(0).getUlbType());

		grbObject.put("ulbName", GarbageAccount.getAddresses().get(0).getUlbName());

		grbObject.put("approvalTime", dateFormat.format(new Date(GarbageAccount.getApprovalDate() * 1000)));

		grbObject.put("approverName",
				null != requestInfo.getUserInfo() ? requestInfo.getUserInfo().getUserName() : null);
		grbObject.put("userName", null != requestInfo.getUserInfo() ? requestInfo.getUserInfo().getName() : null);

		if ("MIGRATION".equals(GarbageAccount.getChannel())) {
			String userName = garbageAccountRepository.getApproverUserNameForTenant(GarbageAccount.getTenantId());
			grbObject.put("approverName", userName);
			UserSearchRequest userSearch = UserSearchRequest.builder().userName(userName).tenantId("hp")
					.requestInfo(requestInfo).build();
			OwnerInfo userDetail = getUserDetails(userSearch);
			if (userDetail != null)
				grbObject.put("userName", userDetail.getName());
		}

		// generate QR code from attributes
		StringBuilder uri = new StringBuilder(applicationPropertiesAndConstant.getFrontEndBaseUri());
		uri.append("citizen-payment");
		String qr = GarbageAccount.getCreated_by().concat("/").concat(GarbageAccount.getUuid()).concat("/")
				.concat(null != GarbageAccount.getPropertyId() ? GarbageAccount.getPropertyId() : "");
		uri.append("/").append(qr);
		grbObject.put("qrCodeText", uri);
		return grbObject;
	}

	private OwnerInfo getUserDetails(UserSearchRequest userSearchRequest) {
		List<OwnerInfo> UserList = userService.userSearch(userSearchRequest);
		if (!CollectionUtils.isEmpty((UserList))) {
			return UserList.get(0);
		}
		return null;
	}

	public void validateGrbCertificateGeneration(GarbageAccount GarbageAccount) {

		if (StringUtils.isEmpty(GarbageAccount.getGrbgApplicationNumber())
//				&& StringUtils.isEmpty(tradeLicense.getApplicationNumber())
				&& StringUtils.isEmpty(GarbageAccount.getName())
				&& StringUtils.isEmpty(GarbageAccount.getAddresses().get(0).getAddress1())
				&& (GarbageAccount.getAddresses().get(0).getAdditionalDetail().get("district") == null || StringUtils
						.isEmpty(GarbageAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText()))
				&& StringUtils.isEmpty(GarbageAccount.getAddresses().get(0).getWardName())
				&& StringUtils.isEmpty(GarbageAccount.getAddresses().get(0).getPincode())
				&& (GarbageAccount.getAdditionalDetail().get("applicantName") == null
						|| StringUtils.isEmpty(GarbageAccount.getAdditionalDetail().get("applicantName").asText()))
				&& (GarbageAccount.getAdditionalDetail().get("applicantPhoneNumber") == null || StringUtils
						.isEmpty(GarbageAccount.getAdditionalDetail().get("applicantPhoneNumber").asText()))) {

			throw new CustomException("NULL_APPLICATION_NUMBER",
					"PDF can't be generated with null values for application number: "
							+ GarbageAccount.getGrbgApplicationNumber());
		}
	}

	private void generateDemandAndBill(GarbageAccountRequest updateGarbageRequest) {
		updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {

			if (StringUtils.equalsIgnoreCase(GrbgConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT,
					account.getWorkflowAction())) {

				List<Demand> savedDemands = new ArrayList<>();
				// generate demand
				BigDecimal taxAmount = new BigDecimal("100.00");
				savedDemands = demandService.generateDemand(updateGarbageRequest.getRequestInfo(), account,
						account.getBusinessService(), taxAmount, null);

				if (CollectionUtils.isEmpty(savedDemands)) {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");
				}

				// fetch/create bill
				GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(account.getTenantId())
						.businessService(account.getBusinessService()).consumerCode(account.getGrbgApplicationNumber())
						.build();
				BillResponse billResponse = billService.generateBill(updateGarbageRequest.getRequestInfo(),
						billCriteria);

			}
		});
	}

	private GarbageAccountRequest loadUpdateGarbageAccountRequestFromMap(GarbageAccountRequest updateGarbageRequest,
			Map<String, GarbageAccount> existingGarbageApplicationAccountsMap) {

		GarbageAccountRequest garbageAccountRequestTemp = GarbageAccountRequest.builder()
				.requestInfo(updateGarbageRequest.getRequestInfo()).garbageAccounts(new ArrayList<>()).build();

		updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {

			if (!BooleanUtils.isTrue(account.getIsOnlyWorkflowCall())) {
				org.egov.garbageservice.model.contract.Role role = org.egov.garbageservice.model.contract.Role.builder()
						.code("CITIZEN").name("Citizen").build();
				userService.processGarbageAccount(updateGarbageRequest.getRequestInfo(), role, account);
				if (null != account.getChildGarbageAccounts()) {
					account.getChildGarbageAccounts().stream().forEach(childAccount -> {
						userService.processGarbageAccount(updateGarbageRequest.getRequestInfo(), role, childAccount);
					});
				}
			}
			if (BooleanUtils.isTrue(account.getIsOnlyWorkflowCall())) {

				Boolean tempBol = account.getIsOnlyWorkflowCall();
				String tempApplicationNo = null != account.getGrbgApplicationNumber()
						? account.getGrbgApplicationNumber()
						: account.getGrbgApplication().getApplicationNo();
				String action = account.getWorkflowAction();
				String status = getStatusOrAction(action, true);
				String comment = account.getWorkflowComment();

				GarbageAccount accountTemp = objectMapper
						.convertValue(
								existingGarbageApplicationAccountsMap.get(
										null != account.getGrbgApplicationNumber() ? account.getGrbgApplicationNumber()
												: account.getGrbgApplication().getApplicationNo()),
								GarbageAccount.class);
				if (null == accountTemp) {
					throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS",
							"Garbage Account not found to run workflow.");
				}

				accountTemp.setIsOnlyWorkflowCall(tempBol);
				accountTemp.setGrbgApplicationNumber(tempApplicationNo);
				accountTemp.setWorkflowAction(action);
				accountTemp.setWorkflowComment(comment);
				accountTemp.setStatus(status);
				accountTemp.getGrbgApplication().setStatus(status);

				if (!(StringUtils.equals(account.getWorkflowAction(),
						GrbgConstants.WORKFLOW_ACTION_TEMPERORY_CLOSED))) {
					if (!CollectionUtils.isEmpty(accountTemp.getChildGarbageAccounts())) {
						accountTemp.getChildGarbageAccounts().stream().forEach(child -> {
							child.setWorkflowAction(action);
							child.setStatus(status);
							child.getGrbgApplication().setStatus(status);
						});
					}
				}

//				accountTemp.setChildGarbageAccounts(null);			// at a time only 1 app no provided for WF

				garbageAccountRequestTemp.getGarbageAccounts().add(accountTemp);
			} else if (StringUtils.equals(account.getWorkflowAction(), GrbgConstants.WORKFLOW_ACTION_INITIATE)
					|| StringUtils.equals(account.getWorkflowAction(),
							GrbgConstants.WORKFLOW_ACTION_PENDING_FOR_MODIFICATION)) {
				// this block will work only when update Account and action is INITIATE
				GarbageAccount accountTemp = objectMapper.convertValue(
						existingGarbageApplicationAccountsMap.get(account.getGrbgApplication().getApplicationNo()),
						GarbageAccount.class);
				if (null == accountTemp) {
					throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS", "Garbage Account not found to update.");
				}
				account.setGrbgApplication(accountTemp.getGrbgApplication());
				garbageAccountRequestTemp.getGarbageAccounts().add(account);
			} else {
				garbageAccountRequestTemp.getGarbageAccounts().add(account);
//				throw new CustomException("WRONG_INPUTS", "Input fields for workflow flag and action is incorrect.");
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
		map.put(GrbgConstants.WORKFLOW_ACTION_APPROVE, GrbgConstants.STATUS_APPROVED);
		map.put(GrbgConstants.WORKFLOW_ACTION_REJECT, GrbgConstants.STATUS_REJECTED);
		map.put(GrbgConstants.WORKFLOW_ACTION_CLOSE, GrbgConstants.STATUS_CLOSED);
		map.put(GrbgConstants.WORKFLOW_ACTION_TEMPERORY_CLOSED, GrbgConstants.STATUS_TEMPERORYCLOSED);

		if (!fetchValue) {
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
			String businessService = null;

			Set<String> userRoles = updateGarbageRequest.getRequestInfo().getUserInfo().getRoles().stream()
					.map(Role::getCode).collect(Collectors.toSet());

			for (GarbageAccount newGarbageAccount : updateGarbageRequest.getGarbageAccounts()) {

				if (!StringUtils.isEmpty(newGarbageAccount.getBusinessService())) {
					businessService = newGarbageAccount.getBusinessService();
				} else {
					if (userRoles.contains(GrbgConstants.USER_TYPE_CITIZEN)) {
						businessService = GrbgConstants.BUSINESS_SERVICE_GB_CITIZEN;
					} else {
						businessService = GrbgConstants.BUSINESS_SERVICE_GB_EMPLOYEE;
					}
				}

				newGarbageAccount.setBusinessService(businessService);

				if (!updateGarbageRequest.getCreateChildAccountOnly()) {
					ProcessInstance parentProcessInstance = ProcessInstance.builder()
							.tenantId(newGarbageAccount.getTenantId()).businessService(businessService)
							.moduleName(GrbgConstants.WORKFLOW_MODULE_NAME)
							.businessId(newGarbageAccount.getGrbgApplication().getApplicationNo())
							.action(null != newGarbageAccount.getWorkflowAction()
									? newGarbageAccount.getWorkflowAction()
									: getStatusOrAction(newGarbageAccount.getStatus(), false))
							.comment(newGarbageAccount.getWorkflowComment()).build();

					processInstances.add(parentProcessInstance);
				}
//				if ((StringUtils.equals(newGarbageAccount.getWorkflowAction(), GrbgConstants.WORKFLOW_ACTION_INITIATE)
//						|| StringUtils.equals(newGarbageAccount.getWorkflowAction(),
//								GrbgConstants.WORKFLOW_ACTION_VERIFY)
//						|| StringUtils.equals(newGarbageAccount.getWorkflowAction(),
//								GrbgConstants.WORKFLOW_ACTION_FORWARD_TO_VERIFIER)
//						|| StringUtils.equals(newGarbageAccount.getWorkflowAction(),
//								GrbgConstants.WORKFLOW_ACTION_APPROVE)
//						|| StringUtils.equals(newGarbageAccount.getWorkflowAction(),
//								GrbgConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR)
//						|| StringUtils.equals(newGarbageAccount.getWorkflowAction(),
//								GrbgConstants.WORKFLOW_ACTION_REJECT)
//						|| StringUtils.equals(newGarbageAccount.getWorkflowAction(),
//								GrbgConstants.WORKFLOW_ACTION_CLOSE))) {
				if (!(StringUtils.equals(newGarbageAccount.getWorkflowAction(),
						GrbgConstants.WORKFLOW_ACTION_TEMPERORY_CLOSED))) {
					if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
						for (GarbageAccount subAccount : newGarbageAccount.getChildGarbageAccounts()) {
							String action;
							if (updateGarbageRequest.getCreateChildAccountOnly()) {
								action = GrbgConstants.WORKFLOW_ACTION_INITIATE;
							} else {
								action = null != newGarbageAccount.getWorkflowAction()
										? newGarbageAccount.getWorkflowAction()
										: getStatusOrAction(newGarbageAccount.getStatus(), false);
							}
							ProcessInstance subProcessInstance = ProcessInstance.builder()
									.tenantId(subAccount.getTenantId()).businessService(businessService)
									.moduleName(GrbgConstants.WORKFLOW_MODULE_NAME)
									.businessId(subAccount.getGrbgApplication().getApplicationNo()).action(action)
									.comment(newGarbageAccount.getWorkflowComment()).build();

							processInstances.add(subProcessInstance);
						}
					}
				}

				if (!StringUtils.isEmpty(newGarbageAccount.getWorkflowAction()) && newGarbageAccount.getWorkflowAction()
						.equalsIgnoreCase(GrbgConstants.WORKFLOW_ACTION_APPROVE)) {
					newGarbageAccount.setApprovalDate(new Date().getTime());
					if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
						for (GarbageAccount subAccount : newGarbageAccount.getChildGarbageAccounts()) {
							subAccount.setApprovalDate(new Date().getTime());
						}
					}
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

			}

			processInstanceRequest = ProcessInstanceRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
					.processInstances(processInstances).build();

			// call workflow
			processInstanceResponse = workflowService.callWf(processInstanceRequest);

		}

		return processInstanceResponse;
	}

	private void updateAndEnrichGarbageAccountObjects(GarbageAccount newGarbageAccount,
			GarbageAccount existingGarbageAccount, Map<String, String> applicationNumberToCurrentStatus) {

		// 1. update application
		if (null != newGarbageAccount.getGrbgApplication()
				&& !newGarbageAccount.getGrbgApplication().equals(existingGarbageAccount.getGrbgApplication())) {
			// enrich application
			newGarbageAccount.getGrbgApplication().setUuid(existingGarbageAccount.getGrbgApplication().getUuid());
			newGarbageAccount.getGrbgApplication().setStatus(
					applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo()));
			// update application
			grbgApplicationRepository.update(newGarbageAccount.getGrbgApplication());
		}

		// 2. update commercial details
//		if (null != newGarbageAccount.getGrbgCommercialDetails()
//				&& StringUtils.isEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid())) {
//			// create commercial details
//			grbgCommercialDetailsRepository.create(newGarbageAccount.getGrbgCommercialDetails());
//		} else if (null != newGarbageAccount.getGrbgCommercialDetails()
//				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid()) && !newGarbageAccount
//						.getGrbgCommercialDetails().equals(existingGarbageAccount.getGrbgCommercialDetails())) {
//			// enrich
////			newGarbageAccount.getGrbgCommercialDetails().setUuid(existingGarbageAccount.getGrbgCommercialDetails().getUuid());
//			newGarbageAccount.getGrbgCommercialDetails()
//					.setGarbageId(existingGarbageAccount.getGrbgCommercialDetails().getGarbageId());
//			// update commercial details
//			grbgCommercialDetailsRepository.update(newGarbageAccount.getGrbgCommercialDetails());
//		}

		// 3. update grbgOldDetails
		if (null != newGarbageAccount.getGrbgOldDetails()
				&& StringUtils.isEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())) {
			// create grbgOldDetails
			grbgOldDetailsRepository.create(newGarbageAccount.getGrbgOldDetails());
		} else if (null != newGarbageAccount.getGrbgOldDetails()
				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())
				&& !newGarbageAccount.getGrbgOldDetails().equals(existingGarbageAccount.getGrbgOldDetails())) {
			// enrich
			if (null != existingGarbageAccount.getGrbgOldDetails()
					&& StringUtils.isNotEmpty(existingGarbageAccount.getGrbgOldDetails().getUuid())) {
				newGarbageAccount.getGrbgOldDetails().setUuid(existingGarbageAccount.getGrbgOldDetails().getUuid());
				newGarbageAccount.getGrbgOldDetails()
						.setGarbageId(existingGarbageAccount.getGrbgOldDetails().getGarbageId());
				// update grbgOldDetails
				grbgOldDetailsRepository.update(newGarbageAccount.getGrbgOldDetails());
			}
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
		if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
			newGarbageAccount.getChildGarbageAccounts().stream().forEach(child -> {
				garbageAccountRepository.update(child);
				// update application
				grbgApplicationRepository.update(child.getGrbgApplication());

				if (!CollectionUtils.isEmpty(child.getGrbgCollectionUnits())) {
					child.getGrbgCollectionUnits().stream().forEach(unit -> {
						grbgCollectionUnitRepository.update(unit);
					});
				}
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
		Map<String, GrbgCollectionUnit> grbgCollectionUnitsToDeactivate = existingGarbageAccount
				.getGrbgCollectionUnits().stream()
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
		if (!CollectionUtils.isEmpty(newGarbageAccount.getGrbgCollectionUnits())) {
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

	private Map<Long, GarbageAccount> searchGarbageAccountMap(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount,
			RequestInfo requestInfo) {

		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
				.builder().searchCriteriaGarbageAccount(searchCriteriaGarbageAccount).requestInfo(requestInfo).build();

		GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteriaGarbageAccountRequest,
				false);

		Map<Long, GarbageAccount> existingGarbageAccountsMap = new HashMap<>();
		garbageAccountResponse.getGarbageAccounts().stream().forEach(account -> {
			existingGarbageAccountsMap.put(account.getGarbageId(), account);
		});

		return existingGarbageAccountsMap;
	}

	private SearchCriteriaGarbageAccount createSearchCriteriaByGarbageAccounts(List<GarbageAccount> garbageAccounts) {

		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = SearchCriteriaGarbageAccount.builder().build();
		searchCriteriaGarbageAccount.setIsActiveAccount(true);
		searchCriteriaGarbageAccount.setIsActiveSubAccount(true);
//		List<Long> ids = new ArrayList<>();
		List<Long> garbageIds = new ArrayList<>();
		List<String> applicationNos = new ArrayList<>();

		garbageAccounts.stream().forEach(grbgAcc -> {
//			if(null != grbgAcc.getId() && 0 <= grbgAcc.getId()) {
//				ids.add(grbgAcc.getId());
//			}
			if (null != grbgAcc.getGarbageId() && 0 <= grbgAcc.getGarbageId()) {
				garbageIds.add(grbgAcc.getGarbageId());
			}
			if (!StringUtils.isEmpty(grbgAcc.getGrbgApplicationNumber())) {
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

	public GarbageAccountResponse searchGarbageAccounts(
			SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest, Boolean isIndex) {

		// validate search criteria
		validateAndEnrichSearchGarbageAccount(searchCriteriaGarbageAccountRequest);

//		if (null != searchCriteriaGarbageAccountRequest
//				&& null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
//			searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setIsActiveAccount(true);
//			searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setIsActiveSubAccount(true);
//		}

		List<GarbageAccount> grbgAccs = new ArrayList<>();
		Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap = new HashMap<>();
		Integer counter = 1;

		garbageCriteriaMap.put(counter++, searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());

		if (isCriteriaEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())
				&& null != searchCriteriaGarbageAccountRequest.getRequestInfo()
				&& null != searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo()
				&& searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getType()
						.equalsIgnoreCase(GrbgConstants.USER_TYPE_EMPLOYEE)) {
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccountCreatedBy = searchCriteriaGarbageAccountRequest
					.getSearchCriteriaGarbageAccount().copy();
			if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccountCreatedBy.getStatusList())) {
				searchCriteriaGarbageAccountCreatedBy.setStatusList(null);
			}
			searchCriteriaGarbageAccountCreatedBy.setCreatedBy(Collections
					.singletonList(searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getUuid()));

			garbageCriteriaMap.put(counter++, searchCriteriaGarbageAccountCreatedBy);
		}

		if (isCriteriaEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())
				&& null != searchCriteriaGarbageAccountRequest.getRequestInfo()
				&& null != searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo()
				&& searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getType()
						.equalsIgnoreCase(GrbgConstants.USER_TYPE_EMPLOYEE)) {

			List<String> rolesWithinTenant = getRolesByTenantId(
					searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getTenantId(),
					searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getRoles());

			for (String role : rolesWithinTenant) {
				if (role.equalsIgnoreCase(GrbgConstants.USER_ROLE_GB_VERIFIER)) {
					SearchCriteriaGarbageAccount garbageCriteriaFromExcel = searchCriteriaGarbageAccountRequest
							.getSearchCriteriaGarbageAccount().copy();
					if (!CollectionUtils.isEmpty(garbageCriteriaFromExcel.getStatusList())) {
						garbageCriteriaFromExcel.setStatusList(null);
					}
					garbageCriteriaFromExcel.setStatus(Collections.singletonList(GrbgConstants.STATUS_INITIATED));
					garbageCriteriaFromExcel.setChannels(Collections.singletonList(GrbgConstants.CHANNEL_TYPE_MIGRATE));

					garbageCriteriaMap.put(counter++, garbageCriteriaFromExcel);
				}
			}
		}

		// search garbage account
		if (isIndex)
			grbgAccs = garbageAccountRepository.searchGarbageAccountIndex(
					searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount(), garbageCriteriaMap);
		else
			grbgAccs = garbageAccountRepository.searchGarbageAccount(
					searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount(), garbageCriteriaMap);

		GarbageAccountResponse garbageAccountResponse = getSearchResponseFromAccounts(grbgAccs);

		if (CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
			garbageAccountResponse.setResponseInfo(responseInfoFactory
					.createResponseInfoFromRequestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo(), false));
		} else {
			garbageAccountResponse.setResponseInfo(responseInfoFactory
					.createResponseInfoFromRequestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo(), true));
		}

		return garbageAccountResponse;
	}

	private GarbageAccountResponse getSearchResponseFromAccounts(List<GarbageAccount> grbgAccs) {

		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder().garbageAccounts(grbgAccs)
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

	private void validateAndEnrichSearchGarbageAccount(
			SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		RequestInfo requestInfo = searchCriteriaGarbageAccountRequest.getRequestInfo();

		if (searchCriteriaGarbageAccountRequest.getIsSchedulerCall()) {
			if (null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
				searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
						.setIsSchedulerCall(searchCriteriaGarbageAccountRequest.getIsSchedulerCall());
			} else {
				searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(SearchCriteriaGarbageAccount
						.builder().isSchedulerCall(searchCriteriaGarbageAccountRequest.getIsSchedulerCall()).build());
			}
		}

		if (null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
			searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
					.setIsUserUuidNull(searchCriteriaGarbageAccountRequest.getIsUserUuidNull());
		}

		if (null != searchCriteriaGarbageAccountRequest.getIsSchedulerCall()
				&& !searchCriteriaGarbageAccountRequest.getIsSchedulerCall()) {
			if (null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
				if (CollectionUtils
						.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getId())
						&& CollectionUtils.isEmpty(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getGarbageId())
						&& CollectionUtils.isEmpty(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getPropertyId())
						&& CollectionUtils.isEmpty(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getType())
						&& CollectionUtils.isEmpty(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getName())
						&& CollectionUtils.isEmpty(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getMobileNumber())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
								.getApplicationNumber())
						&& null == searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getIsOwner()) {

					if (null != requestInfo && null != requestInfo.getUserInfo() && StringUtils
							.equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_CITIZEN)) {
						searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
//								.setCreatedBy(Collections.singletonList(requestInfo.getUserInfo().getUuid()))
						;
					} else if (null != requestInfo && null != requestInfo.getUserInfo() && StringUtils
							.equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_EMPLOYEE)) {

						List<String> listOfStatus = getAccountStatusListByRoles(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getTenantId(),
								requestInfo.getUserInfo().getRoles());
						if (!CollectionUtils.isEmpty(listOfStatus) && isCriteriaEmpty(
								searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())) {
							searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
									.setStatusList(listOfStatus);
						}
					} else {
						throw new CustomException("MISSING_SEARCH_PARAMETER",
								"Provide the parameters to search garbage accounts.");
					}
				}
			} else if (null != requestInfo && null != requestInfo.getUserInfo() && StringUtils
					.equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_CITIZEN)) {
				searchCriteriaGarbageAccountRequest
						.setSearchCriteriaGarbageAccount(SearchCriteriaGarbageAccount.builder()
//						.createdBy(Collections.singletonList(requestInfo.getUserInfo().getUuid()))
								.build());
			}
		}

	}

	public Boolean isCriteriaEmpty(SearchCriteriaGarbageAccount criteria) {
		Boolean isCriteriaEmpty = CollectionUtils.isEmpty(criteria.getId())
				&& CollectionUtils.isEmpty(criteria.getGarbageId()) && CollectionUtils.isEmpty(criteria.getPropertyId())
				&& CollectionUtils.isEmpty(criteria.getUuid()) && CollectionUtils.isEmpty(criteria.getType())
				&& CollectionUtils.isEmpty(criteria.getName()) && CollectionUtils.isEmpty(criteria.getMobileNumber())
				&& CollectionUtils.isEmpty(criteria.getApplicationNumber())
				&& CollectionUtils.isEmpty(criteria.getCreatedBy()) && CollectionUtils.isEmpty(criteria.getStatus());
		return isCriteriaEmpty;
	}

	private List<String> getAccountStatusListByRoles(String tenantId, List<Role> roles) {

		List<String> rolesWithinTenant = getRolesByTenantId(tenantId, roles);
		Set<String> statusWithRoles = new HashSet();

		rolesWithinTenant.stream().forEach(role -> {

			if (StringUtils.equalsIgnoreCase(role, GrbgConstants.USER_ROLE_GB_VERIFIER)) {
				statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORVERIFICATION);
				statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORMODIFICATION);
				statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORAPPROVAL);
				statusWithRoles.add(GrbgConstants.STATUS_APPROVED);
				statusWithRoles.add(GrbgConstants.STATUS_REJECTED);
			} else if (StringUtils.equalsIgnoreCase(role, GrbgConstants.USER_ROLE_GB_APPROVER)) {
				statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORAPPROVAL);
				statusWithRoles.add(GrbgConstants.STATUS_APPROVED);
				statusWithRoles.add(GrbgConstants.STATUS_PENDINGFORMODIFICATION);
				statusWithRoles.add(GrbgConstants.STATUS_REJECTED);
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
		if (!CollectionUtils.isEmpty(response.getGarbageAccounts())) {
			response.setApplicationCount((int) response.getGarbageAccounts().stream().count());
			response.setApplicationInitiated((int) response
					.getGarbageAccounts().stream().filter(account -> StringUtils
							.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_INITIATED, account.getStatus()))
					.count());
			response.setApplicationApplied((int) response.getGarbageAccounts().stream()
					.filter(account -> StringUtils.equalsAnyIgnoreCase(account.getStatus(),
							applicationPropertiesAndConstant.STATUS_PENDINGFORVERIFICATION,
							applicationPropertiesAndConstant.STATUS_PENDINGFORAPPROVAL,
							applicationPropertiesAndConstant.STATUS_PENDINGFORMODIFICATION))
					.count());
			response.setApplicationPendingForPayment(
					(int) response.getGarbageAccounts().stream()
							.filter(account -> StringUtils.equalsIgnoreCase(
									applicationPropertiesAndConstant.STATUS_PENDINGFORPAYMENT, account.getStatus()))
							.count());
			response.setApplicationRejected((int) response
					.getGarbageAccounts().stream().filter(account -> StringUtils
							.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_REJECTED, account.getStatus()))
					.count());
			response.setApplicationApproved((int) response
					.getGarbageAccounts().stream().filter(account -> StringUtils
							.equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_APPROVED, account.getStatus()))
					.count());
		}

	}

	public GarbageAccountActionResponse getApplicationDetails(GarbageAccountActionRequest garbageAccountActionRequest) {

		SearchCriteriaGarbageAccount criteria = SearchCriteriaGarbageAccount.builder().build();
		GarbageAccountActionResponse garbageAccountActionResponse = GarbageAccountActionResponse.builder()
				.applicationDetails(new ArrayList<>()).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(garbageAccountActionRequest.getRequestInfo(), true))
				.build();

		if (CollectionUtils.isEmpty(garbageAccountActionRequest.getApplicationNumbers())) {
			if (null != garbageAccountActionRequest.getRequestInfo()
					&& null != garbageAccountActionRequest.getRequestInfo().getUserInfo()
					&& !StringUtils.isEmpty(garbageAccountActionRequest.getRequestInfo().getUserInfo().getUuid())) {
				criteria.setUser_uuid(Collections
						.singletonList(garbageAccountActionRequest.getRequestInfo().getUserInfo().getUuid()));
			} else {
				throw new CustomException("INVALID REQUEST", "Provide Application Number.");
			}
		} else {
			criteria.setApplicationNumber(garbageAccountActionRequest.getApplicationNumbers());
		}

		if (!CollectionUtils.isEmpty(garbageAccountActionRequest.getPropertyIds())) {
			criteria.setPropertyId(garbageAccountActionRequest.getPropertyIds());
		}

		if (!CollectionUtils.isEmpty(garbageAccountActionRequest.getGarbageUuid())) {
			criteria.setUuid(garbageAccountActionRequest.getGarbageUuid());
		}

		criteria.setIsActiveAccount(true);
		criteria.setIsActiveSubAccount(true);

		// search application number
		List<GarbageAccount> accounts = garbageAccountRepository.searchGarbageAccount(criteria, null);

		List<GarbageAccountDetail> applicationDetails = getApplicationBillUserDetail(accounts,
				garbageAccountActionRequest.getRequestInfo(), garbageAccountActionRequest);

		garbageAccountActionResponse.setApplicationDetails(applicationDetails);

		return garbageAccountActionResponse;
	}

	private List<GarbageAccountDetail> getApplicationBillUserDetail(List<GarbageAccount> accounts,
			RequestInfo requestInfo, GarbageAccountActionRequest garbageAccountActionRequest) {

		List<GarbageAccountDetail> garbageAccountDetails = new ArrayList<>();

		accounts.stream().forEach(account -> {
			GarbageAccountDetail garbageAccountDetail = GarbageAccountDetail.builder()
					.applicationNumber(account.getGrbgApplication().getApplicationNo()).build();

			// search bill Details
			BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder().tenantId(account.getTenantId())
					.consumerCode(Collections.singleton(account.getGrbgApplication().getApplicationNo()))
					.service(null != account.getBusinessService() ? account.getBusinessService() : "GB")// business//
																										// service
					.build();
			BillResponse billResponse = billService.searchBill(billSearchCriteria, requestInfo);
			Map<Object, Object> billDetailsMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(billResponse.getBill())) {
				// enrich all bills

				if (!CollectionUtils.isEmpty(garbageAccountActionRequest.getBillStatus())) {
					List<Bill> finalBills = billResponse.getBill().stream().filter(
							bill -> garbageAccountActionRequest.getBillStatus().contains(bill.getStatus().name()))
							.collect(Collectors.toList());
					billResponse.setBill(finalBills);
				}

				if (!StringUtils.isEmpty(garbageAccountActionRequest.getMonth())
						&& !StringUtils.isEmpty(garbageAccountActionRequest.getYear())) {
					List<Bill> finalBillsAfterYearMonthFilter = new ArrayList<>();
					billResponse.getBill().forEach(bill -> {
						Instant instant = Instant.ofEpochMilli(bill.getBillDate());
						LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String formattedDate = dateTime.format(formatter);

						if (null != dateTime.getMonth()
								&& garbageAccountActionRequest.getMonth()
										.equalsIgnoreCase(dateTime.getMonth().toString())
								&& garbageAccountActionRequest.getYear()
										.equalsIgnoreCase(String.valueOf(dateTime.getYear()))) {
							finalBillsAfterYearMonthFilter.add(bill);
						}

					});
					billResponse.setBill(finalBillsAfterYearMonthFilter);
				}

				garbageAccountDetail.setBills(billResponse.getBill());

				Optional<Bill> activeBill = billResponse.getBill().stream()
						.filter(bill -> StatusEnum.ACTIVE.name().equalsIgnoreCase(bill.getStatus().name())).findFirst();
				activeBill.ifPresent(bill -> {
					// enrich active bill details
					billDetailsMap.put("billId", bill.getId());
					garbageAccountDetail.setTotalPayableAmount(bill.getTotalAmount());
				});

			} else {
				garbageAccountDetail.setTotalPayableAmount(new BigDecimal(100.00));
			}
			garbageAccountDetail.setBillDetails(billDetailsMap);

			// enrich formula
			if (!CollectionUtils.isEmpty(account.getGrbgCollectionUnits())) {
				garbageAccountDetail
						.setFeeCalculationFormula("category: (" + account.getGrbgCollectionUnits().get(0).getCategory()
								+ "), SubCategory: (" + account.getGrbgCollectionUnits().get(0).getSubCategory() + ")");
			}

			// enrich userDetails
			Map<Object, Object> userDetails = new HashMap<>();
			userDetails.put("UserName", account.getName());
			userDetails.put("MobileNo", account.getMobileNumber());
			userDetails.put("Email", account.getEmailId());
			userDetails.put("Address",
					new String(account.getAddresses().get(0).getAddress1().concat(", "))
							.concat(account.getAddresses().get(0).getPincode().concat(", "))
							.concat(account.getAddresses().get(0).getZone().concat(", "))
							.concat(account.getAddresses().get(0).getUlbName().concat(", "))
							.concat(account.getAddresses().get(0).getWardName().concat(", "))
							.concat(account.getAddresses().get(0).getAdditionalDetail().get("district").asText()));

			garbageAccountDetail.setUserDetails(userDetails);

			if (garbageAccountActionRequest.getIsEmptyBillFilter()) {
				if (!CollectionUtils.isEmpty(garbageAccountDetail.getBills())) {
					garbageAccountDetails.add(garbageAccountDetail);
				}
			} else {
				garbageAccountDetails.add(garbageAccountDetail);
			}
		});

		return garbageAccountDetails;
	}

	public GarbageAccountActionResponse getActionsOnApplication(
			GarbageAccountActionRequest garbageAccountActionRequest) {

		if (CollectionUtils.isEmpty(garbageAccountActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST", "Provide Application Number.");
		}

		Map<String, List<String>> applicationActionMaps = new HashMap<>();

		// search garbage accounts by application numbers
		SearchCriteriaGarbageAccount criteria = SearchCriteriaGarbageAccount.builder()
				.applicationNumber(garbageAccountActionRequest.getApplicationNumbers()).isActiveAccount(true)
				.isActiveSubAccount(true).build();
		List<GarbageAccount> accounts = garbageAccountRepository.searchGarbageAccount(criteria, null);
		if (CollectionUtils.isEmpty(accounts)) {
			throw new CustomException("GARBAGE_ACCOUNT_NOT_FOUND",
					"No Garbage Account found with given application number.");
		}
		Map<String, GarbageAccount> mapAccounts = accounts.stream()
				.collect(Collectors.toMap(acc -> acc.getGrbgApplication().getApplicationNo(), acc -> acc));

		String applicationTenantId = accounts.get(0).getTenantId();
		String applicationBusinessId = null;
		Set<String> userRoles = garbageAccountActionRequest.getRequestInfo().getUserInfo().getRoles().stream()
				.map(Role::getCode).collect(Collectors.toSet());

		if (userRoles.contains(GrbgConstants.USER_TYPE_CITIZEN)) {
			applicationBusinessId = GrbgConstants.BUSINESS_SERVICE_GB_CITIZEN;
		} else {
			applicationBusinessId = GrbgConstants.BUSINESS_SERVICE_GB_EMPLOYEE;
		}

		// fetch business service search
		BusinessServiceResponse businessServiceResponse = workflowService
				.businessServiceSearch(garbageAccountActionRequest, applicationTenantId, applicationBusinessId);

		if (null == businessServiceResponse || CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
			throw new CustomException("NO_BUSINESS_SERVICE_FOUND",
					"Business service not found for application numbers: "
							+ garbageAccountActionRequest.getApplicationNumbers().toString());
		}

		List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId,
				garbageAccountActionRequest.getRequestInfo().getUserInfo().getRoles());

		garbageAccountActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {

			String status = mapAccounts.get(applicationNumber).getStatus();
			List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
					.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), status)
							&& !StringUtils.equalsAnyIgnoreCase(state.getApplicationStatus(),
									applicationPropertiesAndConstant.STATUS_APPROVED))
					.collect(Collectors.toList());

			// filtering actions based on roles
			List<String> actions = new ArrayList<>();
			stateList.stream().forEach(state -> {
				state.getActions().stream()
						.filter(action -> action.getRoles().stream().anyMatch(role -> rolesWithinTenant.contains(role)))
						.forEach(action -> {
							actions.add(action.getAction());
						});
			});

			applicationActionMaps.put(applicationNumber, actions);
		});

		List<GarbageAccountDetail> garbageDetailList = new ArrayList<>();
		applicationActionMaps.entrySet().stream().forEach(entry -> {
			garbageDetailList.add(
					GarbageAccountDetail.builder().applicationNumber(entry.getKey()).action(entry.getValue()).build());
		});

		// build response
		GarbageAccountActionResponse garbageAccountActionResponse = GarbageAccountActionResponse.builder()
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(garbageAccountActionRequest.getRequestInfo(), true))
				.applicationDetails(garbageDetailList).build();
		return garbageAccountActionResponse;

	}

	private List<String> getRolesWithinTenant(String tenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}

	public GarbageAccountActionResponse payNowGrbgBill(PayNowRequest payNowRequest) {

		if (StringUtils.isEmpty(payNowRequest.getUserUuid())) {
			throw new CustomException("INVALID REQUEST", "Please Provide User Uuid.");
		}

		// validate user
		UserSearchResponse userSearchResponse = userService.searchUser(payNowRequest.getUserUuid());

		if (null == userSearchResponse || CollectionUtils.isEmpty(userSearchResponse.getUserSearchResponseContent())) {
			throw new CustomException("USER NOT FOUND", "User not found for given user uuid.");
		}

		GarbageAccountActionRequest garbageAccountActionRequest = GarbageAccountActionRequest.builder()
				.applicationNumbers(payNowRequest.getGarbageApplicationNumbers())
				.billStatus(payNowRequest.getBillStatus()).month(payNowRequest.getMonth()).year(payNowRequest.getYear())
				.propertyIds(payNowRequest.getPropertyIds()).garbageUuid(payNowRequest.getGarbageUuid())
				.requestInfo(RequestInfo.builder().userInfo(User.builder().uuid(payNowRequest.getUserUuid()).build())
						.build())
				.build();

		GarbageAccountActionResponse garbageAccountActionResponse = getApplicationDetails(garbageAccountActionRequest);

		return garbageAccountActionResponse;
	}

	public GrbgBillTrackerRequest enrichGrbgBillTrackerCreateRequest(GarbageAccount garbageAccount,
			GenerateBillRequest generateBillRequest, BigDecimal billAmount, Bill bill,
			ObjectNode calculationBreakdown) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		AuditDetails createAuditDetails = grbgUtils.buildCreateAuditDetails(generateBillRequest.getRequestInfo());
		GrbgBillTracker grbgBillTracker = GrbgBillTracker.builder().uuid(UUID.randomUUID().toString())
				.grbgApplicationId(garbageAccount.getGrbgApplicationNumber()).tenantId(garbageAccount.getTenantId())
				.month(null != generateBillRequest.getMonth() ? generateBillRequest.getMonth().toUpperCase() : null)
				.year(generateBillRequest.getYear())
				.fromDate(
						null != generateBillRequest.getFromDate() ? dateFormat.format(generateBillRequest.getFromDate())
								: null)
				.toDate(null != generateBillRequest.getToDate() ? dateFormat.format(generateBillRequest.getToDate())
						: null)
				.type(generateBillRequest.getType()).additionaldetail(calculationBreakdown)
				.ward(garbageAccount.getAddresses().get(0).getWardName()).billId(bill.getId())
				.grbgBillAmount(billAmount).auditDetails(createAuditDetails).build();

		return GrbgBillTrackerRequest.builder().requestInfo(generateBillRequest.getRequestInfo())
				.grbgBillTracker(grbgBillTracker).build();
	}

	public GrbgBillFailure enrichGrbgBillFailure(GarbageAccount garbageAccount, GenerateBillRequest generateBillRequest,
			BillResponse billResponse, List<String> errorList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode response_payload = mapper.valueToTree(billResponse);
		JsonNode request_payload = mapper.valueToTree(generateBillRequest);
		String failure_reason = null;
		GrbgBillFailure grbgBillFailure = GrbgBillFailure.builder()
				.consumer_code(garbageAccount.getGrbgApplicationNumber()).tenant_id(garbageAccount.getTenantId())
				.from_date(
						null != generateBillRequest.getFromDate() ? dateFormat.format(generateBillRequest.getFromDate())
								: null)
				.id(UUID.randomUUID()).module_name("GB").failure_reason(failure_reason)
				.month(null != generateBillRequest.getMonth() ? generateBillRequest.getMonth().toUpperCase() : null)
				.request_payload(request_payload).response_payload(response_payload).status_code("400")
				.created_time(new Date().getTime()).last_modified_time(new Date().getTime()).error_json(errorList)
				.to_date(null != generateBillRequest.getToDate() ? dateFormat.format(generateBillRequest.getToDate())
						: null)
				.year(generateBillRequest.getYear()).build();
		return grbgBillFailure;
	}

	public GrbgBillTracker saveToGarbageBillTracker(GrbgBillTrackerRequest grbgBillTrackerRequest) {

		return garbageBillTrackerRepository.createTracker(grbgBillTrackerRequest.getGrbgBillTracker());
	}

	public GrbgBillFailure saveToGarbageBillFailure(GrbgBillFailure grbgBillFailureRequest) {

		return garbageBillTrackerRepository.createBillFailure(grbgBillFailureRequest);
	}

	public List<GrbgBillTracker> getBillCalculatedGarbageAccounts(
			GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria) {

		return garbageBillTrackerRepository.getBillTracker(grbgBillTrackerSearchCriteria);
	}

	public void createUserForGarbage(SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		// Create GarbageAccountRequest object

		GarbageAccountRequest createGarbageRequest = buildGarbageAccountRequest(
				RequestInfoWrapper.builder().requestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo()).build());

		// Fetch garbage accounts if available
		List<GarbageAccount> garbageAccounts = fetchGarbageAccounts(searchCriteriaGarbageAccountRequest);
		// Set garbage accounts if available
		if (!garbageAccounts.isEmpty()) {
			createGarbageRequest.setGarbageAccounts(garbageAccounts);
			createGarbageRequest = userService.createUser(createGarbageRequest); // Create user if garbage accounts
																					// exist
		}

		List<GarbageAccount> allAccounts = createGarbageRequest.getGarbageAccounts();
		int batchSize = 100;

		for (int i = 0; i < allAccounts.size(); i += batchSize) {
			int end = Math.min(i + batchSize, allAccounts.size());
			List<GarbageAccount> batchList = allAccounts.subList(i, end);

			// Create a new request for the current batch
			GarbageAccountRequest batchRequest = new GarbageAccountRequest();
			batchRequest.setGarbageAccounts(batchList);
			batchRequest.setRequestInfo(createGarbageRequest.getRequestInfo()); // if needed

			update(batchRequest);
		}
	}

	private GarbageAccountRequest buildGarbageAccountRequest(RequestInfoWrapper requestInfoWrapper) {
		return GarbageAccountRequest.builder().requestInfo(requestInfoWrapper.getRequestInfo()).build();
	}

	private List<GarbageAccount> fetchGarbageAccounts(
			SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {

		searchCriteriaGarbageAccountRequest.setIsSchedulerCall(true);
		searchCriteriaGarbageAccountRequest.setIsUserUuidNull(true);
		searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setIsActiveAccount(true);
		searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().setIsActiveSubAccount(true);

		GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteriaGarbageAccountRequest,
				false);

		if (garbageAccountResponse != null && !CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
			return garbageAccountResponse.getGarbageAccounts();
		}
		return Collections.emptyList(); // Return empty list if no garbage accounts found
	}

	public GarbageAccountResponse delete(GarbageAccountRequest deleteGarbageRequest) {

		deleteGarbageRequest.getGarbageAccounts().stream().forEach(garbageAccount -> {
//			deleteGarbageAccountObjects(garbageAccount); // TODO
			garbageAccountRepository.delete(garbageAccount);
		});

		return null;
	}

	private void deleteGarbageAccountObjects(GarbageAccount garbageAccount) {
//		// delete garbage application
//		grbgApplicationRepository.delete(garbageAccount.getGarbageId());
//
//		// delete garbage address
//		deleteGarbageAddress(garbageAccount.getGarbageId());
//
//		// create old garbage details
//		deleteGarbageOldDetails(garbageAccount.getGarbageId());
//
//		// create garbage unit
//		deleteGarbageUnit(garbageAccount.getGarbageId());

//		// enrich garbage document
////				enrichCreateGarbageDocuments(garbageAccount);
//
//		// create garbage documents
////				createGarbageDocuments(garbageAccount);
	}

	private void deleteGarbageAddress(Long garbageId) {
		grbgAddressRepository.delete(garbageId);
	}

	private void deleteGarbageOldDetails(Long garbageId) {
		grbgOldDetailsRepository.delete(garbageId);
	}

	private void deleteGarbageUnit(Long garbageId) {
		grbgCollectionUnitRepository.delete(garbageId);
	}

	public Map<String, Object> totalCount(TotalCountRequest totalCountRequest) {

		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(totalCountRequest.getRequestInfo(),
				true);
		Map<String, Object> response = new HashMap<>();
		if (hasRequiredRole(totalCountRequest.getRequestInfo(), "EMPLOYEE")) {
			List<Map<String, Object>> result = garbageAccountRepository.getStatusCounts(totalCountRequest);
			response.put("ResponseInfo", resInfo);
			response.put("Counts", result.get(0));
			return response;
		} else if (hasRequiredRole(totalCountRequest.getRequestInfo(), "CITIZEN")) {

		}
		return response;
		// grbgAccs = garbageAccountRepository.getStatusCounts(totalCountRequest);
	}

	public boolean hasRequiredRole(RequestInfo requestInfo, String type) {
		if (requestInfo == null || requestInfo.getUserInfo() == null) {
			return false;
		}

		List<Role> roles = requestInfo.getUserInfo().getRoles();
		if (roles == null || roles.isEmpty()) {
			return false;
		}

		if ("CITIZEN".equalsIgnoreCase(type)) {
			return roles.stream().anyMatch(role -> type.equalsIgnoreCase(role.getCode()));
		}

		return roles.stream()
				.anyMatch(role -> containsIgnoreCase(role.getCode(), "PROPERTY_APPROVER")
						|| containsIgnoreCase(role.getCode(), "PROPERTY_VERIFIER")
						|| containsIgnoreCase(role.getCode(), "EMPLOYEE"));
	}

	private boolean containsIgnoreCase(String source, String target) {
		return source != null && source.toLowerCase().contains(target.toLowerCase());
	}

	public ResponseEntity<Resource> generateGrbgTaxBillReceipt(RequestInfoWrapper requestInfoWrapper,
			@Valid String grbgId, @Valid String billid) {

		List<GarbageAccount> garbageAccounts = Collections
				.singletonList(GarbageAccount.builder().grbgApplicationNumber(grbgId).build());

		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(
				garbageAccounts);

		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
				.builder().searchCriteriaGarbageAccount(searchCriteriaGarbageAccount)
				.requestInfo(requestInfoWrapper.getRequestInfo()).build();

		GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteriaGarbageAccountRequest,
				false);

		GarbageAccount grbAccount = garbageAccountResponse.getGarbageAccounts().stream().findFirst().orElse(null);
		if (null == grbAccount) {
			return null;
		}

		GrbgBillTrackerSearchCriteria grbgTrackerMonthSearchCriteria = GrbgBillTrackerSearchCriteria.builder()
				.grbgApplicationIds(Collections.singleton(grbgId)).billIds(Collections.singleton(billid)).build();

		List<GrbgBillTracker> grbgTaxCalculatorMonth = getBillCalculatedGarbageAccounts(grbgTrackerMonthSearchCriteria);

		GrbgBillTracker grbgTaxCalculatorMonthTracker = grbgTaxCalculatorMonth.stream().findFirst().orElse(null);
		if (null == grbgTaxCalculatorMonthTracker) {
			return null;
		}

		int conut = 1;
		List<String> slNos = new ArrayList<>();
		Set<String> garbapplicationNos = new HashSet<>();

		garbapplicationNos.add(grbAccount.getGrbgApplicationNumber());

		for (GarbageAccount childGrbgAccount : grbAccount.getChildGarbageAccounts()) {
			slNos.add(String.valueOf(conut++));
			garbapplicationNos.add(childGrbgAccount.getGrbgApplicationNumber());

		}
		GrbgBillTrackerSearchCriteria grbgTrackerSearchCriteria = GrbgBillTrackerSearchCriteria.builder()
				.type(grbgTaxCalculatorMonthTracker.getType()).grbgApplicationIds(garbapplicationNos).month(grbgTaxCalculatorMonthTracker.getMonth())
				.build();

		List<GrbgBillTracker> grbgTaxCalculatorTracker = getBillCalculatedGarbageAccounts(grbgTrackerSearchCriteria);

		// If no records, return null or empty set
		if (grbgTaxCalculatorTracker == null || grbgTaxCalculatorTracker.isEmpty()) {
			return null;
		}

		// Collect all bill IDs (assuming you have getBillId() or similar method)
		Set<String> billIds = grbgTaxCalculatorTracker.stream().map(GrbgBillTracker::getBillId)
				.collect(Collectors.toSet());

		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.tenantId(grbgTaxCalculatorMonthTracker.getTenantId()).service(grbAccount.getBusinessService())
				.billId(billIds).build();

		BillResponse billResponse = billService.searchBill(billSearchCriteria, requestInfoWrapper.getRequestInfo());

		if (null == billResponse || CollectionUtils.isEmpty(billResponse.getBill())) {
			return null;
		}

		List<Bill> bill = billResponse.getBill();

		// return null;
		PDFRequest pdfRequest = pdfRequestGenerator.generatePdfRequestForBill(requestInfoWrapper, grbAccount, bill,
				grbgTaxCalculatorTracker);

		return reportService.createNoSavePDF(pdfRequest);

	}

	public GarbageAccountResponse updateStatus(GarbageAccountRequest updateGarbageRequest) {

		List<GarbageAccount> garbageAccounts = new ArrayList<>();
		// search existing garbage accounts
		Map<Long, GarbageAccount> existingGarbageIdAccountsMap;
		Map<String, GarbageAccount> existingGarbageApplicationAccountsMap;
		try {
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(
					updateGarbageRequest.getGarbageAccounts());
			existingGarbageIdAccountsMap = searchGarbageAccountMap(searchCriteriaGarbageAccount,
					updateGarbageRequest.getRequestInfo());
			existingGarbageApplicationAccountsMap = existingGarbageIdAccountsMap.entrySet().stream().collect(
					Collectors.toMap(a -> a.getValue().getGrbgApplication().getApplicationNo(), b -> b.getValue()));
		} catch (Exception e) {
			throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS", "Search Garbage account details failed.");
		}

		// load garbage account from backend if workflow = true
		GarbageAccountRequest garbageAccountRequest = loadUpdateGarbageAccountRequestFromMap(updateGarbageRequest,
				existingGarbageApplicationAccountsMap);

		ProcessInstanceResponse processInstanceResponse = null;
		// call workflow
		if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
				&& updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
			processInstanceResponse = callWfUpdateStatus(garbageAccountRequest);
		}
		Map<String, String> applicationNumberToCurrentStatus = new HashMap<>();
		if (null != processInstanceResponse) {
			applicationNumberToCurrentStatus = processInstanceResponse.getProcessInstances().stream().collect(Collectors
					.toMap(ProcessInstance::getBusinessId, instance -> instance.getState().getApplicationStatus()));
		}

		// update garbage account
		if (!CollectionUtils.isEmpty(garbageAccountRequest.getGarbageAccounts())) {
			garbageAccountRequest.getGarbageAccounts().stream().forEach(newGarbageAccount -> {

//				if(!newGarbageAccount.getIsOnlyWorkflowCall()) {
				// validate garbage account request
				validateGarbageAccount(newGarbageAccount, existingGarbageIdAccountsMap.entrySet().stream()
						.map(entry -> entry.getValue()).collect(Collectors.toList()));

			});

			for (GarbageAccount newGarbageAccount : garbageAccountRequest.getGarbageAccounts()) {
//			garbageAccountRequest.getGarbageAccounts().stream().forEach(newGarbageAccount -> {

				// get existing garbage account from map
				GarbageAccount existingGarbageAccount = existingGarbageIdAccountsMap
						.get(newGarbageAccount.getGarbageId());

				// enrich garbage account
				enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount,
						updateGarbageRequest.getRequestInfo(), applicationNumberToCurrentStatus);

				// update garbage account
				if (!newGarbageAccount.equals(existingGarbageAccount)) {
					updateGarbageAccount(updateGarbageRequest, newGarbageAccount, existingGarbageAccount,
							applicationNumberToCurrentStatus);
				}

				// update other objects of garbage account
				updateAndEnrichGarbageAccountObjects(newGarbageAccount, existingGarbageAccount,
						applicationNumberToCurrentStatus);

				garbageAccounts.add(newGarbageAccount);
			}

		}

		if (!updateGarbageRequest.getFromMigration()) {
			// generate certificate and upload

			createAndUploadPDF(garbageAccountRequest, updateGarbageRequest.getRequestInfo());

			// generate demand and fetch bill
			// generateDemandAndBill(garbageAccountRequest);
		}

		// RESPONSE builder
		GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), false))
				.garbageAccounts(garbageAccounts).build();
		if (!CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccountResponse.setResponseInfo(responseInfoFactory
					.createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), true));
		}

		return garbageAccountResponse;
	}

	private ProcessInstanceResponse callWfUpdateStatus(GarbageAccountRequest updateGarbageRequest) {

		ProcessInstanceResponse processInstanceResponse = null;

		if (!CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())) {

			ProcessInstanceRequest processInstanceRequest = null;
			List<ProcessInstance> processInstances = new ArrayList<>();
			String businessService = null;

			Set<String> userRoles = updateGarbageRequest.getRequestInfo().getUserInfo().getRoles().stream()
					.map(Role::getCode).collect(Collectors.toSet());

			for (GarbageAccount newGarbageAccount : updateGarbageRequest.getGarbageAccounts()) {

				if (!StringUtils.isEmpty(newGarbageAccount.getBusinessService())) {
					businessService = newGarbageAccount.getBusinessService();
				} else {
					if (userRoles.contains(GrbgConstants.USER_TYPE_CITIZEN)) {
						businessService = GrbgConstants.BUSINESS_SERVICE_GB_CITIZEN;
					} else {
						businessService = GrbgConstants.BUSINESS_SERVICE_GB_EMPLOYEE;
					}
				}

				newGarbageAccount.setBusinessService(businessService);
				ProcessInstance parentProcessInstance = ProcessInstance.builder()
						.tenantId(newGarbageAccount.getTenantId()).businessService(businessService)
						.moduleName(GrbgConstants.WORKFLOW_MODULE_NAME)
						.businessId(newGarbageAccount.getGrbgApplication().getApplicationNo())
						.action(null != newGarbageAccount.getWorkflowAction() ? newGarbageAccount.getWorkflowAction()
								: getStatusOrAction(newGarbageAccount.getStatus(), false))
						.comment(newGarbageAccount.getWorkflowComment()).build();

				processInstances.add(parentProcessInstance);

			}

			processInstanceRequest = ProcessInstanceRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
					.processInstances(processInstances).build();
			// call workflow
			processInstanceResponse = workflowService.callWf(processInstanceRequest);

		}

		return processInstanceResponse;
	}

	public void removeGarbageBillFailure(GrbgBillFailure grbgBillFailureRequest) {

		garbageBillTrackerRepository.removeBillFailure(grbgBillFailureRequest);
	}

	protected List<Demand> createArearDemand(Demand demand, GarbageAccount garbageAccount) {
		return Collections.singletonList(demand);
	}

	public Map<String, Object> generateArrear(GenrateArrearRequest genrateArrearRequest) {
		String message = null;
		List<String> ListOfConsumerCode = new ArrayList<>();
		ListOfConsumerCode.add(genrateArrearRequest.getDemands().get(0).getConsumerCode());
		List<String> setOfStatuses = new ArrayList<>();
		setOfStatuses.add("APPROVED");
//		PropertyCriteria pptcriteria = PropertyCriteria.builder().propertyIds(setOfConsumerCode)
//				.tenantId(genrateArrearRequest.getDemands().get(0).getTenantId()).status(setOfStatuses).build();
		SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = SearchCriteriaGarbageAccount.builder()
				.applicationNumber(ListOfConsumerCode).tenantId(genrateArrearRequest.getDemands().get(0).getTenantId())
				.status(setOfStatuses).build();
		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
				.builder().requestInfo(genrateArrearRequest.getRequestInfo())
				.searchCriteriaGarbageAccount(searchCriteriaGarbageAccount).build();
		GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteriaGarbageAccountRequest,
				true);
		if (!CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
			GarbageAccount garbageAccount = garbageAccountResponse.getGarbageAccounts().get(0);
//			checkPropertyArears(genrateArrearRequest.getDemands(), properties.get(0));
			genrateArrearRequest.getDemands().stream().forEach(demand -> {

				Map<String, Object> additionalDetails = (Map<String, Object>) demand.getAdditionalDetails();

				// if null, initialize
				if (additionalDetails == null) {
					additionalDetails = new HashMap<>();
				}
				additionalDetails.put("type", "ARREAR");
				additionalDetails.put("name", garbageAccount.getName());
				additionalDetails.put("ward", garbageAccount.getAddresses().get(0).getWardName());
				additionalDetails.put("category", garbageAccount.getGrbgCollectionUnits().get(0).getCategory());
				additionalDetails.put("mobileNumber", garbageAccount.getMobileNumber());
				additionalDetails.put("subCategoryType", garbageAccount.getGrbgCollectionUnits().get(0).getSubCategoryType());
				demand.setAdditionalDetails(additionalDetails);
				Calendar cal = Calendar.getInstance();

				cal.add(Calendar.DAY_OF_MONTH,
						Integer.valueOf(applicationPropertiesAndConstant.getGrbgBillExpiryAfter()));
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
				demand.setFixedBillExpiryDate(cal.getTimeInMillis());
				demand.setPayer(User.builder().uuid(garbageAccount.getUserUuid()).build());

				List<Demand> savedDemands = demandRepository.saveDemand(genrateArrearRequest.getRequestInfo(),
						createArearDemand(demand, garbageAccount));
				if (CollectionUtils.isEmpty(savedDemands)) {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");
				}
				GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
						.tenantId(garbageAccount.getTenantId()).businessService("GB")
						.consumerCode(garbageAccount.getGrbgApplicationNumber()).build();
				BillResponse billResponse = billService.generateBill(genrateArrearRequest.getRequestInfo(),
						billCriteria);
				if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
					ObjectNode calculationBreakdown = objectMapper.createObjectNode();
					calculationBreakdown.put("fee", demand.getMinimumAmountPayable());
					calculationBreakdown.put("subCategoryType", garbageAccount.getGrbgCollectionUnits().get(0).getSubCategoryType());
					GenerateBillRequest generateBillRequest = GenerateBillRequest.builder()
							.requestInfo(genrateArrearRequest.getRequestInfo())
							.fromDate(new Date(demand.getTaxPeriodFrom())).toDate(new Date(demand.getTaxPeriodTo()))
							.year(getFinancialYearFromTimestamps(demand.getTaxPeriodFrom(), demand.getTaxPeriodTo()))
							.type("ARREAR").build();
					GrbgBillTrackerRequest grbgBillTrackerRequest = enrichGrbgBillTrackerCreateRequest(garbageAccount,
							generateBillRequest, demand.getMinimumAmountPayable(), billResponse.getBill().get(0),
							calculationBreakdown);
					GrbgBillTracker grbgBillTracker = saveToGarbageBillTracker(grbgBillTrackerRequest);
				}else {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");				}
			});
			message = "Arear Generated Successfully";
		} else {
			message = "Invalid Garbage Details";
		}
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(genrateArrearRequest.getRequestInfo(), true);
		Map<String, Object> response = new HashMap<>();
		response.put("ResponseInfo", resInfo);
		response.put("message", message);
		return response;
	}

	public static String getFinancialYearFromTimestamps(long timestamp1, long timestamp2) {
		// Pick the earlier date between the two
		Date date1 = new Date(timestamp1);
		Date date2 = new Date(timestamp2);

		Date earlierDate = date1.before(date2) ? date1 : date2;

		Calendar cal = Calendar.getInstance();
		cal.setTime(earlierDate);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH); // 0 = Jan, 3 = April

		int fyStartYear;
		if (month >= Calendar.APRIL) {
			// If April or after, FY starts this year
			fyStartYear = year;
		} else {
			// If before April, FY started last year
			fyStartYear = year - 1;
		}

		int fyEndYear = fyStartYear + 1;

		return fyStartYear + "-" + (fyEndYear % 100); // e.g., "2023-24"
	}

}
