package org.egov.pt.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.AmalgamatedProperty;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyBifurcation;
import org.egov.pt.models.PropertyCriteria;
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
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyService {
	
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
	
	@Autowired
	private PropertyUtil propertyUtil;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request PropertyRequest containing list of properties to be created
	 * @return List of properties successfully created
	 */
	public Property createProperty(PropertyRequest request) {

		propertyValidator.validateCreateRequest(request);
		enrichmentService.enrichCreateRequest(request);
		//Validate For BiFurcation
		List<PropertyBifurcation> bifurList = null;
		if(request.getProperty().getCreationReason().equals(CreationReason.BIFURCATION)) {
			Integer  maxBifurcation  = request.getProperty().getMaxBifurcation();
			if(null==maxBifurcation) {
				throw new CustomException("INVALID_BIFURCATION_NUMBER","Invalid Maximum Bifurcation number");
				
			}
			if(maxBifurcation<2) {
				throw new CustomException("INVALID_BIFURCATION_MIN_NUMBER","minimum Bifurcation number should be 2");
			}
			propertyValidator.validateCreateRequestForBiFurcation(request);
			try {
				repository.savebifurcation(request);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("Exception for save bifurcation data"+e);
			}
			//producer.pushAfterEncrytpion(config.getSaveBifurcationTopic(), request);
			bifurList= repository.getBifurcationProperties(request.getProperty().getParentPropertyId());
			if(null!=bifurList && bifurList.size()>0) {
				
				
				bifurList  = bifurList.stream().sorted((x,y)->x.getCreatedTime().compareTo(y.getCreatedTime())).collect(Collectors.toList());
				Integer dbMaxBifur = bifurList.get(0).getMaxBifurcation();
				request.getProperty().setBifurcationCount(bifurList.size());
				if(dbMaxBifur== bifurList.size()) {
					
					for(PropertyBifurcation b: bifurList)
					{
						request.setProperty(b.getPropertyRequest().getProperty());
						userService.createUser(request);
						if(config.getIsWorkflowEnabled())
						{
							wfService.updateWorkflow(request, request.getProperty().getCreationReason());
						}
						else {
							request.getProperty().setStatus(Status.ACTIVE);
						}
						producer.pushAfterEncrytpion(config.getSavePropertyTopic(), request);
					}
					//Deactivating Parent
					request.getProperty().setBifurcationCount(bifurList.size());
					producer.pushAfterEncrytpion(config.getUpdatePropertyForDeactivaingForBifurcationTopic(), request);
					//push sms to parent Property Notifying Deactivation of the property as Bifiurcation is initiated
					
				} 
			}else {
				request.getProperty().setBifurcationCount(bifurList.size());
			}
				
			
				
		}else {
			
			if(request.getProperty().getCreationReason().equals(CreationReason.AMALGAMATION)) {
				processPropertyCreateForAmalgamation(request);
			}
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
			
			if(request.getProperty().getCreationReason().equals(CreationReason.AMALGAMATION)) {
				producer.pushAfterEncrytpion(config.getUpdatePropertyForDeactivaingForAmalgamationTopic(), request);
			}
			request.getProperty().setWorkflow(request.getProperty().getWorkflow());

			/* decrypt here */
			
		}
		
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
		boolean isRequestForBifurcation = CreationReason.BIFURCATION.equals(request.getProperty().getCreationReason());
		boolean isRequestForAmalgamation = CreationReason.AMALGAMATION.equals(request.getProperty().getCreationReason());
		
		boolean isNumberDifferent = checkIsRequestForMobileNumberUpdate(request, propertyFromSearch);

		if (isRequestForOwnerMutation)
			processOwnerMutation(request, propertyFromSearch);
		else if(isRequestForBifurcation)
			processPropertyUpdateBifurcation(request, propertyFromSearch);
		else if(isNumberDifferent)
			processMobileNumberUpdate(request, propertyFromSearch);
		else if(isRequestForAmalgamation)
			processPropertyUpdateAmalgamation(request, propertyFromSearch);
		
		else
			processPropertyUpdate(request, propertyFromSearch);

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
		
		for(OwnerInfo owner : ownersFromRequest) {
			if(uuidToMobileNumber.containsKey(owner.getUuid()) && !uuidToMobileNumber.get(owner.getUuid()).equals(owner.getMobileNumber())) {
				isNumberDifferent = true;
				break;
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

		//check for Property Ids;
		
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
	
	private void processPropertyUpdateAmalgamation(PropertyRequest request, Property propertyFromSearch) {

		propertyValidator.validateRequestForUpdate(request, propertyFromSearch);
		if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
			userService.createUser(request);
		} else if (request.getProperty().getSource().toString().equals("WS")
				&& CreationReason.UPDATE.equals(request.getProperty().getCreationReason())) {
			userService.updateUser(request);
		} else {
			request.getProperty().setOwners(util.getCopyOfOwners(propertyFromSearch.getOwners()));
		}

		//check for Property Ids;
		
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

				terminateWorkflowAndReInstatePreviousRecordForAmalgamation(request, propertyFromSearch);
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
	
	
	private void processPropertyUpdateBifurcation(PropertyRequest request, Property propertyFromSearch) {

		propertyValidator.validateRequestForUpdate(request, propertyFromSearch);
		if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
			userService.createUser(request);
		} else if (request.getProperty().getSource().toString().equals("WS")
				&& CreationReason.UPDATE.equals(request.getProperty().getCreationReason())) {
			userService.updateUser(request);
		} else {
			request.getProperty().setOwners(util.getCopyOfOwners(propertyFromSearch.getOwners()));
		}

		//check for Property Ids;
		
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

				terminateWorkflowAndReInstatePreviousRecordForBifurcation(request, propertyFromSearch);
			}else {
				
				if(state.getApplicationStatus().equalsIgnoreCase(Status.INWORKFLOW.toString())) {
					producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
					
				}else {
					List<PropertyBifurcation> bifurList= repository.getBifurcationProperties(request.getProperty().getParentPropertyId());
					Integer Count = bifurList.stream()
							.filter(x->!x.getChildpropertyuuid().equalsIgnoreCase(request.getProperty().getId()))
							.filter(x ->!x.getStatus()).collect(Collectors.toList()).size();
					
					if(Count>0 ) {
						request.getProperty().setStatus(Status.INACTIVE);
						producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
						//Update the status to true in bifurcation table for this property uuid
						PropertyBifurcation b = bifurList.stream()
												.filter(x->x.getChildpropertyuuid().equalsIgnoreCase(request.getProperty().getId()))
												.collect(Collectors.toList()).get(0);
						b.setStatus(true);

						request.getProperty().setPropertyBifurcations(Arrays.asList(b));
						producer.push(config.getUpdateChildStatusForBifurcation(), request);
					}else {
						producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
						producer.push(config.getUpdatePropertyStatusForBifurcationSuccess(),request );
						for(PropertyBifurcation b:bifurList) {
							b.setStatus(true);
						}
						request.getProperty().setPropertyBifurcations(bifurList);
						producer.push(config.getUpdateChildStatusForBifurcation(), request);
						
					}
				}
				
				
				
			}

		} else {

			/*
			 * If no workflow then update property directly with mutation information
			 */
			producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);
		}
	}
	
	
	private void processPropertyCreateForAmalgamation(PropertyRequest request) {
		if (null == request.getProperty().getAmalgamatedProperty()
				|| request.getProperty().getAmalgamatedProperty().isEmpty())
			throw new CustomException("INVALID_AMALGAMATION_PROPERTY", "Invalid Property for Amalgamtion");

		Set<String> validPropertyListToBeAmalgamatedIds = new HashSet();
		PropertyRequest amalPropertiesToBeCheckedRequest = null;
		Property propertyForAmalgamation = null;
		Property propertyFromSearchAmalgamation = null;
		for (AmalgamatedProperty a : request.getProperty().getAmalgamatedProperty()) {
			amalPropertiesToBeCheckedRequest = new PropertyRequest();
			propertyForAmalgamation = new Property();
			propertyFromSearchAmalgamation = new Property();

			propertyForAmalgamation.setPropertyId(a.getPropertyId());
			propertyForAmalgamation.setTenantId(a.getTenantId());
			;

			amalPropertiesToBeCheckedRequest.setProperty(propertyForAmalgamation);
			amalPropertiesToBeCheckedRequest.setRequestInfo(request.getRequestInfo());

			propertyFromSearchAmalgamation = unmaskingUtil
					.getPropertyUnmaskedForAmalgamation(amalPropertiesToBeCheckedRequest);
			amalPropertiesToBeCheckedRequest.setProperty(propertyFromSearchAmalgamation);
			if (propertyFromSearchAmalgamation.getStatus().equals(Status.ACTIVE)) {
				Boolean isBillUnpaid = propertyUtil.isBillUnpaid(propertyFromSearchAmalgamation.getPropertyId(),
						propertyFromSearchAmalgamation.getTenantId(), request.getRequestInfo());
				if (isBillUnpaid)
					throw new CustomException("EG_PT_AMALGAMATION_UNPAID_CHILD_ERROR",
							"Child Property has to be completely paid for before initiating the Amalgamation process");
			} else {
				throw new CustomException("EG_PT_AMALGAMATION_INACTIVE_CHILD_ERROR",
						"Child Property has to be in ACTIVE state");
			}
			a.setProperty(propertyFromSearchAmalgamation);
			validPropertyListToBeAmalgamatedIds.add(propertyFromSearchAmalgamation.getId());
		}
		
		
		@SuppressWarnings("unchecked")
		Map<String, Object> additionalDetails = mapper.convertValue(request.getProperty().getAdditionalDetails(), Map.class);
		additionalDetails.put(PTConstants.CREATED_FROM_PROPERTY, request.getProperty().getAmalgamatedProperty().stream().map(x->x.getPropertyId()).collect(Collectors.toList()));
		additionalDetails.put(PTConstants.AMALGAMATED_CREATED_IDS_PROPERTY, validPropertyListToBeAmalgamatedIds);
		additionalDetails.put(PTConstants.AMALGAMATED_PROPERTY, request.getProperty().getAmalgamatedProperty());
		JsonNode node=mapper.convertValue(additionalDetails, JsonNode.class);
		request.getProperty().setAdditionalDetails(node);
	}
	
	
	private void processPropertyUpdateForAmalgamation(PropertyRequest request, Property propertyFromSearch) {
		
		if(null==request.getProperty().getAmalgamatedProperty() ||request.getProperty().getAmalgamatedProperty().isEmpty() )
			throw new CustomException("INVALID_AMALGAMATION_PROPERTY","Invalid Property for Amalgamtion");
		
		Set<Property>validPropertyListToBeAmalgamated = new HashSet();
		PropertyRequest amalPropertiesToBeCheckedRequest = null;
		Property propertyForAmalgamation = null;
		Property propertyFromSearchAmalgamation= null;
		for(AmalgamatedProperty a:request.getProperty().getAmalgamatedProperty()) {
			amalPropertiesToBeCheckedRequest = new PropertyRequest();
			propertyForAmalgamation = new Property();
			propertyFromSearchAmalgamation = new Property();
			
			propertyForAmalgamation.setPropertyId(a.getPropertyId());
			propertyForAmalgamation.setTenantId(a.getTenantId());;
			
			amalPropertiesToBeCheckedRequest.setProperty(propertyForAmalgamation);
			amalPropertiesToBeCheckedRequest.setRequestInfo(request.getRequestInfo());
			
			propertyFromSearchAmalgamation = unmaskingUtil.getPropertyUnmaskedForAmalgamation(amalPropertiesToBeCheckedRequest);
			amalPropertiesToBeCheckedRequest.setProperty(propertyFromSearchAmalgamation);
			if (propertyFromSearchAmalgamation.getStatus().equals(Status.ACTIVE)) {
			Boolean isBillUnpaid = propertyUtil.isBillUnpaid(propertyFromSearchAmalgamation.getPropertyId(), propertyFromSearchAmalgamation.getTenantId(), request.getRequestInfo());
			if (isBillUnpaid)
				throw new CustomException("EG_PT_AMALGAMATION_UNPAID_CHILD_ERROR", "Child Property has to be completely paid for before initiating the Amalgamation process");
			}else {
				throw new CustomException("EG_PT_AMALGAMATION_INACTIVE_CHILD_ERROR", "Child Property has to be in ACTIVE state");
			}
			a.setProperty(propertyFromSearchAmalgamation);
			validPropertyListToBeAmalgamated.add(propertyFromSearchAmalgamation);
		}
		propertyValidator.validateRequestForUpdate(request, propertyFromSearch);
		
		if (propertyFromSearch.getStatus().equals(Status.ACTIVE)) {
		Boolean isBillUnpaid = propertyUtil.isBillUnpaid(propertyFromSearch.getPropertyId(), propertyFromSearch.getTenantId(), request.getRequestInfo());
		if (isBillUnpaid)
			throw new CustomException("EG_PT_AMALGAMATION_UNPAID_PARENT_ERROR", "Parent Property has to be completely paid for before initiating the Amalgamation process");
		}else {
			throw new CustomException("EG_PT_AMALGAMATION_INACTIVE_PARENT_ERROR", "Parent Property has to be in ACTIVE state");
		}
		if (CreationReason.CREATE.equals(request.getProperty().getCreationReason())) {
			userService.createUser(request);
		} else if (request.getProperty().getSource().toString().equals("WS")
				&& CreationReason.UPDATE.equals(request.getProperty().getCreationReason())) {
			userService.updateUser(request);
		} else {
			request.getProperty().setOwners(util.getCopyOfOwners(propertyFromSearch.getOwners()));
		}

		//check for Property Ids;
		
		enrichmentService.enrichAssignes(request.getProperty());
		enrichmentService.enrichUpdateRequestForAmalgamation(request, propertyFromSearch);

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
				OldPropertyRequest.getProperty().setStatus(Status.INACTIVE);
				producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), OldPropertyRequest);
				util.saveOldUuidToRequest(request, propertyFromSearch.getId());
				producer.pushAfterEncrytpion(config.getSavePropertyTopic(), request);
				producer.pushAfterEncrytpion(config.getUpdatePropertyForDeactivaingForAmalgamationTopic(), request);

			} else if (state.getIsTerminateState()
					&& !state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString())) {

				terminateWorkflowAndReInstatePreviousRecordForAmalgamation(request, propertyFromSearch);
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
		calculatorService.calculateMutationFee(request.getRequestInfo(), request.getProperty());

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

	
	private void processOwnerBifurcation(PropertyRequest request, Property propertyFromSearch) {

		propertyValidator.validateBifurcation(request, propertyFromSearch);
		userService.createUserForMutation(request, !propertyFromSearch.getStatus().equals(Status.INWORKFLOW));
		enrichmentService.enrichAssignes(request.getProperty());
		enrichmentService.enrichBiFurcationRequest(request, propertyFromSearch);
		//calculatorService.calculateMutationFee(request.getRequestInfo(), request.getProperty());

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

	private void terminateWorkflowAndReInstatePreviousRecordForBifurcation(PropertyRequest request, Property propertyFromSearch) {

		/* current record being rejected */
		/*
		 * String parentProperty = propertyFromSearch.getParentPropertyId();
		 * 
		 * PropertyCriteria criteria = PropertyCriteria.builder()
		 * .propertyIds(Sets.newHashSet(parentProperty)) .isSearchInternal(true)
		 * .tenantId(propertyFromSearch.getTenantId()).build(); //Parent Property
		 * Property previousPropertyToBeReInstated = searchProperty(criteria,
		 * request.getRequestInfo()).get(0);
		 * 
		 * previousPropertyToBeReInstated.setAuditDetails(util.getAuditDetails(request.
		 * getRequestInfo().getUserInfo().getUuid().toString(), true));
		 * previousPropertyToBeReInstated.setStatus(Status.ACTIVE);
		 */
		
		//For Updating the child property to inactive
		propertyFromSearch.setStatus(Status.INACTIVE);
		PropertyRequest prosearch = PropertyRequest.builder()
				.requestInfo(request.getRequestInfo())
				.property(propertyFromSearch)
				.build();
		producer.pushAfterEncrytpion(config.getBifurcationChildInactive(), prosearch);
		
		
		PropertyRequest proReInstate = PropertyRequest.builder()
				.requestInfo(request.getRequestInfo())
				.property(propertyFromSearch)
				.build();
		//For Updating the parent property to active
		producer.pushAfterEncrytpion(config.getUpdateParentPropertyForBifurcation(), proReInstate);
		
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
	
	private void terminateWorkflowAndReInstatePreviousRecordForAmalgamation(PropertyRequest request, Property propertyFromSearch) {

		/* current record being rejected */
		//producer.pushAfterEncrytpion(config.getUpdatePropertyTopic(), request);

		/* Previous record set to ACTIVE */
		@SuppressWarnings("unchecked")
		Map<String, Object> additionalDetails = mapper.convertValue(propertyFromSearch.getAdditionalDetails(), Map.class);
		if(null == additionalDetails)
			return;
		
		PropertyRequest OldPropertyRequest = PropertyRequest.builder()
				.requestInfo(request.getRequestInfo())
				.property(propertyFromSearch)
				.build();
		@SuppressWarnings("unchecked")
		List<AmalgamatedProperty> amalgamatedProperties = (List<AmalgamatedProperty>) additionalDetails.get("amalgamatedProperty");
		propertyFromSearch.setAmalgamatedProperty(amalgamatedProperties);
		producer.push(config.getUpdateAmalgamatedPropertyInactive(), request);
		producer.push(config.getUpdateAmalgamatedPropertyToActive(), OldPropertyRequest);
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

		if (criteria.getDoorNo() != null || criteria.getName() != null || criteria.getOldPropertyId() != null) {
			properties = fuzzySearchService.getProperties(requestInfo, criteria);
		} else {
			if (criteria.getMobileNumber() != null || criteria.getName() != null || criteria.getOwnerIds() != null) {

				/* converts owner information to associated property ids */
				Boolean shouldReturnEmptyList = repository.enrichCriteriaFromUser(criteria, requestInfo);

				if (shouldReturnEmptyList)
					return Collections.emptyList();

				properties = repository.getPropertiesWithOwnerInfo(criteria, requestInfo, false);
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
