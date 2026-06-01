package org.egov.garbagecollection.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbagecollection.config.GCConfiguration;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.repository.GcDao;
import org.egov.garbagecollection.repository.GcDaoImpl;
import org.egov.garbagecollection.util.EncryptionDecryptionUtil;
import org.egov.garbagecollection.util.GcServicesUtil;
import org.egov.garbagecollection.util.UnmaskingUtil;
import org.egov.garbagecollection.validator.ActionValidator;
import org.egov.garbagecollection.validator.GcValidator;
import org.egov.garbagecollection.validator.MDMSValidator;
import org.egov.garbagecollection.validator.ValidateProperty;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.web.models.Connection.StatusEnum;
import org.egov.garbagecollection.web.models.users.User;
import org.egov.garbagecollection.web.models.workflow.BusinessService;
import org.egov.garbagecollection.web.models.workflow.ProcessInstance;
import org.egov.garbagecollection.workflow.WorkflowIntegrator;
import org.egov.garbagecollection.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.egov.garbagecollection.constants.GCConstants.APPROVE_CONNECTION;
import static org.egov.garbagecollection.constants.GCConstants.WORKFLOW_NO_PAYMENT_CODE;

@Slf4j
@Component
public class GcServiceImpl implements GcService {

	@Autowired
	private GcDao gcDao;

	@Autowired
	private GcValidator gcValidator;

	@Autowired
	private ValidateProperty validateProperty;
	
	
	@Autowired
	private EODBredirect eodbRedirect;

	@Autowired
	private MDMSValidator mDMSValidator;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private GCConfiguration config;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private ActionValidator actionValidator;

	@Autowired
	private GcServicesUtil gcServiceUtil;

	@Autowired
	private CalculationService calculationService;

	@Autowired
	private GcDaoImpl gcDao1;

	@Autowired
	private UserService userService;

	@Autowired
	private GcServicesUtil wsUtil;

	 @Autowired
	 EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	private PaymentUpdateService paymentUpdateService;

	@Autowired
	private UnmaskingUtil unmaskingUtil;
	
	@Autowired
	private GcService gcService;

	/**
	 *
	 * @param garbageConnectionRequest GarbageConnectionRequest contains water
	 *                               connection to be created
	 * @return List of GarbageConnection after create
	 */
	@Override
	public List<GarbageConnection> createGarbageConnection(GarbageConnectionRequest garbageConnectionRequest) {
		Boolean isMigration = false;
		Object additionalDetailsObj = garbageConnectionRequest.getGarbageConnection().getAdditionalDetails();

		if (additionalDetailsObj instanceof Map) {
		    Map<String, Object> additionalDetails = (Map<String, Object>) additionalDetailsObj;

		    if (additionalDetails.containsKey("isMigrated")) {
		        Object migratedValue = additionalDetails.get("isMigrated");

		        if (migratedValue instanceof Boolean) {
		            isMigration = (Boolean) migratedValue;
		        } else if (migratedValue instanceof String) {
		            isMigration = Boolean.parseBoolean((String) migratedValue);
		        }
		    }
		}
		
		
		String applicationType = garbageConnectionRequest.getGarbageConnection().getApplicationType();
		String connectionNo = garbageConnectionRequest.getGarbageConnection().getConnectionNo();

		
		if (isMigration && "NEW_GARBAGE_CONNECTION".equalsIgnoreCase(applicationType) && connectionNo != null && !connectionNo.trim().isEmpty()) {
			    SearchCriteria criteria = new SearchCriteria();
			    criteria.setConnectionNumber(Collections.singleton(connectionNo));
			    criteria.setTenantId(garbageConnectionRequest.getGarbageConnection().getTenantId());

			    List<GarbageConnection> existingConnections = gcService.search(
			        criteria, garbageConnectionRequest.getRequestInfo());

			    if (!existingConnections.isEmpty()) {
			        log.info("Skipping creation: ConnectionNo {} already exists for migrated request.", connectionNo);
			        return existingConnections;
			    }
			}
		
		int reqType = GCConstants.CREATE_APPLICATION;
		Connection.StatusEnum status = garbageConnectionRequest.getGarbageConnection().getStatus();
		log.info("isMigration::::" + isMigration);
		if (garbageConnectionRequest.isDisconnectRequest()
				|| (garbageConnectionRequest.getGarbageConnection().getApplicationType() != null
						&& garbageConnectionRequest.getGarbageConnection().getApplicationType()
								.equalsIgnoreCase(GCConstants.DISCONNECT_GARBAGE_CONNECTION))) {
			reqType = GCConstants.DISCONNECT_CONNECTION;
			validateDisconnectionRequest(garbageConnectionRequest);
		} else if (garbageConnectionRequest.isReconnectRequest()
				|| (garbageConnectionRequest.getGarbageConnection().getApplicationType() != null && garbageConnectionRequest
						.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION))) {
			reqType = GCConstants.RECONNECTION;
			validateReconnectionRequest(garbageConnectionRequest);
		}

		else if (wsUtil.isModifyConnectionRequest(garbageConnectionRequest)) {
			List<GarbageConnection> previousConnectionsList = getAllWaterApplications(garbageConnectionRequest);
			/*
			 * if (previousConnectionsList.size() > 0) { for (GarbageConnection
			 * previousConnectionsListObj : previousConnectionsList) {
			 * waterDaoImpl.updateWaterApplicationStatus(previousConnectionsListObj.getId(),
			 * GCConstants.INACTIVE_STATUS); } }
			 */

			// Validate any process Instance exists with WF
			if (!CollectionUtils.isEmpty(previousConnectionsList)) {
				workflowService.validateInProgressWF(previousConnectionsList, garbageConnectionRequest.getRequestInfo(),
						garbageConnectionRequest.getGarbageConnection().getTenantId());
				gcValidator.validateConnectionStatus(previousConnectionsList, garbageConnectionRequest,
						reqType);
			}
			swapConnHolders(garbageConnectionRequest, previousConnectionsList);

			// Swap masked Plumber info with unmasked plumberInfo from previous applications
//			if (!ObjectUtils.isEmpty(previousConnectionsList.get(0).getPlumberInfo()))
//				unmaskingUtil.getUnmaskedPlumberInfo(garbageConnectionRequest.getGarbageConnection().getPlumberInfo(),
//						previousConnectionsList.get(0).getPlumberInfo());

			// Validate any process Instance exists with WF
			if (!CollectionUtils.isEmpty(previousConnectionsList)) {
				workflowService.validateInProgressWF(previousConnectionsList, garbageConnectionRequest.getRequestInfo(),
						garbageConnectionRequest.getGarbageConnection().getTenantId());
			}
			reqType = GCConstants.MODIFY_CONNECTION;
		}
		gcValidator.validateGarbageConnection(garbageConnectionRequest, reqType);
		Property property = validateProperty.getOrValidateProperty(garbageConnectionRequest);
		enrichAdditionalDetailsFromUnit(garbageConnectionRequest, property);
		validateProperty.validatePropertyFields(property, garbageConnectionRequest.getRequestInfo());
		mDMSValidator.validateMasterForCreateRequest(garbageConnectionRequest);
		enrichmentService.enrichGarbageConnection(garbageConnectionRequest, reqType);
		userService.createUser(garbageConnectionRequest);
		// call work-flow
		if (!isMigration) {

			if (config.getIsExternalWorkFlowEnabled())
				wfIntegrator.callWorkFlow(garbageConnectionRequest, property);
		}

