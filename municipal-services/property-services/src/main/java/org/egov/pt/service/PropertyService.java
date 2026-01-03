package org.egov.pt.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.egov.pt.models.enums.BillStatus;


import org.egov.pt.service.DemandService;
import javax.validation.Valid;
import org.egov.pt.models.bill.Demand.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MdmsResponse;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyBillFailure;
import org.egov.pt.models.PropertyBookingDetail;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.PropertySearchRequest;
import org.egov.pt.models.PropertySearchResponse;
import org.egov.pt.web.contracts.CancelPropertyBillRequest;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.PtTaxCalculatorTrackerSearchCriteria;
import org.egov.pt.models.bill.BillSearchCriteria;

import org.egov.pt.models.bill.Demand;
import org.egov.pt.models.bill.DemandDetail;
import org.egov.pt.models.bill.GenerateBillCriteria;
import org.egov.pt.models.collection.Bill;
import org.egov.pt.models.collection.BillDetail;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.oldProperty.PropertyInfo;
import org.egov.pt.models.report.PDFRequest;
import org.egov.pt.models.report.PDFRequestGenerator;
import org.egov.pt.models.user.UserDetailResponse;
import org.egov.pt.models.user.UserSearchRequest;
import org.egov.pt.models.user.UserSearchResponse;
import org.egov.pt.models.workflow.BusinessServiceResponse;
import org.egov.pt.models.workflow.State;
import org.egov.pt.producer.PropertyProducer;
import org.egov.pt.repository.DemandRepository;
import org.egov.pt.repository.OwnersRepository;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.EncryptionDecryptionUtil;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.util.RequestInfoUtils;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.util.UnmaskingUtil;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.web.contracts.CreateObPassUserRequest;
import org.egov.pt.web.contracts.GenrateArrearRequest;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.PropertyResponse;
import org.egov.pt.web.contracts.PropertyStatusUpdateRequest;
import org.egov.pt.web.contracts.PtTaxCalculatorTrackerRequest;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.pt.web.contracts.TotalCountRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import org.egov.common.contract.request.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper; 
import org.egov.pt.web.contracts.UpdatePropertyBillCriteria;



@Slf4j
@Service
public class PropertyService {

	@Autowired
	private BillingService billingService;

	@Autowired
	private UnmaskingUtil unmaskingUtil;

	@Autowired
	private PropertyProducer producer;

	@Autowired
	private NotificationService notifService;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private PropertyUtil util;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CalculationService calculatorService;
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private FuzzySearchService fuzzySearchService;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private OwnersRepository ownersRepository;

	@Autowired
	private ReportService reportService;

	@Autowired
	private PDFRequestGenerator pdfRequestGenerator;

	@Autowired
	private BillService billService;

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private RequestInfoUtils requestInfoUtils;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request PropertyRequest containing list of properties to be created
	 * @return List of properties successfully created
	 */
	public Property createProperty(PropertyRequest request) {

		propertyValidator.validateCreateRequest(request);
		enrichmentService.enrichCreateRequest(request);
		userService.createUser(request);
		if (config.getIsWorkflowEnabled()
				&& !request.getProperty().getCreationReason().equals(CreationReason.DATA_UPLOAD)) {
			wfService.updateWorkflow(request, request.getProperty().getCreationReason());

		} else {

			request.getProperty().setStatus(Status.ACTIVE);
		}

		/*
		 * Fix this. For FUZZY-search, This code to be un-commented when privacy is
		 * enabled
		 * 
		 * //Push PLAIN data to fuzzy search index
		 * producer.push(config.getSavePropertyFuzzyTopic(), request);
		 *
		 */
		// Push data after encryption
		producer.pushAfterEncrytpion(config.getSavePropertyTopic(), request);
		request.getProperty().setWorkflow(null);

		/* decrypt here */
		return encryptionDecryptionUtil.decryptObject(request.getProperty(), PTConstants.PROPERTY_MODEL, Property.class,
				request.getRequestInfo());
		// return request.getProperty();
	}

	/**
	 * Updates the property
	 *
	 * handles multiple processes
	 *
	 * Update
	 *
	 * Mutation
	 *
	 * @param request PropertyRequest containing list of properties to be update
	 * @return List of updated properties
	 */
	public Property updateProperty(PropertyRequest request, Boolean isStatusUpdate) {

		Property propertyFromSearch = unmaskingUtil.getPropertyUnmasked(request);
		propertyValidator.validateCommonUpdateInformation(request, propertyFromSearch);

		boolean isRequestForOwnerMutation = CreationReason.MUTATION.equals(request.getProperty().getCreationReason());

//		boolean isOwnerUpdate = checkIsRequestForOwnerUpdate(request, propertyFromSearch);

		boolean isRequestForStatusChange = CreationReason.STATUS.equals(request.getProperty().getCreationReason());

		if (isRequestForOwnerMutation)
			processOwnerMutation(request, propertyFromSearch);

		else {
			if (isStatusUpdate)
				processOwnerUpdate(request, propertyFromSearch);
			if (isRequestForStatusChange) {
				BillResponse billResponse = billingService.fetchBill(request.getProperty(), request.getRequestInfo());

				BigDecimal dueAmount = new BigDecimal("0");
				if (billResponse != null && billResponse.getBill() != null && billResponse.getBill().size() >= 1) {
					dueAmount = billResponse.getBill().get(0).getTotalAmount();
				}

				log.info("No. of Active Bills===" + ((billResponse != null && billResponse.getBill() != null
						&& billResponse.getBill().size() >= 1) ? billResponse.getBill().size() : "0"));
				log.info("Amount Due is " + dueAmount);
				if (dueAmount.compareTo(BigDecimal.ZERO) > 0)
					throw new CustomException("EG_PT_ERROR_ACTIVE_BILL_PRESENT",
							"Clear Pending dues before De-Enumerating the property");

				else
					processPropertyUpdate(request, propertyFromSearch, isStatusUpdate);

			} else {
				processPropertyUpdate(request, propertyFromSearch, isStatusUpdate);
			}
		}

		request.getProperty().setWorkflow(null);

		// Push PLAIN data to fuzzy search index
		PropertyRequest fuzzyPropertyRequest = new PropertyRequest(request.getRequestInfo(), request.getProperty(),
				false);
		fuzzyPropertyRequest.setProperty(encryptionDecryptionUtil.decryptObject(request.getProperty(),
				PTConstants.PROPERTY_DECRYPT_MODEL, Property.class, request.getRequestInfo()));

		/*
		 * Fix this. For FUZZY-search, This code to be un-commented when privacy is
		 * enabled
		 * 
		 * //Push PLAIN data to fuzzy search index
		 * producer.push(config.getSavePropertyFuzzyTopic(), fuzzyPropertyRequest);
		 *
		 */

		/* decrypt here */
		return encryptionDecryptionUtil.decryptObject(request.getProperty(), PTConstants.PROPERTY_MODEL, Property.class,
				request.getRequestInfo());
	}

