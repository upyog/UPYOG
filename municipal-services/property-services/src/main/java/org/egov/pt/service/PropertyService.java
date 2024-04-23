package org.egov.pt.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.collection.BillDetail;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.user.UserDetailResponse;
import org.egov.pt.models.user.UserSearchRequest;
import org.egov.pt.models.workflow.State;
import org.egov.pt.producer.PropertyProducer;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.EncryptionDecryptionUtil;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.util.UnmaskingUtil;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

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
	private FuzzySearchService fuzzySearchService;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;

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

		/* Fix this.
		 * For FUZZY-search, This code to be un-commented when privacy is enabled
		 
		//Push PLAIN data to fuzzy search index
		producer.push(config.getSavePropertyFuzzyTopic(), request);
		*
		*/
		//Push data after encryption
		producer.pushAfterEncrytpion(config.getSavePropertyTopic(), request);
		request.getProperty().setWorkflow(null);

		/* decrypt here */
		return encryptionDecryptionUtil.decryptObject(request.getProperty(), PTConstants.PROPERTY_MODEL, Property.class, request.getRequestInfo());
		//return request.getProperty();
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
	public Property updateProperty(PropertyRequest request) {
		
		Property propertyFromSearch = unmaskingUtil.getPropertyUnmasked(request);
		propertyValidator.validateCommonUpdateInformation(request, propertyFromSearch);

		boolean isRequestForOwnerMutation = CreationReason.MUTATION.equals(request.getProperty().getCreationReason());
		
		boolean isNumberDifferent = checkIsRequestForMobileNumberUpdate(request, propertyFromSearch);
		
		boolean isRequestForStatusChange=CreationReason.STATUS.equals(request.getProperty().getCreationReason());

		if (isRequestForOwnerMutation)
			processOwnerMutation(request, propertyFromSearch);
		else if(isNumberDifferent)
			processMobileNumberUpdate(request, propertyFromSearch);
			
		else
		{
			if(isRequestForStatusChange)
			{
				 BillResponse billResponse = billingService.fetchBill(request.getProperty(), request.getRequestInfo());
				 
				 BigDecimal dueAmount= new BigDecimal("0");
				 if (billResponse != null && billResponse.getBill()!= null && billResponse.getBill().size()>=1) 
				 {
			     dueAmount = billResponse.getBill().get(0).getTotalAmount();
				 }
				 
			     log.info("No. of Active Bills==="+ ((billResponse != null && billResponse.getBill()!= null && billResponse.getBill().size()>=1)?billResponse.getBill().size(): "0"));
			     log.info("Amount Due is "+ dueAmount);
			     if(dueAmount.compareTo(BigDecimal.ZERO)>0)
			    	 throw new CustomException("EG_PT_ERROR_ACTIVE_BILL_PRESENT",
								"Clear Pending dues before De-Enumerating the property");
							
				else
			    	 processPropertyUpdate(request, propertyFromSearch); 
				
			}
			else
			{
			processPropertyUpdate(request, propertyFromSearch);
			}
		}

		request.getProperty().setWorkflow(null);

		//Push PLAIN data to fuzzy search index
		PropertyRequest fuzzyPropertyRequest = new PropertyRequest(request.getRequestInfo(),request.getProperty());
		fuzzyPropertyRequest.setProperty(encryptionDecryptionUtil.decryptObject(request.getProperty(), PTConstants.PROPERTY_DECRYPT_MODEL,
				Property.class, request.getRequestInfo()));

		/* Fix this.
		 * For FUZZY-search, This code to be un-commented when privacy is enabled
		 
		//Push PLAIN data to fuzzy search index
		producer.push(config.getSavePropertyFuzzyTopic(), fuzzyPropertyRequest);
		*
		*/

		/* decrypt here */
		return encryptionDecryptionUtil.decryptObject(request.getProperty(), PTConstants.PROPERTY_MODEL, Property.class, request.getRequestInfo());
	}
	
	/*
		Method to check if the update request is for updating owner mobile numbers
	*/
	
	private boolean checkIsRequestForMobileNumberUpdate(PropertyRequest request, Property propertyFromSearch) {
		Map <String, String> uuidToMobileNumber = new HashMap <String, String>();
		List <OwnerInfo> owners = propertyFromSearch.getOwners();
		
		for(OwnerInfo owner : owners) {
			uuidToMobileNumber.put(owner.getUuid(), owner.getMobileNumber());
		}
		
		List <OwnerInfo> ownersFromRequest = request.getProperty().getOwners();
		
		Boolean isNumberDifferent = false;
if(!request.getProperty().getCreationReason().equals(CreationReason.MUTATION))
{
		for(OwnerInfo owner : ownersFromRequest) {
			if(uuidToMobileNumber.containsKey(owner.getUuid()) && !uuidToMobileNumber.get(owner.getUuid()).equals(owner.getMobileNumber())) {
				isNumberDifferent = true;
				break;
			}
		}
}
		
		return isNumberDifferent;
	}
	
	/*
		Method to process owner mobile number update
	*/
	
	private void processMobileNumberUpdate(PropertyRequest request, Property propertyFromSearch) {
		
				if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
					userService.createUser(request);
				} else {			
					updateOwnerMobileNumbers(request,propertyFromSearch);
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
	private void processPropertyUpdate(PropertyRequest request, Property propertyFromSearch) {

		propertyValidator.validateRequestForUpdate(request, propertyFromSearch);
		if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
			userService.createUser(request);
		} else if (request.getProperty().getSource().toString().equals("WS")
				&& CreationReason.UPDATE.equals(request.getProperty().getCreationReason())) {
			userService.updateUser(request);
		} else {
			request.getProperty().setOwners(util.getCopyOfOwners(propertyFromSearch.getOwners()));
		}


		enrichmentService.enrichAssignes(request.getProperty());
		enrichmentService.enrichUpdateRequest(request, propertyFromSearch);

		PropertyRequest OldPropertyRequest = PropertyRequest.builder()
				.requestInfo(request.getRequestInfo())
				.property(propertyFromSearch)
				.build();

		util.mergeAdditionalDetails(request, propertyFromSearch);

		if(config.getIsWorkflowEnabled()) {

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
			}else {
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
	
	/*
		Method to update owners mobile number
	*/

	private void updateOwnerMobileNumbers(PropertyRequest request, Property propertyFromSearch) {
		
		
		Map <String, String> uuidToMobileNumber = new HashMap <String, String>();
		List <OwnerInfo> owners = propertyFromSearch.getOwners();
		
		for(OwnerInfo owner : owners) {
			uuidToMobileNumber.put(owner.getUuid(), owner.getMobileNumber());
		}
		
		userService.updateUserMobileNumber(request, uuidToMobileNumber);
		notifService.sendNotificationForMobileNumberUpdate(request, propertyFromSearch,uuidToMobileNumber);		
	}

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
	//	calculatorService.calculateMutationFee(request.getRequestInfo(), request.getProperty());

		// TODO FIX ME block property changes FIXME
		util.mergeAdditionalDetails(request, propertyFromSearch);
		PropertyRequest oldPropertyRequest = PropertyRequest.builder()
				.requestInfo(request.getRequestInfo())
				.property(propertyFromSearch)
				.build();

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
		Map<String, Object> additionalDetails = mapper.convertValue(propertyFromSearch.getAdditionalDetails(), Map.class);
		if(null == additionalDetails)
			return;

		String propertyUuId = (String) additionalDetails.get(PTConstants.PREVIOUS_PROPERTY_PREVIOUD_UUID);
		if(StringUtils.isEmpty(propertyUuId))
			return;

		PropertyCriteria criteria = PropertyCriteria.builder()
				.uuids(Sets.newHashSet(propertyUuId))
				.isSearchInternal(true)
				.tenantId(propertyFromSearch.getTenantId()).build();
		Property previousPropertyToBeReInstated = searchProperty(criteria, request.getRequestInfo()).get(0);
		previousPropertyToBeReInstated.setAuditDetails(util.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid().toString(), true));
		previousPropertyToBeReInstated.setStatus(Status.ACTIVE);
		request.setProperty(previousPropertyToBeReInstated);

		producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
	}
	
	
	public List<Property> enrichProperty(List<Property> properties, RequestInfo requestInfo)
	{
        log.info("In Fetch Bill for Defaulter notice +++++++++++++++++++");

		BillResponse billResponse=new BillResponse();
		List<Property> defaulterProperties=new ArrayList<>();
		for(Property property:properties)
		{
			try{
				billResponse= billingService.fetchBill(property,requestInfo);
			}
			catch(Exception e)
			{
				log.info("Error occured while fetch bill for Property " + property.getPropertyId());
				properties.remove(property);
				enrichProperty(properties,requestInfo);
				defaulterProperties.clear();
			}
			if(billResponse.getBill().size()>=1)
			{
				if(billResponse.getBill().get(0).getBillDetails().size()==1)
				{
					Long fromDate=billResponse.getBill().get(0).getBillDetails().get(0).getFromPeriod();
					Long toDate=billResponse.getBill().get(0).getBillDetails().get(0).getToPeriod();
					int fromYear=new Date(fromDate).getYear()+1900;
					int toYear=new Date(toDate).getYear()+1900;
					if(!(fromYear==(new Date().getYear()) || toYear==(new Date().getYear())))
					{
						property.setDueAmount(billResponse.getBill().get(0).getTotalAmount().toString());
						String assessmentYear=fromYear+"-"+toYear+"(Rs."+billResponse.getBill().get(0).getTotalAmount().toString()+")";
						property.setDueAmountYear(assessmentYear);
						defaulterProperties.add(property);
					}
				}
				else
				{
					property.setDueAmount(billResponse.getBill().get(0).getTotalAmount().toString());
					String assessmentYear=null;
					for(BillDetail bd:billResponse.getBill().get(0).getBillDetails())
					{
						Long fromDate=bd.getFromPeriod();
						Long toDate=bd.getToPeriod();
						int fromYear=new Date(fromDate).getYear()+1900;
						int toYear=new Date(toDate).getYear()+1900;
						assessmentYear=assessmentYear==null? fromYear+"-"+toYear+"(Rs."+bd.getAmount()+")":
							assessmentYear.concat(",").concat(fromYear+"-"+toYear+"(Rs."+bd.getAmount()+")");
					}
					property.setDueAmountYear(assessmentYear);

					defaulterProperties.add(property);
				}

			}
	        log.info("Property Id "+property.getPropertyId() + " added in defaulter notice with Due " +property.getDueAmount() + " for year " +property.getDueAmountYear());

		}
		return defaulterProperties;
		
	}

	/**
	 * Search property with given PropertyCriteria
	 *
	 * @param criteria PropertyCriteria containing fields on which search is based
	 * @return list of properties satisfying the containing fields in criteria
	 */
	public List<Property> searchProperty(PropertyCriteria criteria, RequestInfo requestInfo) {

		List<Property> properties;
		/* encrypt here */
		if(!criteria.getIsRequestForOldDataEncryption())
			criteria = encryptionDecryptionUtil.encryptObject(criteria, PTConstants.PROPERTY_MODEL, PropertyCriteria.class);

		/*
		 * throw error if audit request is with no proeprty id or multiple propertyids
		 */
		if (criteria.isAudit() && (CollectionUtils.isEmpty(criteria.getPropertyIds())
				|| (!CollectionUtils.isEmpty(criteria.getPropertyIds()) && criteria.getPropertyIds().size() > 1))) {

			throw new CustomException("EG_PT_PROPERTY_AUDIT_ERROR", "Audit can only be provided for a single propertyId");
		}

		if (!criteria.getIsRequestForDuplicatePropertyValidation() && (criteria.getDoorNo() != null || criteria.getOldPropertyId() != null)) {
			properties = fuzzySearchService.getProperties(requestInfo, criteria);
		} else {
			if (criteria.getMobileNumber() != null || criteria.getName() != null || criteria.getOwnerIds() != null) {

				log.info("In Property Search");
				/* converts owner information to associated property ids */
				Boolean shouldReturnEmptyList = repository.enrichCriteriaFromUser(criteria, requestInfo);

				if (shouldReturnEmptyList)
					return Collections.emptyList();

				properties = repository.getPropertiesWithOwnerInfo(criteria, requestInfo, false);
				log.info("In Property Search before filtering");

				filterPropertiesForUser(properties, criteria.getOwnerIds());
			} else {
				properties = repository.getPropertiesWithOwnerInfo(criteria, requestInfo, false);
			}

			properties.forEach(property -> {
				enrichmentService.enrichBoundary(property, requestInfo);
			});
		}

		/* Decrypt here */
		 if(criteria.getIsSearchInternal())
			return encryptionDecryptionUtil.decryptObject(properties, PTConstants.PROPERTY_DECRYPT_MODEL, Property.class, requestInfo);
		else if(!criteria.getIsRequestForOldDataEncryption())
			return encryptionDecryptionUtil.decryptObject(properties, PTConstants.PROPERTY_MODEL, Property.class, requestInfo);

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
		for(Property property:properties)
			enrichmentService.enrichBoundary(property,requestInfo);
		return properties;
	}


	List<Property> getPropertiesPlainSearch(PropertyCriteria criteria, RequestInfo requestInfo) {
		
		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			criteria.setLimit(config.getMaxSearchLimit());
		if(criteria.getLimit()==null)
			criteria.setLimit(config.getDefaultLimit());
		if(criteria.getOffset()==null)
			criteria.setOffset(config.getDefaultOffset());
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		if (criteria.getUuids() != null || criteria.getPropertyIds() != null) {
			if (criteria.getUuids() != null)
				propertyCriteria.setUuids(criteria.getUuids());
			if (criteria.getPropertyIds() != null)
				propertyCriteria.setPropertyIds(criteria.getPropertyIds());

		} else if(criteria.getIsRequestForOldDataEncryption()){
			propertyCriteria.setTenantIds(criteria.getTenantIds());
		}
		else {
			List<String> uuids = repository.fetchIds(criteria, true);
			if (uuids.isEmpty())
				return Collections.emptyList();
			propertyCriteria.setUuids(new HashSet<>(uuids));
		}
		propertyCriteria.setLimit(criteria.getLimit());
		List<Property> properties = repository.getPropertiesForBulkSearch(propertyCriteria, true);
		if(properties.isEmpty())
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
		
		Map <String, String> uuidToAlternateMobileNumber = new HashMap <String, String>();
		List <OwnerInfo> owners = propertyFromSearch.getOwners();
		
		for(OwnerInfo owner : owners) {
			
			if(owner.getAlternatemobilenumber()!=null) {
			   uuidToAlternateMobileNumber.put(owner.getUuid(), owner.getAlternatemobilenumber());
			}
			else {
				uuidToAlternateMobileNumber.put(owner.getUuid(), " ");
			}
		}
		
		notifService.sendNotificationForAlternateNumberUpdate(request, propertyFromSearch,uuidToAlternateMobileNumber);	
		
		//enrichmentService.enrichUpdateRequest(request, propertyFromSearch);
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

}