		/* encrypt here */
		// garbageConnectionRequest.setGarbageConnection(encryptConnectionDetails(garbageConnectionRequest.getGarbageConnection()));
		/* encrypt here for connection holder details */
		// garbageConnectionRequest.setGarbageConnection(encryptConnectionHolderDetails(garbageConnectionRequest.getGarbageConnection()));
		if (isMigration) {
		    if (garbageConnectionRequest.getGarbageConnection() != null 
		        && garbageConnectionRequest.getGarbageConnection().getStatus() != null 
		        && !garbageConnectionRequest.getGarbageConnection().getStatus().toString().isEmpty()) {
		        
		    	 garbageConnectionRequest.getGarbageConnection().setStatus(status);		  
		    }
		}
		gcDao.saveGarbageConnection(garbageConnectionRequest);

		/* decrypt here */
		// garbageConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(garbageConnectionRequest.getGarbageConnection(),
		// WNS_ENCRYPTION_MODEL, GarbageConnection.class,
		// garbageConnectionRequest.getRequestInfo()));
		// PlumberInfo masked during Create call of New Application
//		if (reqType == 0)
		// garbageConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(garbageConnectionRequest.getGarbageConnection(),
		// WNS_PLUMBER_ENCRYPTION_MODEL, GarbageConnection.class,
		// garbageConnectionRequest.getRequestInfo()));
		// PlumberInfo unmasked during create call of Disconnect/Modify Applications
//		else
		// garbageConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(garbageConnectionRequest.getGarbageConnection(),
		// "WnSConnectionPlumberDecrypDisabled", GarbageConnection.class,
		// garbageConnectionRequest.getRequestInfo()));

		/*
		 * List<OwnerInfo> connectionHolders =
		 * garbageConnectionRequest.getGarbageConnection().getConnectionHolders(); if
		 * (!CollectionUtils.isEmpty(connectionHolders))
		 * garbageConnectionRequest.getGarbageConnection().setConnectionHolders(
		 * encryptionDecryptionUtil.decryptObject(connectionHolders,
		 * WNS_OWNER_ENCRYPTION_MODEL, OwnerInfo.class,
		 * garbageConnectionRequest.getRequestInfo()));
		 */
		return Arrays.asList(garbageConnectionRequest.getGarbageConnection());
	}

	@SuppressWarnings("unchecked")
	private void enrichAdditionalDetailsFromUnit(GarbageConnectionRequest request, Property property) {
		if (request == null || request.getGarbageConnection() == null || property == null || CollectionUtils.isEmpty(property.getUnits())) {
			return;
		}

		String unitId = request.getGarbageConnection().getUnitId();
		if (StringUtils.isEmpty(unitId)) {
			return;
		}

		Optional<Unit> selectedUnitOptional = property.getUnits().stream()
				.filter(unit -> unitId.equals(unit.getId()))
				.findFirst();

		if (!selectedUnitOptional.isPresent()) {
			return;
		}

		Unit selectedUnit = selectedUnitOptional.get();
		Map<String, Object> connectionAdditionalDetails = new HashMap<>();
		Object existingAdditionalDetails = request.getGarbageConnection().getAdditionalDetails();

		if (existingAdditionalDetails instanceof Map) {
			connectionAdditionalDetails.putAll((Map<String, Object>) existingAdditionalDetails);
		}

		Object derivedDefAmount = connectionAdditionalDetails.get(GCConstants.DEF_AMOUNT);
		if (ObjectUtils.isEmpty(derivedDefAmount)) {
			derivedDefAmount = extractDefAmountFromUnit(selectedUnit);
		}
		connectionAdditionalDetails.put(GCConstants.DEF_AMOUNT, ObjectUtils.isEmpty(derivedDefAmount) ? "100" : derivedDefAmount);

		Object payloadFloorNo = connectionAdditionalDetails.get(GCConstants.FLOOR_NO);
		if (ObjectUtils.isEmpty(payloadFloorNo)) {
			Integer floorNo = selectedUnit.getFloorNo();
			connectionAdditionalDetails.put(GCConstants.FLOOR_NO, floorNo == null ? "" : floorNo);
		}

		request.getGarbageConnection().setAdditionalDetails(connectionAdditionalDetails);
	}

	@SuppressWarnings("unchecked")
	private Object extractDefAmountFromUnit(Unit unit) {
		if (unit == null || !(unit.getAdditionalDetails() instanceof Map)) {
			return null;
		}

		Map<String, Object> unitAdditionalDetails = (Map<String, Object>) unit.getAdditionalDetails();
		if (!ObjectUtils.isEmpty(unitAdditionalDetails.get(GCConstants.DEF_AMOUNT))) {
			return unitAdditionalDetails.get(GCConstants.DEF_AMOUNT);
		}
		if (!ObjectUtils.isEmpty(unitAdditionalDetails.get("amount"))) {
			return unitAdditionalDetails.get("amount");
		}
		return null;
	}

	private void validateDisconnectionRequest(GarbageConnectionRequest waterConnectionRequest) {
		if (!waterConnectionRequest.getGarbageConnection().getStatus().toString().equalsIgnoreCase(GCConstants.ACTIVE)) {
			throw new CustomException("INVALID_REQUEST", "Garbage connection must be active for disconnection request");
		}

		List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);
		swapConnHolders(waterConnectionRequest, previousConnectionsList);

		// Swap masked Plumber info with unmasked plumberInfo from previous applications
//		if (!ObjectUtils.isEmpty(previousConnectionsList.get(0).getPlumberInfo()))
//			unmaskingUtil.getUnmaskedPlumberInfo(waterConnectionRequest.getGarbageConnection().getPlumberInfo(),
//					previousConnectionsList.get(0).getPlumberInfo());

		for (GarbageConnection connection : previousConnectionsList) {
			if (!(connection.getApplicationStatus().equalsIgnoreCase(GCConstants.STATUS_APPROVED)
					|| connection.getApplicationStatus().equalsIgnoreCase(GCConstants.DISCONNECTION_FINAL_STATE)
		            || connection.getApplicationStatus().equalsIgnoreCase(GCConstants.STATUS_REJECTED)
					|| connection.getApplicationStatus().equalsIgnoreCase(GCConstants.MODIFIED_FINAL_STATE))) {
				throw new CustomException("INVALID_REQUEST",
						"No application should be in progress while applying for disconnection");
			}
		}

	}

	private void validateReconnectionRequest(GarbageConnectionRequest waterConnectionRequest) {
		if (waterConnectionRequest.getGarbageConnection().getStatus().toString().equalsIgnoreCase(GCConstants.ACTIVE)) {
			throw new CustomException("INVALID_REQUEST", "Garbage connection must be inactive for reconnection request");
		}

		List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);
		swapConnHolders(waterConnectionRequest, previousConnectionsList);

		// Swap masked Plumber info with unmasked plumberInfo from previous applications