	/*
	 * Method to check if the update request is for updating owner mobile numbers
	 */

	private boolean checkIsRequestForOwnerUpdate(PropertyRequest request, Property propertyFromSearch) {
		Map<String, String> uuidToMobileNumber = new HashMap<String, String>();
		Map<String, String> uuidToName = new HashMap<String, String>();
		Map<String, String> uuidToEmail = new HashMap<String, String>();
		List<OwnerInfo> owners = propertyFromSearch.getOwners();

		for (OwnerInfo owner : owners) {
			uuidToMobileNumber.put(owner.getUuid(), owner.getMobileNumber());
			uuidToName.put(owner.getUuid(), owner.getName());
			uuidToEmail.put(owner.getUuid(), owner.getEmailId());
		}

		List<OwnerInfo> ownersFromRequest = request.getProperty().getOwners();

		Boolean isUpdate = false;

		for (OwnerInfo owner : ownersFromRequest) {
			if (uuidToMobileNumber.containsKey(owner.getUuid())
					&& (!uuidToMobileNumber.get(owner.getUuid()).equals(owner.getMobileNumber())
							|| !uuidToName.get(owner.getUuid()).equals(owner.getName())
							|| (!StringUtils.isEmpty(owner.getEmailId())
									&& !uuidToEmail.get(owner.getUuid()).equals(owner.getEmailId())))) {
				isUpdate = true;
				break;
			}
		}

		return isUpdate;
	}

	/*
	 * Method to process owner mobile number update
	 */

	private void processOwnerUpdate(PropertyRequest request, Property propertyFromSearch) {

		if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
			userService.createUser(request);
		} else {
			userService.createUser(request);
			// userService.updateOwnerDetails(request);
		}

		enrichmentService.enrichUpdateRequest(request, propertyFromSearch);
		util.mergeAdditionalDetails(request, propertyFromSearch);
		producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
	}

	/**
	 * Method to process Property update
	 *
	 * @param request
	 * @param propertyFromSearch
	 */
	private void processPropertyUpdate(PropertyRequest request, Property propertyFromSearch, Boolean updateOwnerName) {

		propertyValidator.validateRequestForUpdate(request, propertyFromSearch);
		if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
			userService.createUser(request);
		} else if (request.getProperty().getSource().toString().equals("WS")
				&& CreationReason.UPDATE.equals(request.getProperty().getCreationReason())) {
			userService.updateUser(request);
//		} else {
//			request.getProperty().setOwners(util.getCopyOfOwners(propertyFromSearch.getOwners()));
		} else if (CreationReason.UPDATE.equals(request.getProperty().getCreationReason())) {
			if (request.getProperty().getOwners().size() > propertyFromSearch.getOwners().size()) {
				request.getProperty().getOwners().forEach(owner -> {

					if (owner.getOwnerInfoUuid() == null) {
						owner.setOwnerInfoUuid(UUID.randomUUID().toString());
						if (!CollectionUtils.isEmpty(owner.getDocuments()))
							owner.getDocuments().forEach(doc -> {
								doc.setId(UUID.randomUUID().toString());
								doc.setStatus(Status.ACTIVE);
							});

						owner.setStatus(Status.ACTIVE);
					}

				});
				userService.createUser(request);
			}
		}

		enrichmentService.enrichAssignes(request.getProperty());
		enrichmentService.enrichUpdateRequest(request, propertyFromSearch);

		PropertyRequest OldPropertyRequest = PropertyRequest.builder().requestInfo(request.getRequestInfo())
				.property(propertyFromSearch).build();

		util.mergeAdditionalDetails(request, propertyFromSearch);

		if (config.getIsWorkflowEnabled()) {

			State state = wfService.updateWorkflow(request, CreationReason.UPDATE);

			if (state.getIsStartState() == true
					&& state.getApplicationStatus().equalsIgnoreCase(Status.INWORKFLOW.toString())
					&& !propertyFromSearch.getStatus().equals(Status.INWORKFLOW)) {

				propertyFromSearch.setStatus(Status.INACTIVE);
				producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), OldPropertyRequest);
				util.saveOldUuidToRequest(request, propertyFromSearch.getId());
				producer.pushAfterEncrytpion(config.getSavePropertyTopic(), request);

			} else if (state.getIsTerminateState()
					&& !state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString())) {

				terminateWorkflowAndReInstatePreviousRecord(request, propertyFromSearch);
			} else {
				/*
				 * If property is In Workflow then continue
				 */
				if (!updateOwnerName) {
					request.getProperty().getOwners().forEach(owner -> {
						owner.setName(owner.getPropertyOwnerName());
					});
				}

				producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
			}

		} else {

			/*
			 * If no workflow then update property directly with mutation information
			 */
			producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
		}
	}

	/*
	 * Method to update owners mobile number
	 */