//		if (!ObjectUtils.isEmpty(previousConnectionsList.get(0).getPlumberInfo()))
//			unmaskingUtil.getUnmaskedPlumberInfo(waterConnectionRequest.getGarbageConnection().getPlumberInfo(),
//					previousConnectionsList.get(0).getPlumberInfo());

		for (GarbageConnection connection : previousConnectionsList) {
			if (!(connection.getApplicationStatus().equalsIgnoreCase(GCConstants.STATUS_APPROVED)
					|| connection.getApplicationStatus().equalsIgnoreCase(GCConstants.DISCONNECTION_FINAL_STATE)
					|| connection.getApplicationStatus().equalsIgnoreCase(GCConstants.MODIFIED_FINAL_STATE))) {
				throw new CustomException("INVALID_REQUEST",
						"No application should be in progress while applying for Reconnection");
			}
		}
	}

	/**
	 *
	 * @param criteria    GarbageConnectionSearchCriteria contains search criteria on
	 *                    water connection
	 * @param requestInfo
	 * @return List of matching water connection
	 */
	public List<GarbageConnection> search(SearchCriteria criteria, RequestInfo requestInfo) {
		List<GarbageConnection> garbageConnectionList;
		if(StringUtils.isEmpty(criteria.getMobileNumber()) && StringUtils.isEmpty(criteria.getApplicationNumber()) 
				&& StringUtils.isEmpty(criteria.getConnectionNumber()) && StringUtils.isEmpty(criteria.getPropertyId())){

			criteria.setUserIds(Collections.singleton(requestInfo.getUserInfo().getUuid()));
		}
		garbageConnectionList = getGarbageConnectionsList(criteria, requestInfo);


		for (GarbageConnection gc : garbageConnectionList) {
			if (gc == null || gc.getProcessInstance() == null) {
				continue; // skip safely
			}

			String applicationType = gc.getApplicationType();
			if ("NEW_GARBAGE_CONNECTION".equals(applicationType)) {
				gc.getProcessInstance().setBusinessService("NewGC");
			} else if ("DISCONNECT_GARBAGE_CONNECTION".equals(applicationType)) {
				gc.getProcessInstance().setBusinessService("DisconnectGCConnection");
			}
		}



		log.info("Garbage Connection List Inside Search API call ::" + garbageConnectionList);
		log.info("Search Criteria ::" + criteria);
		if (!StringUtils.isEmpty(criteria.getSearchType())
				&& criteria.getSearchType().equals(GCConstants.SEARCH_TYPE_CONNECTION)) {
			garbageConnectionList = enrichmentService.filterConnections(garbageConnectionList);
			if (criteria.getIsPropertyDetailsRequired()) {
				garbageConnectionList = enrichmentService.enrichPropertyDetails(garbageConnectionList, criteria,
						requestInfo);

			}
		}
		gcValidator.validatePropertyForConnection(garbageConnectionList);
		enrichmentService.enrichConnectionHolderDeatils(garbageConnectionList, criteria, requestInfo);
		return garbageConnectionList;
	}

	/**
	 *
	 * @param criteria    GarbageConnectionSearchCriteria contains search criteria on
	 *                    water connection
	 * @param requestInfo
	 * @return List of matching water connection
	 */
	public List<GarbageConnection> getGarbageConnectionsList(SearchCriteria criteria, RequestInfo requestInfo) {
		return gcDao.getGarbageConnectionList(criteria, requestInfo);
	}

	/**
	 *
	 * @param criteria    GarbageConnectionSearchCriteria contains search criteria on
	 *                    water connection
	 * @param requestInfo
	 * @return Count of List of matching water connection
	 */
	@Override
	public Integer countAllGarbageApplications(SearchCriteria criteria, RequestInfo requestInfo) {
		criteria.setIsCountCall(Boolean.TRUE);
		return getGarbageConnectionsCount(criteria, requestInfo);
	}

	@Override
	public List<GarbageConnection> searchGarbageConnectionPlainSearch(SearchCriteria criteria, RequestInfo requestInfo) {
		List<GarbageConnection> waterConnectionList = getGarbageConnectionPlainSearch(criteria, requestInfo);
		return waterConnectionList;
	}

	List<GarbageConnection> getGarbageConnectionPlainSearch(SearchCriteria criteria, RequestInfo requestInfo) {

		if (criteria.getLimit() == null) {
			criteria.setLimit(config.getDefaultLimit());
		} else if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxLimit()) {
			criteria.setLimit(config.getMaxLimit());
		}

		if (criteria.getOffset() == null)
			criteria.setOffset(config.getDefaultOffset());

		List<String> ids = gcDao.fetchGarbageConnectionIds(criteria);
		if (ids.isEmpty())
			return Collections.emptyList();

		SearchCriteria newCriteria = new SearchCriteria();
		newCriteria.setIds(new HashSet<>(ids));

		List<GarbageConnection> garbageConnectionList = gcDao.getPlainGarbageConnectionSearch(newCriteria);
		return garbageConnectionList;
	}

	/**
	 *
	 * @param criteria    GarbageConnectionSearchCriteria contains search criteria on
	 *                    water connection
	 * @param requestInfo
	 * @return count of matching water connection
	 */
	public Integer getGarbageConnectionsCount(SearchCriteria criteria, RequestInfo requestInfo) {
		return gcDao.getGarbageConnectionsCount(criteria, requestInfo);
	}

	/**
	 *
	 * @param waterConnectionRequest GarbageConnectionRequest contains water
	 *                               connection to be updated
	 * @return List of GarbageConnection after update
	 */
	@Override
	public List<GarbageConnection> updateGarbageConnection(GarbageConnectionRequest waterConnectionRequest) {
		boolean eodbPushed = false;
		if (waterConnectionRequest.isDisconnectRequest() || waterConnectionRequest.getGarbageConnection()
				.getApplicationType().equalsIgnoreCase(GCConstants.DISCONNECT_GARBAGE_CONNECTION)) {
			return updateGarbageConnectionForDisconnectFlow(waterConnectionRequest);
		} else if (waterConnectionRequest.isReconnectRequest() || waterConnectionRequest.getGarbageConnection()
				.getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION)) {
			return updateGarbageConnectionForReconnectFlow(waterConnectionRequest);
		}
		SearchCriteria criteria = new SearchCriteria();
		log.info("con" + wsUtil.isModifyConnectionRequest(waterConnectionRequest));
		log.info("isDisconnection" + waterConnectionRequest.getGarbageConnection().getIsDisconnectionTemporary());


		if (waterConnectionRequest.getGarbageConnection().isIsworkflowdisabled()) {
			log.info("Water Request: " + waterConnectionRequest);
			if (waterConnectionRequest.getGarbageConnection().getConnectionHolders() != null) {
				SearchCriteria criteriaAppNo = new SearchCriteria();
				criteriaAppNo.setApplicationNumber(new HashSet<>(Collections.singletonList(
						waterConnectionRequest.getGarbageConnection().getApplicationNo())));
				List<GarbageConnection> waterConnectionList = search(criteriaAppNo, waterConnectionRequest.getRequestInfo());
				if (!waterConnectionList.isEmpty()) {
					List<OwnerInfo> connectionHoldersFromSearch = waterConnectionList.get(0).getConnectionHolders();
					HashMap<String, String> mobileNumberMapFromSearch = connectionHoldersFromSearch.stream().collect(Collectors.toMap(User::getUuid, OwnerInfo::getMobileNumber, (a, b) -> b, HashMap::new));
					HashMap<String, String> mobileNumberMapFromRequest = waterConnectionRequest.getGarbageConnection().getConnectionHolders().stream().collect(Collectors.toMap(User::getUuid, OwnerInfo::getMobileNumber, (a, b) -> b, HashMap::new));
					boolean areMobileNumbersSame = mobileNumberMapFromRequest.equals(mobileNumberMapFromSearch);
					if (!areMobileNumbersSame) {
						userService.updateUser(waterConnectionRequest, waterConnectionRequest.getGarbageConnection());
					}
				}
			}

			gcDao.updateGarbageConnection(waterConnectionRequest,
					waterConnectionRequest.getGarbageConnection().isIsworkflowdisabled());
			return Arrays.asList(waterConnectionRequest.getGarbageConnection());
		}

		if (wsUtil.isModifyConnectionRequest(waterConnectionRequest)
				&& !waterConnectionRequest.getGarbageConnection().getIsDisconnectionTemporary()) {
			
			waterConnectionRequest = updateConnectionStatusBasedOnAction(waterConnectionRequest);
			  // Received request to update the connection for modifyConnection WF
			return updateGarbageConnectionForModifyFlow(waterConnectionRequest);
		}

		gcValidator.validateGarbageConnection(waterConnectionRequest, GCConstants.UPDATE_APPLICATION);
		mDMSValidator.validateMasterData(waterConnectionRequest, GCConstants.UPDATE_APPLICATION);
		Property property = validateProperty.getOrValidateProperty(waterConnectionRequest);
		validateProperty.validatePropertyFields(property, waterConnectionRequest.getRequestInfo());
		BusinessService businessService = workflowService.getBusinessService(
				waterConnectionRequest.getGarbageConnection().getTenantId(), waterConnectionRequest.getRequestInfo(),
				config.getBusinessServiceValue());
		GarbageConnection searchResult = getConnectionForUpdateRequest(
				waterConnectionRequest.getGarbageConnection().getId(), waterConnectionRequest.getRequestInfo());