//	private void updateOwnerDetails(PropertyRequest request, Property propertyFromSearch) {
//
//		Map<String, String> uuidToMobileNumber = new HashMap<String, String>();
//		List<OwnerInfo> owners = propertyFromSearch.getOwners();
//
//		for (OwnerInfo owner : owners) {
//			uuidToMobileNumber.put(owner.getUuid(), owner.getMobileNumber());
//		}
//
//		userService.updateOwnerDetails(request);
//		notifService.sendNotificationForMobileNumberUpdate(request, propertyFromSearch, uuidToMobileNumber);
//	}

	/**
	 * method to process owner mutation
	 *
	 * @param request
	 * @param propertyFromSearch
	 */
	private void processOwnerMutation(PropertyRequest request, Property propertyFromSearch) {

		propertyValidator.validateMutation(request, propertyFromSearch);
		userService.createUserForMutation(request, !propertyFromSearch.getStatus().equals(Status.INWORKFLOW));
		enrichmentService.enrichAssignes(request.getProperty());
		enrichmentService.enrichMutationRequest(request, propertyFromSearch);
		calculatorService.calculateMutationFee(request.getRequestInfo(), request.getProperty());

		// TODO FIX ME block property changes FIXME
		util.mergeAdditionalDetails(request, propertyFromSearch);
		PropertyRequest oldPropertyRequest = PropertyRequest.builder().requestInfo(request.getRequestInfo())
				.property(propertyFromSearch).build();

		if (config.getIsMutationWorkflowEnabled()) {

			State state = wfService.updateWorkflow(request, CreationReason.MUTATION);

			/*
			 * updating property from search to INACTIVE status
			 *
			 * to create new entry for new Mutation
			 */
			if (state.getIsStartState() == true
					&& state.getApplicationStatus().equalsIgnoreCase(Status.INWORKFLOW.toString())
					&& !propertyFromSearch.getStatus().equals(Status.INWORKFLOW)) {

				propertyFromSearch.setStatus(Status.INACTIVE);
				producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), oldPropertyRequest);

				util.saveOldUuidToRequest(request, propertyFromSearch.getId());
				/* save new record */
				producer.pushAfterEncrytpion(config.getSavePropertyTopic(), request);

			} else if (state.getIsTerminateState()
					&& !state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString())) {

				terminateWorkflowAndReInstatePreviousRecord(request, propertyFromSearch);
			} else {
				/*
				 * If property is In Workflow then continue
				 */
				producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
			}

		} else {

			/*
			 * If no workflow then update property directly with mutation information
			 */
			producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
		}
	}

	private void terminateWorkflowAndReInstatePreviousRecord(PropertyRequest request, Property propertyFromSearch) {

		/* current record being rejected */
		producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);

		/* Previous record set to ACTIVE */
		@SuppressWarnings("unchecked")
		Map<String, Object> additionalDetails = mapper.convertValue(propertyFromSearch.getAdditionalDetails(),
				Map.class);
		if (null == additionalDetails)
			return;

		String propertyUuId = (String) additionalDetails.get(PTConstants.PREVIOUS_PROPERTY_PREVIOUD_UUID);
		if (StringUtils.isEmpty(propertyUuId))
			return;

		PropertyCriteria criteria = PropertyCriteria.builder().uuids(Sets.newHashSet(propertyUuId))
				.isSearchInternal(true).tenantId(propertyFromSearch.getTenantId()).build();
		Property previousPropertyToBeReInstated = searchProperty(criteria, request.getRequestInfo(), null).get(0);
		previousPropertyToBeReInstated.setAuditDetails(
				util.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid().toString(), true));
		previousPropertyToBeReInstated.setStatus(Status.ACTIVE);
		request.setProperty(previousPropertyToBeReInstated);

		producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
	}

	public List<Property> enrichProperty(List<Property> properties, RequestInfo requestInfo) {
		log.info("In Fetch Bill for Defaulter notice +++++++++++++++++++");

		BillResponse billResponse = new BillResponse();
		List<Property> defaulterProperties = new ArrayList<>();
		for (Property property : properties) {
			try {
				billResponse = billingService.fetchBill(property, requestInfo);
			} catch (Exception e) {
				log.info("Error occured while fetch bill for Property " + property.getPropertyId());
				properties.remove(property);
				enrichProperty(properties, requestInfo);
				defaulterProperties.clear();
			}
			if (billResponse.getBill().size() >= 1) {
				if (billResponse.getBill().get(0).getBillDetails().size() == 1) {
					Long fromDate = billResponse.getBill().get(0).getBillDetails().get(0).getFromPeriod();
					Long toDate = billResponse.getBill().get(0).getBillDetails().get(0).getToPeriod();
					int fromYear = new Date(fromDate).getYear() + 1900;
					int toYear = new Date(toDate).getYear() + 1900;
					if (!(fromYear == (new Date().getYear()) || toYear == (new Date().getYear()))) {
						property.setDueAmount(billResponse.getBill().get(0).getTotalAmount().toString());
						String assessmentYear = fromYear + "-" + toYear + "(Rs."
								+ billResponse.getBill().get(0).getTotalAmount().toString() + ")";
						property.setDueAmountYear(assessmentYear);
						defaulterProperties.add(property);
					}
				} else {
					property.setDueAmount(billResponse.getBill().get(0).getTotalAmount().toString());
					String assessmentYear = null;
					for (BillDetail bd : billResponse.getBill().get(0).getBillDetails()) {
						Long fromDate = bd.getFromPeriod();
						Long toDate = bd.getToPeriod();
						int fromYear = new Date(fromDate).getYear() + 1900;
						int toYear = new Date(toDate).getYear() + 1900;
						assessmentYear = assessmentYear == null
								? fromYear + "-" + toYear + "(Rs." + bd.getAmount() + ")"
								: assessmentYear.concat(",")
										.concat(fromYear + "-" + toYear + "(Rs." + bd.getAmount() + ")");
					}
					property.setDueAmountYear(assessmentYear);

					defaulterProperties.add(property);
				}

			}
			log.info("Property Id " + property.getPropertyId() + " added in defaulter notice with Due "
					+ property.getDueAmount() + " for year " + property.getDueAmountYear());

		}
		return defaulterProperties;

	}

	/**
	 * Search property with given PropertyCriteria
	 *
	 * @param criteria PropertyCriteria containing fields on which search is based
	 * @return list of properties satisfying the containing fields in criteria
	 */
	public List<Property> searchProperty(PropertyCriteria criteria, RequestInfo requestInfo,
			Map<Integer, PropertyCriteria> propertyCriteriaMap) {

		List<Property> properties;
		/* encrypt here */
		if (!criteria.getIsRequestForOldDataEncryption())
			criteria = encryptionDecryptionUtil.encryptObject(criteria, PTConstants.PROPERTY_MODEL,
					PropertyCriteria.class);

		/*
		 * throw error if audit request is with no proeprty id or multiple propertyids
		 */
		if (criteria.isAudit() && (CollectionUtils.isEmpty(criteria.getPropertyIds())
				|| (!CollectionUtils.isEmpty(criteria.getPropertyIds()) && criteria.getPropertyIds().size() > 1))) {

			throw new CustomException("EG_PT_PROPERTY_AUDIT_ERROR",
					"Audit can only be provided for a single propertyId");
		}

		if (!criteria.getIsRequestForDuplicatePropertyValidation()
				&& (criteria.getDoorNo() != null || criteria.getOldPropertyId() != null)) {
			properties = fuzzySearchService.getProperties(requestInfo, criteria);
		} else {
			properties = repository.getPropertiesWithOwnerInfo(criteria, requestInfo, false, propertyCriteriaMap);
		}

		/* Decrypt here */
		if (criteria.getIsSearchInternal())
			return encryptionDecryptionUtil.decryptObject(properties, PTConstants.PROPERTY_DECRYPT_MODEL,
					Property.class, requestInfo);
		else if (!criteria.getIsRequestForOldDataEncryption())
			return encryptionDecryptionUtil.decryptObject(properties, PTConstants.PROPERTY_MODEL, Property.class,
					requestInfo);

		return properties;
	}

	private void filterPropertiesForUser(List<Property> properties, Set<String> ownerIds) {

		List<Property> propertiesToBeRemoved = new ArrayList<>();

		for (Property property : properties) {

			boolean isOwnerPresent = false;

			for (OwnerInfo owner : property.getOwners()) {

				if (ownerIds.contains(owner.getUuid())) {
					isOwnerPresent = true;
					break;
				}
			}
			if (!isOwnerPresent)
				propertiesToBeRemoved.add(property);
		}
		properties.removeAll(propertiesToBeRemoved);
	}

	public List<Property> searchPropertyPlainSearch(PropertyCriteria criteria, RequestInfo requestInfo) {
		List<Property> properties = getPropertiesPlainSearch(criteria, requestInfo);
		for (Property property : properties)
			enrichmentService.enrichBoundary(property, requestInfo);
		return properties;
	}

	List<Property> getPropertiesPlainSearch(PropertyCriteria criteria, RequestInfo requestInfo) {

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			criteria.setLimit(config.getMaxSearchLimit());
		if (criteria.getLimit() == null)
			criteria.setLimit(config.getDefaultLimit());
		if (criteria.getOffset() == null)
			criteria.setOffset(config.getDefaultOffset());
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		if (criteria.getUuids() != null || criteria.getPropertyIds() != null) {
			if (criteria.getUuids() != null)
				propertyCriteria.setUuids(criteria.getUuids());
			if (criteria.getPropertyIds() != null)
				propertyCriteria.setPropertyIds(criteria.getPropertyIds());

		} else if (criteria.getIsRequestForOldDataEncryption()) {
			propertyCriteria.setTenantIds(criteria.getTenantIds());
		} else {
			List<String> uuids = repository.fetchIds(criteria, true);
			if (uuids.isEmpty())
				return Collections.emptyList();
			propertyCriteria.setUuids(new HashSet<>(uuids));
		}
		propertyCriteria.setLimit(criteria.getLimit());
		List<Property> properties = repository.getPropertiesForBulkSearch(propertyCriteria, true);
		if (properties.isEmpty())
			return Collections.emptyList();
		Set<String> ownerIds = properties.stream().map(Property::getOwners).flatMap(List::stream)
				.map(OwnerInfo::getUuid).collect(Collectors.toSet());

		UserSearchRequest userSearchRequest = userService.getBaseUserSearchRequest(criteria.getTenantId(), requestInfo);
		userSearchRequest.setUuid(ownerIds);
		UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);
		util.enrichOwner(userDetailResponse, properties, false);
		return properties;
	}

	public Property addAlternateNumber(PropertyRequest request) {

		Property propertyFromSearch = unmaskingUtil.getPropertyUnmasked(request);
		propertyValidator.validateAlternateMobileNumberInformation(request, propertyFromSearch);
		userService.createUserForAlternateNumber(request);

		request.getProperty().setAlternateUpdated(true);

		Map<String, String> uuidToAlternateMobileNumber = new HashMap<String, String>();
		List<OwnerInfo> owners = propertyFromSearch.getOwners();

		for (OwnerInfo owner : owners) {

			if (owner.getAlternatemobilenumber() != null) {
				uuidToAlternateMobileNumber.put(owner.getUuid(), owner.getAlternatemobilenumber());
			} else {
				uuidToAlternateMobileNumber.put(owner.getUuid(), " ");
			}
		}

		notifService.sendNotificationForAlternateNumberUpdate(request, propertyFromSearch, uuidToAlternateMobileNumber);

		// enrichmentService.enrichUpdateRequest(request, propertyFromSearch);
		util.mergeAdditionalDetails(request, propertyFromSearch);

		producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);

		request.getProperty().setWorkflow(null);

		return request.getProperty();
	}

	public Integer count(RequestInfo requestInfo, @Valid PropertyCriteria propertyCriteria) {
		propertyCriteria.setIsInboxSearch(false);
		Integer count = repository.getCount(propertyCriteria, requestInfo);
		return count;
	}

	public PropertySearchResponse getActionsOnApplication(PropertySearchRequest request) {
		Map<String, PropertyInfo> mapBookings = new HashMap<>();
		String applicationTenantId = StringUtils.EMPTY;
		String businessId = StringUtils.EMPTY;
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		List<PropertyBookingDetail> propertyBookingDetailList = new ArrayList<>();
		PropertySearchResponse response = new PropertySearchResponse();
		try {

			if (CollectionUtils.isEmpty(request.getApplicationNumbers())
					|| StringUtils.isEmpty(request.getBusinessService())) {
				throw new CustomException("INVALID REQUEST", "Provide Application Number and Business Service.");
			}
			if (StringUtils.equalsAnyIgnoreCase(request.getBusinessService(),
					PropertyUtil.BUSINESS_SERVICE_PROPERTY_BOOKING)) {
				Set<String> propertyIds = new HashSet<>(request.getApplicationNumbers());
				PropertyCriteria criteria = PropertyCriteria.builder().propertyIds(propertyIds).build();
				List<PropertyInfo> propertySearch = repository.getPropertyId(criteria);
				if (CollectionUtils.isEmpty(propertySearch)) {
					throw new CustomException("PROPERTY_NOT_FOUND", "No Property found with given application number.");
				}

				mapBookings = propertySearch.stream().collect(Collectors.toMap(acc -> acc.getPropertyId(), acc -> acc));
				applicationTenantId = propertySearch.get(0).getTenantId();
				businessId = PropertyUtil.BUSINESS_SERVICE_PROPERTY_BOOKING;
			}
			BusinessServiceResponse businessServiceResponse = wfService.businessServiceSearch(request,
					applicationTenantId, businessId);

			if (null == businessServiceResponse
					|| CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
				throw new CustomException("NO_BUSINESS_SERVICE_FOUND",
						"Business service not found for application numbers: "
								+ request.getApplicationNumbers().toString());
			}
			List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId,
					request.getRequestInfo().getUserInfo().getRoles(), request.getBusinessService());

			for (int i = 0; i < request.getApplicationNumbers().size(); i++) {
				String applicationNumber = request.getApplicationNumbers().get(i);
				final String status;
				if (StringUtils.equalsIgnoreCase(request.getBusinessService(),
						PropertyUtil.BUSINESS_SERVICE_PROPERTY_BOOKING)) {
					status = mapBookings.get(applicationNumber).getStatus().toString();
				} else {
					throw new CustomException("UNKNOWN_BUSINESS_SERVICE", "Provide the correct business service id.");
				}
				List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
						.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), status)
								&& !StringUtils.equalsAnyIgnoreCase(state.getApplicationStatus(),
										PropertyUtil.STATUS_RESOLVED))
						.collect(Collectors.toList());

				List<String> actions = new ArrayList<>();
				stateList.stream().forEach(state -> {
					state.getActions().stream().filter(
							action -> action.getRoles().stream().anyMatch(role -> rolesWithinTenant.contains(role)))
							.forEach(action -> {
								actions.add(action.getAction());
							});
				});

				applicationActionMaps.put(applicationNumber, actions);
			}
			applicationActionMaps.entrySet().stream().forEach(entry -> {
				propertyBookingDetailList.add(PropertyBookingDetail.builder().applicationNumber(entry.getKey())
						.action(entry.getValue()).build());
			});

			response = PropertySearchResponse.builder()
					.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.applicationDetails(propertyBookingDetailList).build();

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	private List<String> getRolesWithinTenant(String applicationTenantId, List<Role> roles, String businessService) {

		List<String> roleCodes = new ArrayList<>();
		try {
			if (StringUtils.equalsIgnoreCase(businessService, PropertyUtil.BUSINESS_SERVICE_PROPERTY_BOOKING)) {
				for (Role role : roles) {
					if (StringUtils.equalsIgnoreCase(role.getCode(), PropertyUtil.PROPERTY_CFC)) {
						roleCodes.add(PropertyUtil.PROPERTY_CFC);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), PropertyUtil.PROPERTY_CITIZEN)) {
						roleCodes.add(PropertyUtil.PROPERTY_CITIZEN);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), PropertyUtil.PROPERTY_CSR)) {
						roleCodes.add(PropertyUtil.PROPERTY_CSR);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), PropertyUtil.PROPERTY_DGRO)) {
						roleCodes.add(PropertyUtil.PROPERTY_DGRO);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), PropertyUtil.PROPERTY_GRO)) {
						roleCodes.add(PropertyUtil.PROPERTY_GRO);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), PropertyUtil.PROPERTY_PGR_LME)) {
						roleCodes.add(PropertyUtil.PROPERTY_PGR_LME);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return roleCodes;
	}

	public Property updateStatus(@Valid PropertyStatusUpdateRequest request) {

		List<Property> properties = searchProperty(
				PropertyCriteria.builder().propertyIds(Collections.singleton(request.getPropertyId())).build(),
				request.getRequestInfo(), null);

		if (null == properties || CollectionUtils.isEmpty(properties)) {
			throw new CustomException("PROPERTY NOT FOUND", "No Property found with given property id.");
		}

		Property property = properties.get(0);
		property.setWorkflow(request.getWorkflow());

		PropertyRequest propertyRequest = PropertyRequest.builder().property(property)
				.requestInfo(request.getRequestInfo()).build();

		return updateProperty(propertyRequest, false);
	}

	public void setAllCount(List<Property> properties, PropertyResponse response) {

		if (!CollectionUtils.isEmpty(properties)) {
			for (Property property : properties) {
				if (property.getStatus().equals(Status.INITIATED)) {
					response.setApplicationInitiated(response.getApplicationInitiated() + 1);
				} else if (property.getStatus().equals(Status.PENDINGFORVERIFICATION)) {
					response.setApplicationPendingForVerification(response.getApplicationPendingForVerification() + 1);
				} else if (property.getStatus().equals(Status.PENDINGFORMODIFICATION)) {
					response.setApplicationPendingForModification(response.getApplicationPendingForModification() + 1);
				} else if (property.getStatus().equals(Status.PENDINGFORAPPROVAL)) {
					response.setApplicationPendingForApproval(response.getApplicationPendingForApproval() + 1);
				} else if (property.getStatus().equals(Status.APPROVED)) {
					response.setApplicationApproved(response.getApplicationApproved() + 1);
				} else if (property.getStatus().equals(Status.REJECTED)) {
					response.setApplicationRejected(response.getApplicationRejected() + 1);
				}
			}
		}

	}

	public PtTaxCalculatorTracker saveToPtTaxCalculatorTracker(
			PtTaxCalculatorTrackerRequest ptTaxCalculatorTrackerRequest) {

		producer.push(config.getSavePropertyTaxCalculatorTrackerTopic(), ptTaxCalculatorTrackerRequest);

		return ptTaxCalculatorTrackerRequest.getPtTaxCalculatorTracker();
	}

	public PtTaxCalculatorTracker updatePtTaxCalculatorTracker(
			PtTaxCalculatorTrackerRequest ptTaxCalculatorTrackerRequest) {

		log.info("propertyId tracker update {}", ptTaxCalculatorTrackerRequest);
		producer.push(config.getUpdatePropertyTaxCalculatorTrackerTopic(), ptTaxCalculatorTrackerRequest);

		return ptTaxCalculatorTrackerRequest.getPtTaxCalculatorTracker();
	}

	public List<PtTaxCalculatorTracker> getTaxCalculatedProperties(
			PtTaxCalculatorTrackerSearchCriteria ptTaxCalculatorTrackerSearchCriteria) {

		return repository.getTaxCalculatedProperties(ptTaxCalculatorTrackerSearchCriteria);
	}

	public List<String> getTaxCalculatedTenantIds(
			PtTaxCalculatorTrackerSearchCriteria ptTaxCalculatorTrackerSearchCriteria) {

		return repository.getTaxCalculatedTenantIds(ptTaxCalculatorTrackerSearchCriteria);
	}

	public boolean isCriteriaEmpty(PropertyCriteria propertyCriteria) {
		return propertyValidator.isCriteriaEmpty(propertyCriteria);
	}

	public Map<String, Object> totalCount(TotalCountRequest totalCountRequest) {
		Map<String, Object> return1 = new HashMap<>();
		if (hasRequiredRole(totalCountRequest.getRequestInfo(), "EMPLOYEE")) {
			List<Map<String, Object>> result = repository.getStatusCounts(totalCountRequest);
			return result.get(0);

		} else if (hasRequiredRole(totalCountRequest.getRequestInfo(), "CITIZEN")) {

		}

		return return1;

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

	public List<OwnerInfo> updateExistingOwnerDetails(RequestInfoWrapper requestInfoWrapper) {
		// Fetch all the owners from the repository
		List<OwnerInfo> owners = ownersRepository.getAllPropertyOwners();

		// Extract all the UUIDs of owners
		Set<String> ownerUuids = owners.stream().map(OwnerInfo::getUuid).collect(Collectors.toSet());

		// Create a user search request to retrieve the user details for the owners
		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.requestInfo(requestInfoWrapper.getRequestInfo()).uuid(ownerUuids).build();

		// Fetch the user details in a map (UUID -> User)
		Map<String, User> uuidToUserMap = userService.searchUser(userSearchRequest);

		// Iterate over the owners and update their details with user info
		owners.forEach(owner -> {
			User user = uuidToUserMap.get(owner.getUuid());
			if (user != null) {
				owner.setName(user.getName());
				owner.setMobileNumber(user.getMobileNumber());
			} else {
				// Handle case where the user isn't found if necessary (e.g., log or default
				// values)
				log.warn("User not found for owner UUID: " + owner.getUuid());
			}
		});

		// Now we update owners in batches of 100
		final int BATCH_SIZE = 100;
		int totalOwners = owners.size();
		for (int i = 0; i < totalOwners; i += BATCH_SIZE) {
			// Calculate the end index for the batch (ensure we don't go out of bounds)
			int end = Math.min(i + BATCH_SIZE, totalOwners);

			// Get a sublist of owners for this batch
			List<OwnerInfo> batchOwners = owners.subList(i, end);

			// Update the batch in the repository
			ownersRepository.updateOwnersBatch(batchOwners);
		}

		return owners;
	}

	public ResponseEntity<Resource> generatePropertyTaxBillReceipt(RequestInfoWrapper requestInfoWrapper,
			@Valid String propertyId, @Valid String billId, String status) {

		PropertyCriteria propertyCriteria = PropertyCriteria.builder().isSchedulerCall(true)
				.propertyIds(Collections.singleton(propertyId)).build();

		List<Property> properties = searchProperty(propertyCriteria, requestInfoWrapper.getRequestInfo(), null);

		Property property = properties.stream().findFirst().orElse(null);
		if (null == property) {
			return null;
		}

		PtTaxCalculatorTrackerSearchCriteria ptTaxCalculatorTrackerSearchCriteria = PtTaxCalculatorTrackerSearchCriteria
				.builder().billId(billId).propertyIds(Collections.singleton(propertyId)).limit(1).build();

		List<PtTaxCalculatorTracker> ptTaxCalculatorTrackers = getTaxCalculatedProperties(
				ptTaxCalculatorTrackerSearchCriteria);

		PtTaxCalculatorTracker ptTaxCalculatorTracker = ptTaxCalculatorTrackers.stream().findFirst().orElse(null);
		if (null == ptTaxCalculatorTracker) {
			return null;
		}

		BillSearchCriteria.BillSearchCriteriaBuilder builder =
		        BillSearchCriteria.builder()
		                .tenantId(ptTaxCalculatorTracker.getTenantId())
		                .billId(Collections.singleton(ptTaxCalculatorTracker.getBillId()));
		
		if (status != null && !status.trim().isEmpty()) {
		    Demand.StatusEnum dynamicStatus =
		            Demand.StatusEnum.valueOf(status.trim().toUpperCase());
		    builder.status(dynamicStatus);
		}
		
		BillSearchCriteria billSearchCriteria = builder.build();
		
	

		BillResponse billResponse = billService.searchBill(billSearchCriteria, requestInfoWrapper.getRequestInfo());

		if (null == billResponse || CollectionUtils.isEmpty(billResponse.getBill())) {
			return null;
		}

		Bill bill = billResponse.getBill().stream().findFirst().orElse(null);
		if (null == bill) {
			return null;
		}

		MdmsResponse mdmsResponse = mdmsService.getPropertyReabateDaysMdmsData(requestInfoWrapper.getRequestInfo(),
				null);

		Map<String, Integer> tenantIdDaysMap = getUlbDaysMap(mdmsResponse);

		PDFRequest pdfRequest = pdfRequestGenerator.generatePdfRequest(requestInfoWrapper, property,
				ptTaxCalculatorTracker, bill, tenantIdDaysMap);

		return reportService.createNoSavePDF(pdfRequest);
	}

	public Boolean cancelPropertyBill(CancelPropertyBillRequest cancelRequest) {
		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.tenantId(cancelRequest.getTenantId())
				.billId(Collections.singleton(cancelRequest.getBillId()))
				.service(PTConstants.MODULE_PROPERTY)
				.isActive(true)
				.isCancelled(false)
				.build();

		BillResponse billResponse = billService.searchBill(billSearchCriteria, cancelRequest.getRequestInfo());

		if (billResponse == null || CollectionUtils.isEmpty(billResponse.getBill())) {
			throw new CustomException("INVALID UPDATE","No Bill exists for billId: " + cancelRequest.getBillId());
		}

		Bill bill = billResponse.getBill().get(0);

		// Only single demand allowed
		if (bill.getBillDetails() == null || bill.getBillDetails().size() != 1) {
			throw new CustomException("INVALID UPDATE", "Multiple demand cancellation is not allowed");
		}

		//Cancel demand
		bill.getBillDetails().forEach(bd -> {
			demandService.cancelDemand(bd.getTenantId(), Collections.singleton(bd.getDemandId()),
					cancelRequest.getRequestInfo(), bill.getBusinessService());
		});

		//Add cancellation reason
		Map<String, Object> additionalDetails = new HashMap<>();
		additionalDetails.put("reason", cancelRequest.getReason());
		additionalDetails.put("reasonMessage", cancelRequest.getReason());

		JsonNode addDetailsNode = objectMapper.valueToTree(additionalDetails);

		// Update bill status
		UpdatePropertyBillCriteria updateBillCriteria = UpdatePropertyBillCriteria.builder().tenantId(bill.getTenantId())
				.consumerCodes(Collections.singleton(bill.getConsumerCode()))
				.billIds(Collections.singleton(bill.getId()))
				.statusToBeUpdated(StatusEnum.CANCELLED)
				.businessService(bill.getBusinessService()).additionalDetails(addDetailsNode).build();
		billService.updateBillStatus(updateBillCriteria, cancelRequest.getRequestInfo());
		
		// UPDATE PT TRACKER STATUS
		PtTaxCalculatorTrackerSearchCriteria trackerSearchCriteria =
		        PtTaxCalculatorTrackerSearchCriteria.builder()
		                .tenantId(bill.getTenantId())
		                .billId(bill.getId())
		                .limit(1)
		                .build();

		List<PtTaxCalculatorTracker> trackers =
				repository.getTaxCalculatedProperties(trackerSearchCriteria);

		if (!CollectionUtils.isEmpty(trackers)) {
		    PtTaxCalculatorTracker tracker = trackers.get(0);
		    tracker.setBillStatus(BillStatus.CANCELLED);

		    PtTaxCalculatorTrackerRequest trackerRequest =
		            PtTaxCalculatorTrackerRequest.builder()
		                    .requestInfo(cancelRequest.getRequestInfo())
		                    .ptTaxCalculatorTracker(tracker)
		                    .build();

		    propertyService.updatePtTaxCalculatorTracker(trackerRequest);
		} else {
		    log.warn("PT tracker not found for billId {}", bill.getId());
		}
		 return true;  
	}

	public Map<String, Integer> getUlbDaysMap(MdmsResponse mdmsResponse) {
		return Optional.ofNullable(mdmsResponse).map(MdmsResponse::getMdmsRes)
				.map(mdmsRes -> mdmsRes.get(PTConstants.MDMS_MODULE_ULBS)).map(mapper::valueToTree).map(ulbsNode -> {
					JsonNode jsonNode = (JsonNode) ulbsNode; // explicit cast
					return jsonNode.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYREBATEDAYS);
				}).filter(JsonNode::isArray)
				.map(rebateDaysNode -> StreamSupport.stream(rebateDaysNode.spliterator(), false)
						.collect(Collectors.toMap(
								node -> config.getStateLevelTenantId() + "." + node.get("ulbName").asText(),
								node -> node.get("days").asInt())))
				.orElseGet(HashMap::new);
	}

	public Map<String, Object> checkMastersStatus(RequestInfoWrapper requestInfoWrapper, String UlbName) {
		List<Map<String, Object>> result = repository.getPropertyMastersStatus(UlbName);
		return result.get(0);
	}

	public String generateArrear(GenrateArrearRequest genrateArrearRequest) {
		String message = null;
		Set<String> setOfConsumerCode = new HashSet<>();
		setOfConsumerCode.add(genrateArrearRequest.getDemands().get(0).getConsumerCode());
		Set<Status> setOfStatuses = new HashSet<>();
		setOfStatuses.add(Status.APPROVED);
		PropertyCriteria pptcriteria = PropertyCriteria.builder().propertyIds(setOfConsumerCode)
				.tenantId(genrateArrearRequest.getDemands().get(0).getTenantId()).status(setOfStatuses).build();
		List<Property> properties = searchProperty(pptcriteria, genrateArrearRequest.getRequestInfo(), null);

		if (!CollectionUtils.isEmpty(properties)) {
			checkPropertyArears(genrateArrearRequest.getDemands(), properties.get(0));
			genrateArrearRequest.getDemands().stream().forEach(demand -> {
				
				Map<String, Object> demandAdditionalDetail = null;

				if (demand.getAdditionalDetails() instanceof Map) {
				    Map<?, ?> map = (Map<?, ?>) demand.getAdditionalDetails();

				    if (!map.isEmpty()) {
				        // Now safely cast
				    	demandAdditionalDetail = (Map<String, Object>) map;
				    }
				}

				// If null or empty → initialize
				if (demandAdditionalDetail == null) {
					demandAdditionalDetail = new HashMap<>();
				}
				JsonNode addDetail = mapper.valueToTree(properties.get(0).getAddress().getAdditionalDetails());

				String wardName = null;
				if (addDetail != null && addDetail.has("wardNumber")) {
					wardName = addDetail.get("wardNumber").asText();
				}
				demandAdditionalDetail.put("type", "ARREAR");
				demandAdditionalDetail.put("ward", StringUtils.isNotEmpty(wardName) ? wardName : "N/A");
				demandAdditionalDetail.put("oldPropertyId",
						StringUtils.isNotEmpty(properties.get(0).getOldPropertyId()) ? properties.get(0).getOldPropertyId() : "N/A");
				demandAdditionalDetail.put("ownerOldCustomerId",
						StringUtils.isNotEmpty(
								properties.get(0).getOwners().get(0).getAdditionalDetails().get("ownerOldCustomerId").asText())
										? properties.get(0).getOwners().get(0).getAdditionalDetails().get("ownerOldCustomerId").asText()
										: "N/A");
				demandAdditionalDetail.put("ownerName",
						StringUtils.isNotEmpty(properties.get(0).getOwners().get(0).getPropertyOwnerName())
								? properties.get(0).getOwners().get(0).getPropertyOwnerName()
								: "N/A");
				demandAdditionalDetail.put("contactNumber",
						StringUtils.isNotEmpty(properties.get(0).getOwners().get(0).getMobileNumber())
								? properties.get(0).getOwners().get(0).getMobileNumber()
								: "N/A");
				
				demand.setAdditionalDetails(demandAdditionalDetail); 
				
				List<Demand> savedDemands = demandRepository.saveDemand(genrateArrearRequest.getRequestInfo(),
						createArearDemand(demand, properties.get(0)));
				if (CollectionUtils.isEmpty(savedDemands)) {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");
				}
				GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
						.tenantId(properties.get(0).getTenantId()).businessService(PTConstants.MODULE_PROPERTY)
						.consumerCode(properties.get(0).getPropertyId()).build();
				BillResponse billResponse = billService.generateBill(genrateArrearRequest.getRequestInfo(),
						billCriteria);
				if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {

					CalculateTaxRequest calculateTaxRequest = CalculateTaxRequest.builder()
							.requestInfo(genrateArrearRequest.getRequestInfo())
							.fromDate(new Date(demand.getTaxPeriodFrom())).toDate(new Date(demand.getTaxPeriodTo()))
							.type("ARREAR")
							.financialYear(
									getFinancialYearFromTimestamps(demand.getTaxPeriodFrom(), demand.getTaxPeriodTo()))
							.build();
					JsonNode node = mapper.createObjectNode();
					PtTaxCalculatorTrackerRequest ptTaxCalculatorTrackerRequest = enrichmentService
							.enrichTaxCalculatorTrackerCreateRequest(properties.get(0), calculateTaxRequest,
									demand.getMinimumAmountPayable(), node, billResponse.getBill(), BigDecimal.ZERO,
									demand.getMinimumAmountPayable());
					PtTaxCalculatorTracker ptTaxCalculatorTracker = propertyService
							.saveToPtTaxCalculatorTracker(ptTaxCalculatorTrackerRequest);
				} else {
					throw new CustomException("INVALID_CONSUMERCODE", "Bill not generated");
				}
			});
			message = "Arear Generated Successfully";
		} else {
			message = "Invalid Property Details";
		}
		return message;
	}

	public void checkPropertyArears(List<Demand> demands, Property property) {

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

	public List<Demand> createArearDemand(Demand demand, Property property) {
//		DemandDetail demandDetail = DemandDetail.builder().taxHeadMasterCode(PTConstants.PROPERTY_TAX_HEAD_MASTER_CODE)
//				.taxAmount(taxAmount).collectionAmount(BigDecimal.ZERO).build();
		Demand newDemand = demand;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(365));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);

		ObjectMapper mapper = new ObjectMapper();