//		boolean isPlumberSwapped = unmaskingUtil.getUnmaskedPlumberInfo(
//				waterConnectionRequest.getGarbageConnection().getPlumberInfo(), searchResult.getPlumberInfo());
//		// if (isPlumberSwapped)
		// waterConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(waterConnectionRequest.getGarbageConnection(),
		// "WnSConnectionPlumberDecrypDisabled", GarbageConnection.class,
		// waterConnectionRequest.getRequestInfo()));

		String previousApplicationStatus = workflowService.getApplicationStatus(waterConnectionRequest.getRequestInfo(),
				waterConnectionRequest.getGarbageConnection().getApplicationNo(),
				waterConnectionRequest.getGarbageConnection().getTenantId(), config.getBusinessServiceValue());

		boolean isStateUpdatable = gcServiceUtil.getStatusForUpdate(businessService, previousApplicationStatus);

		enrichmentService.enrichUpdateGarbageConnection(waterConnectionRequest);
		actionValidator.validateUpdateRequest(waterConnectionRequest, businessService, previousApplicationStatus);
		gcValidator.validateUpdate(waterConnectionRequest, searchResult, GCConstants.UPDATE_APPLICATION);
		userService.updateUser(waterConnectionRequest, searchResult);
		// Enriching the property details
		List<GarbageConnection> waterConnectionList = new ArrayList<>();
		waterConnectionList.add(waterConnectionRequest.getGarbageConnection());
		criteria.setTenantId(waterConnectionRequest.getGarbageConnection().getTenantId());
		waterConnectionRequest.setGarbageConnection(enrichmentService
				.enrichPropertyDetails(waterConnectionList, criteria, waterConnectionRequest.getRequestInfo()).get(0));


		// call calculator service to generate the demand for one time fee
		calculationService.calculateFeeAndGenerateDemand(waterConnectionRequest, property);

		// Call workflow
		wfIntegrator.callWorkFlow(waterConnectionRequest, property);

		// check for edit and send edit notification
		gcDao1.pushForEditNotification(waterConnectionRequest, isStateUpdatable);
		// Enrich file store Id After payment
		enrichmentService.enrichFileStoreIds(waterConnectionRequest);
//		userService.createUser(waterConnectionRequest);
		enrichmentService.postStatusEnrichment(waterConnectionRequest);

		/* encrypt here */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionDetails(waterConnectionRequest.getGarbageConnection()));
		/* encrypt here for connection holder details */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionHolderDetails(waterConnectionRequest.getGarbageConnection()));

		gcDao.updateGarbageConnection(waterConnectionRequest, isStateUpdatable);

		enrichmentService.postForMeterReading(waterConnectionRequest, GCConstants.UPDATE_APPLICATION);
		if (!StringUtils.isEmpty(waterConnectionRequest.getGarbageConnection().getTenantId()))
			criteria.setTenantId(waterConnectionRequest.getGarbageConnection().getTenantId());
		enrichmentService.enrichProcessInstance(Arrays.asList(waterConnectionRequest.getGarbageConnection()), criteria,
				waterConnectionRequest.getRequestInfo());

		/* decrypt here */
		waterConnectionRequest.setGarbageConnection(decryptConnectionDetails(waterConnectionRequest.getGarbageConnection(),
				waterConnectionRequest.getRequestInfo()));

		

		try {
		    String channel = waterConnectionRequest.getGarbageConnection().getChannel();
		    String thirdPartyCode = null;
		    Object additionalDetailsObj = waterConnectionRequest.getGarbageConnection().getAdditionalDetails();
		    if (additionalDetailsObj instanceof Map) {
		        Map<String, Object> additionalDetails = (Map<String, Object>) additionalDetailsObj;
		        Object isavail = additionalDetails.get("thirdPartyCode");
		        thirdPartyCode = isavail != null ? isavail.toString() : null;
		    }

		    if ("EODB".equalsIgnoreCase(channel) || "EODB".equalsIgnoreCase(thirdPartyCode)) {
		        eodbPushed = eodbRedirect.runEodbFlow(waterConnectionRequest);
		    }
		} catch (Exception e) {
		    log.error("EODB push failed", e);
		}
		log.info("EODB push status: {}", eodbPushed ? "SUCCESS" : "SKIPPED OR FAILED");

		return Arrays.asList(waterConnectionRequest.getGarbageConnection());
	}

	public GarbageConnectionRequest updateConnectionStatusBasedOnAction(GarbageConnectionRequest waterConnectionRequest) {
		
		if(waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction() != null 
				  && waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction().equals(GCConstants.SUBMIT_APPLICATION_CONST)){
			List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);
			if (previousConnectionsList.size() > 0) { 
			  for (GarbageConnection previousConnectionsListObj : previousConnectionsList) {
				  if(previousConnectionsListObj.getStatus().equals(StatusEnum.ACTIVE)){
					  gcDao1.updateGarbageApplicationStatus(previousConnectionsListObj.getId(),
							  GCConstants.INACTIVE_STATUS); 
				  	}
			  	} 
			  }
		  waterConnectionRequest.getGarbageConnection().setStatus(StatusEnum.ACTIVE);
		}
		  
		  if(waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction() != null 
				  && waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction().equals(GCConstants.ACTION_REJECT)){
			  List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);
			  if (previousConnectionsList.size() > 0) { 
				  Collections.sort(previousConnectionsList, Comparator.comparing((GarbageConnection wc) -> wc.getAuditDetails().getLastModifiedTime()).reversed());
				  for (GarbageConnection previousConnectionsListObj : previousConnectionsList) {
					   if(previousConnectionsListObj.getApplicationStatus().equals(GCConstants.STATUS_APPROVED) 
							   || previousConnectionsListObj.getApplicationStatus().equals(GCConstants.APPROVED)){
						   gcDao1.updateGarbageApplicationStatus(previousConnectionsListObj.getId(),
									  GCConstants.ACTIVE_STATUS); 
						   waterConnectionRequest.getGarbageConnection().setStatus(StatusEnum.INACTIVE);
						   break;
					   }
				  }
			}
			  
		  }
		  return waterConnectionRequest;
	}
	
	
public GarbageConnectionRequest updateConnectionStatusBasedOnActionDisconnection(GarbageConnectionRequest waterConnectionRequest) {
		
		if(waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction() != null 
				  && waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction().equals(GCConstants.SUBMIT_APPLICATION_CONST)){
			List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);
			if (previousConnectionsList.size() > 0) { 
			  for (GarbageConnection previousConnectionsListObj : previousConnectionsList) {
				  if(previousConnectionsListObj.getStatus().equals(StatusEnum.ACTIVE)){
					  gcDao1.updateGarbageApplicationStatus(previousConnectionsListObj.getId(),
							  GCConstants.INACTIVE_STATUS); 
				  	}
			  	} 
			  }
		  waterConnectionRequest.getGarbageConnection().setStatus(StatusEnum.INACTIVE);
		}
		  
		  if(waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction() != null 
				  && waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction().equals(GCConstants.ACTION_REJECT)){
			  List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);
			  if (previousConnectionsList.size() > 0) { 
				  Collections.sort(previousConnectionsList, Comparator.comparing((GarbageConnection wc) -> wc.getAuditDetails().getLastModifiedTime()).reversed());
				  for (GarbageConnection previousConnectionsListObj : previousConnectionsList) {
					   if(previousConnectionsListObj.getApplicationStatus().equals(GCConstants.STATUS_APPROVED) 
							   || previousConnectionsListObj.getApplicationStatus().equals(GCConstants.APPROVED)){
						   gcDao1.updateGarbageApplicationStatus(previousConnectionsListObj.getId(),
									  GCConstants.ACTIVE_STATUS); 
						   waterConnectionRequest.getGarbageConnection().setStatus(StatusEnum.INACTIVE);
						   break;
					   }
				  }
			}
			  
		  }
		  return waterConnectionRequest;
	}
	
	public List<GarbageConnection> updateGarbageConnectionForDisconnectFlow(GarbageConnectionRequest waterConnectionRequest) {

		SearchCriteria criteria = new SearchCriteria();
		gcValidator.validateGarbageConnection(waterConnectionRequest, GCConstants.DISCONNECT_CONNECTION);
		mDMSValidator.validateMasterData(waterConnectionRequest, GCConstants.DISCONNECT_CONNECTION);

		Property property = validateProperty.getOrValidateProperty(waterConnectionRequest);
		validateProperty.validatePropertyFields(property, waterConnectionRequest.getRequestInfo());
		waterConnectionRequest = updateConnectionStatusBasedOnActionDisconnection(waterConnectionRequest);
		BusinessService businessService = workflowService.getBusinessService(
				waterConnectionRequest.getGarbageConnection().getTenantId(), waterConnectionRequest.getRequestInfo(),
				config.getDisconnectBusinessServiceName());
		GarbageConnection searchResult = getConnectionForUpdateRequest(
				waterConnectionRequest.getGarbageConnection().getId(), waterConnectionRequest.getRequestInfo());

//		boolean isPlumberSwapped = unmaskingUtil.getUnmaskedPlumberInfo(
//				waterConnectionRequest.getGarbageConnection().getPlumberInfo(), searchResult.getPlumberInfo());
		// if (isPlumberSwapped)
		// waterConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(waterConnectionRequest.getGarbageConnection(),
		// "WnSConnectionPlumberDecrypDisabled", GarbageConnection.class,
		// waterConnectionRequest.getRequestInfo()));

		String previousApplicationStatus = workflowService.getApplicationStatus(waterConnectionRequest.getRequestInfo(),
				waterConnectionRequest.getGarbageConnection().getApplicationNo(),
				waterConnectionRequest.getGarbageConnection().getTenantId(), config.getDisconnectBusinessServiceName());

		boolean isStateUpdatable = gcServiceUtil.getStatusForUpdate(businessService, previousApplicationStatus);

		enrichmentService.enrichUpdateGarbageConnection(waterConnectionRequest);
		actionValidator.validateUpdateRequest(waterConnectionRequest, businessService, previousApplicationStatus);
		gcValidator.validateUpdate(waterConnectionRequest, searchResult,
				GCConstants.DISCONNECT_CONNECTION);
		userService.updateUser(waterConnectionRequest, searchResult);
		// call calculator service to generate the demand for one time fee
		// if(!waterConnectionRequest.getGarbageConnection().getIsDisconnectionTemporary())
		calculationService.calculateFeeAndGenerateDemand(waterConnectionRequest, property);
		// check whether amount is due
		boolean isNoPayment = false;
		GarbageConnection waterConnection = waterConnectionRequest.getGarbageConnection();
		ProcessInstance processInstance = waterConnection.getProcessInstance();
		if (GCConstants.APPROVE_DISCONNECTION_CONST.equalsIgnoreCase(processInstance.getAction())) {
			isNoPayment = calculationService.fetchBill(waterConnection.getTenantId(), waterConnection.getConnectionNo(),
					waterConnectionRequest.getRequestInfo());
			if (isNoPayment) {
				processInstance.setComment(WORKFLOW_NO_PAYMENT_CODE);
			}
		}
		// Call workflow
		wfIntegrator.callWorkFlow(waterConnectionRequest, property);
		// check for edit and send edit notification
		// waterDaoImpl.pushForEditNotification(waterConnectionRequest,
		// isStateUpdatable);

		/* encrypt here */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionDetails(waterConnectionRequest.getGarbageConnection()));
		/* encrypt here for connection holder details */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionHolderDetails(waterConnectionRequest.getGarbageConnection()));

		gcDao.updateGarbageConnection(waterConnectionRequest, isStateUpdatable);

		// setting oldApplication Flag
		markOldApplication(waterConnectionRequest);