//		ObjectNode node = (ObjectNode) demand.getAdditionalDetails();
		ObjectNode node = mapper.valueToTree(demand.getAdditionalDetails());
		if (node == null) {
			node = mapper.createObjectNode(); // create new if null
		}

//		node = demand.getAdditionalDetails();

		JsonNode addDetail = mapper.valueToTree(property.getAddress().getAdditionalDetails());

		String wardName = null;
		if (addDetail != null && addDetail.has("wardNumber")) {
			wardName = addDetail.get("wardNumber").asText();
		}
		node.put("ward", StringUtils.isNotEmpty(wardName) ? wardName : "N/A");
		node.put("oldPropertyId",
				StringUtils.isNotEmpty(property.getOldPropertyId()) ? property.getOldPropertyId() : "N/A");
		node.put("ownerOldCustomerId",
				StringUtils.isNotEmpty(
						property.getOwners().get(0).getAdditionalDetails().get("ownerOldCustomerId").asText())
								? property.getOwners().get(0).getAdditionalDetails().get("ownerOldCustomerId").asText()
								: "N/A");
		node.put("ownerName",
				StringUtils.isNotEmpty(property.getOwners().get(0).getPropertyOwnerName())
						? property.getOwners().get(0).getPropertyOwnerName()
						: "N/A");
		node.put("contactNumber",
				StringUtils.isNotEmpty(property.getOwners().get(0).getMobileNumber())
						? property.getOwners().get(0).getMobileNumber()
						: "N/A");
		node.put("billtype", "ARREAR");
		newDemand.setAdditionalDetails(node);
		newDemand.setPayer(User.builder().uuid(property.getOwners().get(0).getUuid()).build());
		newDemand.setBusinessService(PTConstants.MODULE_PROPERTY);
		newDemand.setConsumerType(PTConstants.MODULE_PROPERTY);
		newDemand.setFixedBillExpiryDate(cal.getTimeInMillis());
		return Collections.singletonList(newDemand);

//		Demand demandOne = Demand.builder()
//				.payer(User.builder().uuid(property.getOwners().get(0).getUuid()).build())
//				.tenantId(property.getTenantId())
//				.taxPeriodFrom(calculateTaxRequest.getFromDate().getTime())
//				.taxPeriodTo(calculateTaxRequest.getToDate().getTime()).fixedBillExpiryDate(cal.getTimeInMillis())
//				.build();

//		List<Demand> demands = Arrays.asList(demand);
	}

	public void saveToPtBillFailure(PropertyBillFailure propertyBillFailure) {
		producer.push(config.getSaveBillFailureTopic(), propertyBillFailure);
	}

	public void removePtBillFailure(PropertyBillFailure propertyBillFailure) {
		producer.push(config.getRemoveBillFailureTopic(), propertyBillFailure);
	}

	public ResponseEntity<?> checkAndCreateUser(CreateObPassUserRequest createUserRequest) {

		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();

		return userService.createNewObPassUser(createUserRequest, requestInfo);

	}

}