//		enrichmentService.postForMeterReading(waterConnectionRequest,  GCConstants.DISCONNECT_CONNECTION);
		if (!StringUtils.isEmpty(waterConnectionRequest.getGarbageConnection().getTenantId()))
			criteria.setTenantId(waterConnectionRequest.getGarbageConnection().getTenantId());
		enrichmentService.enrichProcessInstance(Arrays.asList(waterConnectionRequest.getGarbageConnection()), criteria,
				waterConnectionRequest.getRequestInfo());

		// Updating the workflow from approve for disconnection to pending for
		// disconnection execution when there are no dues
		if (GCConstants.APPROVE_DISCONNECTION_CONST.equalsIgnoreCase(processInstance.getAction()) && isNoPayment) {
			paymentUpdateService.noPaymentWorkflow(waterConnectionRequest, property,
					waterConnectionRequest.getRequestInfo());
		}

		/* decrypt here */
		waterConnectionRequest.setGarbageConnection(decryptConnectionDetails(waterConnectionRequest.getGarbageConnection(),
				waterConnectionRequest.getRequestInfo()));

		return Arrays.asList(waterConnectionRequest.getGarbageConnection());
	}

	public List<GarbageConnection> updateGarbageConnectionForReconnectFlow(GarbageConnectionRequest waterConnectionRequest) {

		SearchCriteria criteria = new SearchCriteria();

		gcValidator.validateGarbageConnection(waterConnectionRequest, GCConstants.RECONNECTION);
		mDMSValidator.validateMasterData(waterConnectionRequest, GCConstants.RECONNECTION);

		Property property = validateProperty.getOrValidateProperty(waterConnectionRequest);
		validateProperty.validatePropertyFields(property, waterConnectionRequest.getRequestInfo());
		BusinessService businessService = workflowService.getBusinessService(
				waterConnectionRequest.getGarbageConnection().getTenantId(), waterConnectionRequest.getRequestInfo(),
				config.getReconnectBusinessServiceName());
		GarbageConnection searchResult = getConnectionForUpdateRequest(
				waterConnectionRequest.getGarbageConnection().getId(), waterConnectionRequest.getRequestInfo());

//		boolean isPlumberSwapped = unmaskingUtil.getUnmaskedPlumberInfo(
//				waterConnectionRequest.getGarbageConnection().getPlumberInfo(), searchResult.getPlumberInfo());
		// if (isPlumberSwapped)
		// waterConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(waterConnectionRequest.getGarbageConnection(),
		// "WnSConnectionPlumberDecrypDisabled", GarbageConnection.class,
		// waterConnectionRequest.getRequestInfo()));

		String previousApplicationStatus = workflowService.getApplicationStatus(waterConnectionRequest.getRequestInfo(),
				waterConnectionRequest.getGarbageConnection().getApplicationNo(),
				waterConnectionRequest.getGarbageConnection().getTenantId(), config.getReconnectBusinessServiceName());

		boolean isStateUpdatable = gcServiceUtil.getStatusForUpdate(businessService, previousApplicationStatus);

		enrichmentService.enrichUpdateGarbageConnection(waterConnectionRequest);
		actionValidator.validateUpdateRequest(waterConnectionRequest, businessService, previousApplicationStatus);
		gcValidator.validateUpdate(waterConnectionRequest, searchResult, GCConstants.RECONNECTION);
		userService.updateUser(waterConnectionRequest, searchResult);
		// call calculator service to generate the demand for one time fee
		// if(!waterConnectionRequest.getGarbageConnection().getIsDisconnectionTemporary())
		calculationService.calculateFeeAndGenerateDemand(waterConnectionRequest, property);
		// check whether amount is due
		boolean isNoPayment = false;
		GarbageConnection waterConnection = waterConnectionRequest.getGarbageConnection();
		ProcessInstance processInstance = waterConnection.getProcessInstance();
		if (GCConstants.APPROVE_CONNECTION_CONST.equalsIgnoreCase(processInstance.getAction())) {
			isNoPayment = calculationService.fetchBillForReconnect(waterConnection.getTenantId(),
					waterConnection.getApplicationNo(), waterConnectionRequest.getRequestInfo());
			if (isNoPayment) {
				processInstance.setComment(WORKFLOW_NO_PAYMENT_CODE);
			}
		}
		// Call workflow
		wfIntegrator.callWorkFlow(waterConnectionRequest, property);
		// check for edit and send edit notification
		// waterDaoImpl.pushForEditNotification(waterConnectionRequest,
		// isStateUpdatable);

		/* encrypt here */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionDetails(waterConnectionRequest.getGarbageConnection()));
		/* encrypt here for connection holder details */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionHolderDetails(waterConnectionRequest.getGarbageConnection()));

		gcDao.updateGarbageConnection(waterConnectionRequest, isStateUpdatable);

		// setting oldApplication Flag
		markOldApplication(waterConnectionRequest);
//		enrichmentService.postForMeterReading(waterConnectionRequest,  GCConstants.DISCONNECT_CONNECTION);
		if (!StringUtils.isEmpty(waterConnectionRequest.getGarbageConnection().getTenantId()))
			criteria.setTenantId(waterConnectionRequest.getGarbageConnection().getTenantId());
		enrichmentService.enrichProcessInstance(Arrays.asList(waterConnectionRequest.getGarbageConnection()), criteria,
				waterConnectionRequest.getRequestInfo());

		// Updating the workflow from approve for disconnection to pending for
		// disconnection execution when there are no dues
		if (GCConstants.APPROVE_CONNECTION_CONST.equalsIgnoreCase(processInstance.getAction()) && isNoPayment) {
			paymentUpdateService.noPaymentWorkflow(waterConnectionRequest, property,
					waterConnectionRequest.getRequestInfo());
		}

		/* decrypt here */
		waterConnectionRequest.setGarbageConnection(decryptConnectionDetails(waterConnectionRequest.getGarbageConnection(),
				waterConnectionRequest.getRequestInfo()));

		return Arrays.asList(waterConnectionRequest.getGarbageConnection());
	}

	/**
	 * Search Water connection to be update
	 *
	 * @param id
	 * @param requestInfo
	 * @return water connection
	 */
	public GarbageConnection getConnectionForUpdateRequest(String id, RequestInfo requestInfo) {
		Set<String> ids = new HashSet<>(Arrays.asList(id));
		SearchCriteria criteria = new SearchCriteria();
		criteria.setIds(ids);
		List<GarbageConnection> connections = getGarbageConnectionsList(criteria, requestInfo);
		if (CollectionUtils.isEmpty(connections)) {
			StringBuilder builder = new StringBuilder();
			builder.append("GARBAGE CONNECTION NOT FOUND FOR: ").append(id).append(" :ID");
			throw new CustomException("INVALID_GARBAGECONNECTION_SEARCH", builder.toString());
		}

		return connections.get(0);
	}

	private List<GarbageConnection> getAllWaterApplications(GarbageConnectionRequest waterConnectionRequest) {
		GarbageConnection waterConnection = waterConnectionRequest.getGarbageConnection();
		SearchCriteria criteria = SearchCriteria.builder()
				.connectionNumber(Stream.of(waterConnection.getConnectionNo().toString()).collect(Collectors.toSet()))
				.build();
		if (waterConnectionRequest.isDisconnectRequest() || !StringUtils.isEmpty(waterConnection.getConnectionNo()))
			criteria.setIsInternalCall(true);
		return search(criteria, waterConnectionRequest.getRequestInfo());
	}

	private List<GarbageConnection> updateGarbageConnectionForModifyFlow(GarbageConnectionRequest waterConnectionRequest) {
		gcValidator.validateGarbageConnection(waterConnectionRequest, GCConstants.MODIFY_CONNECTION);
		mDMSValidator.validateMasterData(waterConnectionRequest, GCConstants.MODIFY_CONNECTION);
		BusinessService businessService = workflowService.getBusinessService(
				waterConnectionRequest.getGarbageConnection().getTenantId(), waterConnectionRequest.getRequestInfo(),
				config.getModifyGCBusinessServiceName());
		GarbageConnection searchResult = getConnectionForUpdateRequest(
				waterConnectionRequest.getGarbageConnection().getId(), waterConnectionRequest.getRequestInfo());

//		boolean isPlumberSwapped = unmaskingUtil.getUnmaskedPlumberInfo(
//				waterConnectionRequest.getGarbageConnection().getPlumberInfo(), searchResult.getPlumberInfo());
		// if (isPlumberSwapped)
		// waterConnectionRequest.setGarbageConnection(encryptionDecryptionUtil.decryptObject(waterConnectionRequest.getGarbageConnection(),
		// "WnSConnectionPlumberDecrypDisabled", GarbageConnection.class,
		// waterConnectionRequest.getRequestInfo()));

		Property property = validateProperty.getOrValidateProperty(waterConnectionRequest);
		validateProperty.validatePropertyFields(property, waterConnectionRequest.getRequestInfo());
		String previousApplicationStatus = workflowService.getApplicationStatus(waterConnectionRequest.getRequestInfo(),
				waterConnectionRequest.getGarbageConnection().getApplicationNo(),
				waterConnectionRequest.getGarbageConnection().getTenantId(), config.getModifyGCBusinessServiceName());
		enrichmentService.enrichUpdateGarbageConnection(waterConnectionRequest);
		actionValidator.validateUpdateRequest(waterConnectionRequest, businessService, previousApplicationStatus);
		userService.updateUser(waterConnectionRequest, searchResult);
		gcValidator.validateUpdate(waterConnectionRequest, searchResult, GCConstants.MODIFY_CONNECTION);
		wfIntegrator.callWorkFlow(waterConnectionRequest, property);
		boolean isStateUpdatable = gcServiceUtil.getStatusForUpdate(businessService, previousApplicationStatus);

		// check for edit and send edit notification
		gcDao1.pushForEditNotification(waterConnectionRequest, isStateUpdatable);

		/* encrypt here */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionDetails(waterConnectionRequest.getGarbageConnection()));
		/* encrypt here for connection holder details */
		waterConnectionRequest
				.setGarbageConnection(encryptConnectionHolderDetails(waterConnectionRequest.getGarbageConnection()));

		gcDao.updateGarbageConnection(waterConnectionRequest, isStateUpdatable);

		// setting oldApplication Flag
		markOldApplication(waterConnectionRequest);
		// check for edit and send edit notification
		gcDao1.pushForEditNotification(waterConnectionRequest, isStateUpdatable);
		enrichmentService.postForMeterReading(waterConnectionRequest, GCConstants.MODIFY_CONNECTION);

		/* decrypt here */
		waterConnectionRequest.setGarbageConnection(decryptConnectionDetails(waterConnectionRequest.getGarbageConnection(),
				waterConnectionRequest.getRequestInfo()));

		return Arrays.asList(waterConnectionRequest.getGarbageConnection());
	}

	public void markOldApplication(GarbageConnectionRequest waterConnectionRequest) {
		if (waterConnectionRequest.getGarbageConnection().getProcessInstance().getAction()
				.equalsIgnoreCase(APPROVE_CONNECTION)) {
			String currentModifiedApplicationNo = waterConnectionRequest.getGarbageConnection().getApplicationNo();
			List<GarbageConnection> previousConnectionsList = getAllWaterApplications(waterConnectionRequest);

			for (GarbageConnection waterConnection : previousConnectionsList) {
				if (!waterConnection.getOldApplication()
						&& !(waterConnection.getApplicationNo().equalsIgnoreCase(currentModifiedApplicationNo))) {
					waterConnection.setOldApplication(Boolean.TRUE);
					waterConnection = encryptConnectionDetails(waterConnection);
					GarbageConnectionRequest previousGarbageConnectionRequest = GarbageConnectionRequest.builder()
							.requestInfo(waterConnectionRequest.getRequestInfo()).garbageConnection(waterConnection)
							.build();
					gcDao.updateGarbageConnection(previousGarbageConnectionRequest, Boolean.TRUE);
				}
			}
		}
	}

	@Override
	public void disConnectGarbageConnection(String connectionNo, RequestInfo requestInfo, String tenantId) {
		// TODO Auto-generated method stub
		GarbageConnectionRequest connectionRequest = new GarbageConnectionRequest();
		connectionRequest.setRequestInfo(requestInfo);
		GarbageConnection waterConnection = new GarbageConnection();
		waterConnection.setConnectionNo(connectionNo);
		waterConnection.setTenantId(tenantId);
		connectionRequest.setGarbageConnection(waterConnection);
		List<GarbageConnection> waterConnectionList = getAllWaterApplications(connectionRequest);
		List<GarbageConnection> activeGarbageConnections = waterConnectionList.stream()
				.filter(connection -> connection.getStatus().toString().equalsIgnoreCase(GCConstants.ACTIVE_STATUS)
						&& !connection.getOldApplication())
				.collect(Collectors.toList());
		validateDisconnectGarbageConnection(waterConnectionList, connectionNo, requestInfo, tenantId,
				activeGarbageConnections);
		gcDao1.updateGarbageApplicationStatus(activeGarbageConnections.get(0).getId(), GCConstants.INACTIVE_STATUS);

	}

	private void validateDisconnectGarbageConnection(List<GarbageConnection> waterConnectionList, String connectionNo,
			RequestInfo requestInfo, String tenantId, List<GarbageConnection> activeGarbageConnectionList) {

		if (activeGarbageConnectionList.size() != 1) {
			throw new CustomException("EG_WS_DISCONNECTION_ERROR", GCConstants.ACTIVE_ERROR_MESSAGE);
		}

		if (!CollectionUtils.isEmpty(waterConnectionList)) {
			workflowService.validateInProgressWF(waterConnectionList, requestInfo, connectionNo);
		}

		boolean isBillUnpaid = gcServiceUtil.isBillUnpaid(connectionNo, tenantId, requestInfo);

		if (isBillUnpaid)
			throw new CustomException("EG_WS_DISCONNECTION_ERROR", GCConstants.DUES_ERROR_MESSAGE);

	}

	public GarbageConnectionResponse plainSearch(SearchCriteria criteria, RequestInfo requestInfo) {
		criteria.setIsSkipLevelSearch(Boolean.TRUE);
		GarbageConnectionResponse garbageConnectionsListForPlainSearch = getGarbageConnectionsListForPlainSearch(criteria, requestInfo);
		garbageConnectionsListForPlainSearch.setGarbageConnections(
				enrichmentService.enrichPropertyDetails(garbageConnectionsListForPlainSearch.getGarbageConnections(), criteria, requestInfo));
		gcValidator.validatePropertyForConnection(garbageConnectionsListForPlainSearch.getGarbageConnections());
		enrichmentService.enrichConnectionHolderDeatils(garbageConnectionsListForPlainSearch.getGarbageConnections(), criteria, requestInfo);
		return garbageConnectionsListForPlainSearch;
	}

	public GarbageConnectionResponse getGarbageConnectionsListForPlainSearch(SearchCriteria criteria,
			RequestInfo requestInfo) {
		return gcDao.getGarbageConnectionListForPlainSearch(criteria, requestInfo);
	}

	/**
	 * Replace the requestBody data and data from dB for those fields that come as
	 * masked (data containing "*" is identified as masked) in requestBody
	 *
	 * @param waterConnectionRequest  contains requestBody of waterConnection
	 * @param previousConnectionsList contains unmasked data from the search result
	 *                                of waterConnection
	 *
	 */
	private void swapConnHolders(GarbageConnectionRequest waterConnectionRequest,
			List<GarbageConnection> previousConnectionsList) {

		if (!ObjectUtils.isEmpty(waterConnectionRequest.getGarbageConnection().getConnectionHolders())
				&& !ObjectUtils.isEmpty(previousConnectionsList.get(0).getConnectionHolders())) {

			List<OwnerInfo> connHolders = waterConnectionRequest.getGarbageConnection().getConnectionHolders();
			List<OwnerInfo> searchedConnHolders = previousConnectionsList.get(0).getConnectionHolders();

			if (!ObjectUtils.isEmpty(connHolders.get(0).getOwnerType())
					&& !ObjectUtils.isEmpty(searchedConnHolders.get(0).getOwnerType())) {

				int k = 0;
				for (OwnerInfo holderInfo : connHolders) {
					if (holderInfo.getOwnerType().contains("*"))
						holderInfo.setOwnerType(searchedConnHolders.get(k).getOwnerType());
					if (holderInfo.getRelationship().contains("*"))
						holderInfo.setRelationship(searchedConnHolders.get(k).getRelationship());
					k++;

				}
			}
		}
	}

	/**
	 * Encrypts waterConnection details
	 *
	 * @param waterConnection contains waterConnection object
	 *
	 */
	private GarbageConnection encryptConnectionDetails(GarbageConnection waterConnection) {
		/* encrypt here */
		// waterConnection = encryptionDecryptionUtil.encryptObject(waterConnection,
		// WNS_ENCRYPTION_MODEL, GarbageConnection.class);
		// waterConnection = encryptionDecryptionUtil.encryptObject(waterConnection,
		// WNS_PLUMBER_ENCRYPTION_MODEL, GarbageConnection.class);
		return waterConnection;
	}

	/**
	 * Encrypts connectionOwner details coming from user service
	 *
	 * @param waterConnection contains waterConnection object
	 *
	 */
	private GarbageConnection encryptConnectionHolderDetails(GarbageConnection waterConnection) {
		/* encrypt here */
		List<OwnerInfo> connectionHolders = waterConnection.getConnectionHolders();
		if (!CollectionUtils.isEmpty(connectionHolders)) {
			int k = 0;
			for (OwnerInfo holderInfo : connectionHolders) {
				// waterConnection.getConnectionHolders().set(k,
				// encryptionDecryptionUtil.encryptObject(holderInfo,
				// WNS_OWNER_ENCRYPTION_MODEL, OwnerInfo.class));
				k++;
			}
		}
		return waterConnection;
	}

	/**
	 * Decrypts waterConnection details
	 *
	 * @param waterConnection contains waterConnection object
	 */
	private GarbageConnection decryptConnectionDetails(GarbageConnection waterConnection, RequestInfo requestInfo) {
		/* decrypt here */
		// waterConnection = encryptionDecryptionUtil.decryptObject(waterConnection,
		// WNS_ENCRYPTION_MODEL, GarbageConnection.class, requestInfo);
		// waterConnection = encryptionDecryptionUtil.decryptObject(waterConnection,
		// WNS_PLUMBER_ENCRYPTION_MODEL, GarbageConnection.class, requestInfo);
		List<OwnerInfo> connectionHolders = waterConnection.getConnectionHolders();
		// if (!CollectionUtils.isEmpty(connectionHolders))
		// waterConnection.setConnectionHolders(encryptionDecryptionUtil.decryptObject(connectionHolders,
		// WNS_OWNER_ENCRYPTION_MODEL, OwnerInfo.class, requestInfo));

		return waterConnection;
	}

	@Override
	public List<GarbageConnection> createGarbageConnection(GarbageConnectionRequest waterConnectionRequest,
			Boolean isMigration) {
		// TODO Auto-generated method stub
		return null;
	}

}
