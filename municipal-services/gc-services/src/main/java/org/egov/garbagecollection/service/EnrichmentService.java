package org.egov.garbagecollection.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.garbagecollection.repository.GcDaoImpl;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.egov.garbagecollection.config.GCConfiguration;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.producer.GCProducer;
import org.egov.garbagecollection.repository.IdGenRepository;
import org.egov.garbagecollection.repository.ServiceRequestRepository;
import org.egov.garbagecollection.util.EncryptionDecryptionUtil;
import org.egov.garbagecollection.util.UnmaskingUtil;
import org.egov.garbagecollection.util.GcServicesUtil;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.web.models.Connection.StatusEnum;
import org.egov.garbagecollection.web.models.Idgen.IdResponse;
import org.egov.garbagecollection.web.models.users.User;
import org.egov.garbagecollection.web.models.users.UserDetailResponse;
import org.egov.garbagecollection.web.models.users.UserSearchRequest;
import org.egov.garbagecollection.web.models.workflow.ProcessInstance;
import org.egov.garbagecollection.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.egov.garbagecollection.constants.GCConstants.DOCUMENT_ACCESS_AUDIT_MSG;

@Service
@Slf4j
public class EnrichmentService {
	@Autowired
	private GcServicesUtil gcServicesUtil;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private GCConfiguration config;

	@Autowired
	private ObjectMapper mapper;
	
	@Lazy
	@Autowired
	private GcDaoImpl garbageDao;
	
	@Autowired
	private UserService userService;

	@Lazy
	@Autowired
	private GcServiceImpl gcService;

	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private GCProducer producer;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	private UnmaskingUtil unmaskingUtil;
	

	@Autowired
	private GcServicesUtil gcUtil;


	/**
	 * Enrich water connection
	 * 
	 * @param garbageConnectionRequest WaterConnection Object
	 */
	@SuppressWarnings("unchecked")
	public void enrichGarbageConnection(GarbageConnectionRequest garbageConnectionRequest, int reqType) {
	String roleCodeName=null;
		AuditDetails auditDetails = gcServicesUtil
				.getAuditDetails(garbageConnectionRequest.getRequestInfo().getUserInfo().getUuid(), true);
		garbageConnectionRequest.getGarbageConnection().setAuditDetails(auditDetails);
		garbageConnectionRequest.getGarbageConnection().setId(UUID.randomUUID().toString());
		garbageConnectionRequest.getGarbageConnection().setStatus(StatusEnum.ACTIVE);
		
		// Log unit information for tracking
		if (!StringUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getUnitId())) {
			log.info("Enriching GC connection for Property: {}, Unit: {}", 
				garbageConnectionRequest.getGarbageConnection().getPropertyId(),
				garbageConnectionRequest.getGarbageConnection().getUnitId());
		}
		/*
		 *Changing Hard coded channel and moving hardcoded part to constant
		 *Moreover adding 3rd party channer config here on the basis  of role if it contains particular role.
		 *Abhishek Rana -- 30-12-2024
		 *		 */
		
		String userType = garbageConnectionRequest.getRequestInfo().getUserInfo().getType().toUpperCase();
		
		if (gcUtil.isModifyConnectionRequest(garbageConnectionRequest)){
			garbageConnectionRequest.getGarbageConnection().setStatus(StatusEnum.INACTIVE);
		}
		
		Object thirdPartyData = fetchThirdPartyIntegration(garbageConnectionRequest.getRequestInfo(), config.getStateLevelTenantId(), GCConstants.MDMS_WC_ROLE_MODLENAME , GCConstants.MDMS_WC_ROLE_MASTERNAME, userType,true);

		 Map<String, String> roleMap = new HashMap<>();
		 
	        if (thirdPartyData instanceof Map) {
	            List<Map<String, Object>> thirdPartyList = (List<Map<String, Object>>) 
	                Optional.ofNullable((Map<String, Object>) thirdPartyData)
	                        .map(data -> (Map<String, Object>) data.get(GCConstants.MDMS_RESPONSE_KEY))
	                        .map(mdmsRes -> (Map<String, Object>) mdmsRes.get(GCConstants.MDMS_WC_ROLE_MODLENAME))
	                        .map(commonMasters -> (List<Map<String, Object>>) commonMasters.get(GCConstants.MDMS_WC_ROLE_MASTERNAME))
	                        .orElse(Collections.emptyList());

	            thirdPartyList.forEach(role -> {
	                String category = (String) role.get(GCConstants.CATEGORY_KEY);
	                String roleCode = (String) role.get(GCConstants.ROLE_CODE_KEY);
	                roleMap.put(category, roleCode);
	            });
	        }
	        
	        List<String> requestRoles = garbageConnectionRequest.getRequestInfo()
	                .getUserInfo()
	                .getRoles()
	                .stream()
	                .map(Role::getCode) 
	                .collect(Collectors.toList());

	        for (String roleCode : roleMap.values()) {
	            if (requestRoles.contains(roleCode)) {
	            	roleCodeName=roleCode;
	            	
	            }
	        }


		if (roleCodeName != null) {
		    garbageConnectionRequest.getGarbageConnection().setChannel(roleCodeName);
		} else {
		    if (GCConstants.USER_TYPE_TO_CHANNEL.containsKey(userType)) {
		        garbageConnectionRequest.getGarbageConnection().setChannel(GCConstants.USER_TYPE_TO_CHANNEL.get(userType));
		    } else {
		        throw new IllegalStateException(
		            String.format("Unable to determine channel for userType: %s and roles: %s", 
		                          userType, 
		                          garbageConnectionRequest.getRequestInfo()
		                              .getUserInfo()
		                              .getRoles()
		                              .stream()
		                              .map(role -> role != null ? role.getCode() : "null")
		                              .collect(Collectors.toList()))); 
		    }
		}

		HashMap<String, Object> additionalDetail = new HashMap<>();
		if (garbageConnectionRequest.getGarbageConnection().getAdditionalDetails() == null) {
			for (String constValue : GCConstants.ADDITIONAL_OBJ_CONSTANT) {
				additionalDetail.put(constValue, null);
			}
		} else {
			additionalDetail = mapper
					.convertValue(garbageConnectionRequest.getGarbageConnection().getAdditionalDetails(), HashMap.class);
		}
		additionalDetail.put(GCConstants.APP_CREATED_DATE, BigDecimal.valueOf(System.currentTimeMillis()));
		garbageConnectionRequest.getGarbageConnection().setAdditionalDetails(additionalDetail);
	    //Setting ApplicationType
		String applicationType=null;
		
		
		if(reqType==GCConstants.CREATE_APPLICATION) {
			applicationType=GCConstants.NEW_GARBAGE_CONNECTION;
		}
		else if(reqType==GCConstants.DISCONNECT_CONNECTION) {
			applicationType=GCConstants.DISCONNECT_GARBAGE_CONNECTION;
		}
		else if (reqType == GCConstants.RECONNECTION) {
			applicationType=GCConstants.GARBAGE_RECONNECTION;
		}
		else {
			applicationType=GCConstants.MODIFY_GARBAGE_CONNECTION;
		}
		
		garbageConnectionRequest.getGarbageConnection().setApplicationType(applicationType);
		
		setApplicationIdGenIds(garbageConnectionRequest);
		setStatusForCreate(garbageConnectionRequest);

		GarbageConnection connection = garbageConnectionRequest.getGarbageConnection();

//		if (!CollectionUtils.isEmpty(connection.getRoadCuttingInfo())) {
//			connection.getRoadCuttingInfo().forEach(roadCuttingInfo -> {
//				roadCuttingInfo.setId(UUID.randomUUID().toString());
//				roadCuttingInfo.setStatus(Status.ACTIVE);
//				roadCuttingInfo.setAuditDetails(auditDetails);
//			});
//		}

//		if (applicationType.equalsIgnoreCase(MODIFY_WATER_CONNECTION) || applicationType.equalsIgnoreCase(DISCONNECT_WATER_CONNECTION)) {
//			if (!CollectionUtils.isEmpty(connection.getPlumberInfo())) {
//				connection.getPlumberInfo().forEach(plumberInfo -> {
//					plumberInfo.setId(null);
//					plumberInfo.setAuditDetails(auditDetails);
//				});
//			}
//		}

	}

	@SuppressWarnings("unchecked")
	public void enrichingAdditionalDetails(GarbageConnectionRequest garbageConnectionRequest) {
		HashMap<String, Object> additionalDetail = new HashMap<>();
		if (garbageConnectionRequest.getGarbageConnection().getAdditionalDetails() == null) {
			GCConstants.ADDITIONAL_OBJ_CONSTANT.forEach(key -> {
				additionalDetail.put(key, null);
			});
		} else {
			HashMap<String, Object> addDetail = mapper
					.convertValue(garbageConnectionRequest.getGarbageConnection().getAdditionalDetails(), HashMap.class);
			List<String> numberConstants = Arrays.asList(GCConstants.ADHOC_PENALTY, GCConstants.ADHOC_REBATE,
//					GCConstants.INITIAL_METER_READING_CONST,
					GCConstants.APP_CREATED_DATE,
					GCConstants.ESTIMATION_DATE_CONST);
			for (String constKey : GCConstants.ADDITIONAL_OBJ_CONSTANT) {
				if (addDetail.getOrDefault(constKey, null) != null && numberConstants.contains(constKey)) {
					BigDecimal big = new BigDecimal(String.valueOf(addDetail.get(constKey)));
					additionalDetail.put(constKey, big);
				} else {
					additionalDetail.put(constKey, addDetail.get(constKey));
				}
			}
			if (garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction()
					.equalsIgnoreCase(GCConstants.APPROVE_CONNECTION_CONST)) {
				additionalDetail.put(GCConstants.ESTIMATION_DATE_CONST, System.currentTimeMillis());
			}
//			additionalDetail.put(GCConstants.LOCALITY, addDetail.get(GCConstants.LOCALITY).toString());

			for (Map.Entry<String, Object> entry : addDetail.entrySet()) {
				if (additionalDetail.getOrDefault(entry.getKey(), null) == null) {
					additionalDetail.put(entry.getKey(), addDetail.get(entry.getKey()));
				}
			}
		}
		garbageConnectionRequest.getGarbageConnection().setAdditionalDetails(additionalDetail);
	}
	
	
	public Object fetchThirdPartyIntegration(RequestInfo requestInfo, String tenantId, String moduleName, String masterName, String userType, Boolean active) {
	    
		
		List<MasterDetail> masterDetails = new ArrayList<>();
		String filter = String.format("[?(@.category=='%s' && @.active==%b)]", userType, active);
	    
	    // Add master detail with the dynamic filter
	    masterDetails.add(MasterDetail.builder()
	            .name(GCConstants.MDMS_WC_ROLE_MASTERNAME)
	            .filter(filter)
	            .build());

     
        List<ModuleDetail> wfModuleDtls = Collections.singletonList(ModuleDetail.builder().masterDetails(masterDetails)
                .moduleName(GCConstants.MDMS_WC_ROLE_MODLENAME).build());

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(wfModuleDtls)
                .tenantId(config.getStateLevelTenantId())
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();
        String uRi=config.getMdmsHost()+config.getMdmsUrl();
        Object result = serviceRequestRepository.fetchmdmsResult(uRi, mdmsCriteriaReq);


	    return result;
	}
	

	/**
	 * Sets the WaterConnectionId for given WaterConnectionRequest
	 *
	 * @param request
	 *            WaterConnectionRequest which is to be created
	 */
	private void setApplicationIdGenIds(GarbageConnectionRequest request) {
		GarbageConnection waterConnection = request.getGarbageConnection();
		List<String> applicationNumbers = new ArrayList<>();
		if (request.getGarbageConnection().getApplicationStatus() != null && request.isDisconnectRequest()) {
			applicationNumbers = getIdList(request.getRequestInfo(),
					request.getGarbageConnection().getTenantId(), config.getWaterDisconnectionIdGenName(),
					config.getWaterDisconnectionIdGenFormat());
		} else {
			applicationNumbers = getIdList(request.getRequestInfo(),
					request.getGarbageConnection().getTenantId(), config.getGarbageApplicationIdGenName(),
					config.getGarbageApplicationIdGenFormat());
		}
		if (applicationNumbers.size() != 1) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("IDGEN_ERROR",
					"The Id of GarbageConnection returned by IdGen is not equal to number of GarbageConnection");
			throw new CustomException(errorMap);
		}
		waterConnection.setApplicationNo(applicationNumbers.get(0));
	}

	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idFormat) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idFormat, 1)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException(GCConstants.IDGEN_ERROR_CONST, "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}
	
	
	/**
	 * Enrich update water connection
	 * 
	 * @param garbageConnectionRequest WaterConnectionRequest Object
	 */
	public void enrichUpdateGarbageConnection(GarbageConnectionRequest garbageConnectionRequest) {
		AuditDetails auditDetails = gcServicesUtil
				.getAuditDetails(garbageConnectionRequest.getRequestInfo().getUserInfo().getUuid(), false);
		garbageConnectionRequest.getGarbageConnection().setAuditDetails(auditDetails);
		GarbageConnection connection = garbageConnectionRequest.getGarbageConnection();
		if (!CollectionUtils.isEmpty(connection.getDocuments())) {
			if(garbageConnectionRequest.getGarbageConnection().getApplicationType().equalsIgnoreCase("DISCONNECT_GARBAGE_CONNECTION") && garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction().equalsIgnoreCase("SUBMIT_APPLICATION")){
				connection.getDocuments().forEach(document -> {

						document.setId(UUID.randomUUID().toString());
						document.setApplicationId(garbageConnectionRequest.getGarbageConnection().getId());
						document.setStatus(Status.ACTIVE);

					document.setAuditDetails(auditDetails);
				});

			}else {


				connection.getDocuments().forEach(document -> {
					if (document.getId() == null) {
						document.setId(UUID.randomUUID().toString());
						document.setApplicationId(garbageConnectionRequest.getGarbageConnection().getId());
						document.setStatus(Status.ACTIVE);
					}
					document.setAuditDetails(auditDetails);
				});
			}
		}
//		if (!CollectionUtils.isEmpty(connection.getPlumberInfo())) {
//			connection.getPlumberInfo().forEach(plumberInfo -> {
//				if (plumberInfo.getId() == null) {
//					plumberInfo.setId(UUID.randomUUID().toString());
//				}
//				plumberInfo.setAuditDetails(auditDetails);
//			});
//		}
//		if (!CollectionUtils.isEmpty(connection.getRoadCuttingInfo())) {
//			connection.getRoadCuttingInfo().forEach(roadCuttingInfo -> {
//				if (roadCuttingInfo.getId() == null) {
//					roadCuttingInfo.setId(UUID.randomUUID().toString());
//					roadCuttingInfo.setStatus(Status.ACTIVE);
//				}
//				roadCuttingInfo.setAuditDetails(auditDetails);
//			});
//		}
		enrichingAdditionalDetails(garbageConnectionRequest);
	}
	
	/**
	 * Enrich water connection request and add connection no if status is approved
	 * 
	 * @param garbageConnectionRequest WaterConnectionRequest Object
	 */
	public void postStatusEnrichment(GarbageConnectionRequest garbageConnectionRequest) {
		String action = garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction();
		String appStatus = garbageConnectionRequest.getGarbageConnection().getApplicationStatus();

		if (GCConstants.ACTIVATE_CONNECTION.equalsIgnoreCase(action)) {
			// Reconnect / other flows: ACTIVATE_CONNECTION still sets connection number
			setConnectionNO(garbageConnectionRequest);
			garbageConnectionRequest.getGarbageConnection().setConnectionExecutionDate(System.currentTimeMillis());
		} else if (GCConstants.ACTION_PAY.equalsIgnoreCase(action)
				&& GCConstants.STATUS_APPROVED.equalsIgnoreCase(appStatus)) {
			// NewGC trimmed flow: PAY goes directly to CONNECTION_ACTIVATED
			setConnectionNO(garbageConnectionRequest);
			garbageConnectionRequest.getGarbageConnection().setConnectionExecutionDate(System.currentTimeMillis());
		}
	}

	/**
	 * Create meter reading for meter connection
	 *
	 * @param garbageConnectionrequest
	 */
	public void postForMeterReading(GarbageConnectionRequest garbageConnectionrequest, int reqType) {
		if (!StringUtils.isEmpty(garbageConnectionrequest.getGarbageConnection().getConnectionType())
				&& GCConstants.METERED_CONNECTION
				.equalsIgnoreCase(garbageConnectionrequest.getGarbageConnection().getConnectionType())) {
			if (reqType == GCConstants.UPDATE_APPLICATION && GCConstants.ACTIVATE_CONNECTION
					.equalsIgnoreCase(garbageConnectionrequest.getGarbageConnection().getProcessInstance().getAction())) {
//				garbageDao.postForMeterReading(garbageConnectionrequest);
			} else if (GCConstants.MODIFY_CONNECTION == reqType && GCConstants.APPROVE_CONNECTION.
					equals(garbageConnectionrequest.getGarbageConnection().getProcessInstance().getAction())) {
				SearchCriteria criteria = SearchCriteria.builder()
						.tenantId(garbageConnectionrequest.getGarbageConnection().getTenantId())
						.connectionNumber(Stream.of(garbageConnectionrequest.getGarbageConnection().getConnectionNo().toString()).collect(Collectors.toSet())).isCountCall(false)
						.build();
				List<GarbageConnection> connections = gcService.search(criteria, garbageConnectionrequest.getRequestInfo());
				if (!CollectionUtils.isEmpty(connections)) {
					GarbageConnection connection = connections.get(connections.size() - 1);
					if (!connection.getConnectionType().equals(GCConstants.METERED_CONNECTION)) {
//						garbageDao.postForMeterReading(garbageConnectionrequest);
					}
				}
			}
		}
	}
    
    
    /**
     * Enrich water connection request and set water connection no
     * @param request WaterConnectionRequest Object
     */
	private void setConnectionNO(GarbageConnectionRequest request) {
		List<String> connectionNumbers = getIdList(request.getRequestInfo(), request.getGarbageConnection().getTenantId(),
				config.getGarbageConnectionIdGenName(), config.getGarbageConnectionIdGenFormat());
		if (connectionNumbers.size() != 1) {
			throw new CustomException("IDGEN_ERROR",
					"The Id of GarbageConnection returned by IdGen is not equal to number of GarbageConnection");
		}
		request.getGarbageConnection().setConnectionNo(connectionNumbers.get(0));
	}

	/**
	 * Enrich fileStoreIds
	 *
	 * @param garbageConnectionRequest WaterConnectionRequest Object
	 */
	public void enrichFileStoreIds(GarbageConnectionRequest garbageConnectionRequest) {
		try {
			log.info("ACTION " + garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction());
			log.info("ApplicationStatus " + garbageConnectionRequest.getGarbageConnection().getApplicationStatus());
			if (garbageConnectionRequest.getGarbageConnection().getApplicationStatus()
					.equalsIgnoreCase(GCConstants.PENDING_APPROVAL_FOR_CONNECTION_CODE)
					|| garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction()
					.equalsIgnoreCase(GCConstants.ACTION_PAY)) {
				garbageDao.enrichFileStoreIds(garbageConnectionRequest);
			}
		} catch (Exception ex) {
			log.debug(ex.toString());
		}
	}

	/**
	 * Sets status for create request
	 *
	 * @param garbageConnectionRequest The create request
	 */
	private void setStatusForCreate(GarbageConnectionRequest garbageConnectionRequest) {
		if (garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction()
				.equalsIgnoreCase(GCConstants.ACTION_INITIATE)) {
			garbageConnectionRequest.getGarbageConnection().setApplicationStatus(GCConstants.STATUS_INITIATED);
		}
	}

	/**
	 * Enrich
	 *
	 * @param garbageConnectionList - List Of GarbageConnectionObject
	 * @param criteria            - Search Criteria
	 * @param requestInfo         - RequestInfo Object
	 */
	public void enrichConnectionHolderDeatils(List<GarbageConnection> garbageConnectionList, SearchCriteria criteria,
											  RequestInfo requestInfo) {
		if (CollectionUtils.isEmpty(garbageConnectionList))
			return;
		Set<String> connectionHolderIds = new HashSet<>();
		for (GarbageConnection garbageConnection : garbageConnectionList) {
			if (!CollectionUtils.isEmpty(garbageConnection.getConnectionHolders())) {
				connectionHolderIds.addAll(garbageConnection.getConnectionHolders().stream()
						.map(OwnerInfo::getUuid).collect(Collectors.toSet()));
			}
		}
		if (CollectionUtils.isEmpty(connectionHolderIds))
			return;
		UserSearchRequest userSearchRequest = userService.getBaseUserSearchRequest(criteria.getTenantId(), requestInfo);
		userSearchRequest.setUuid(connectionHolderIds);
		UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);
		enrichConnectionHolderInfo(userDetailResponse, garbageConnectionList,requestInfo);

	}

	/**
	 * Populates the owner fields inside of the water connection objects from the response got from calling user api
	 * @param userDetailResponse
	 * @param garbageConnectionList List of water connection whose owner's are to be populated from userDetailsResponse
	 */
	public void enrichConnectionHolderInfo(UserDetailResponse userDetailResponse, List<GarbageConnection> garbageConnectionList, RequestInfo requestInfo) {
		List<OwnerInfo> connectionHolderInfos = userDetailResponse.getUser();
		Map<String, OwnerInfo> userIdToConnectionHolderMap = new HashMap<>();
		connectionHolderInfos.forEach(user -> userIdToConnectionHolderMap.put(user.getUuid(), user));
		garbageConnectionList.forEach(garbageConnection -> {
			if (!CollectionUtils.isEmpty(garbageConnection.getConnectionHolders())) {
				garbageConnection.getConnectionHolders().forEach(holderInfo -> {
					if (userIdToConnectionHolderMap.get(holderInfo.getUuid()) == null)
						throw new CustomException("OWNER SEARCH ERROR", "The owner of the water application"
								+ garbageConnection.getApplicationNo() + " is not coming in user search");
					else {
						Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
						if (isOpenSearch)
							holderInfo.addUserDetail(getMaskedOwnerInfo(userIdToConnectionHolderMap.get(holderInfo.getUuid())));
						else
							holderInfo.addUserDetail(userIdToConnectionHolderMap.get(holderInfo.getUuid()));

					}

				});
			}
		});
	}

	public Boolean isSearchOpen(org.egov.common.contract.request.User userInfo) {

		return userInfo.getType().equalsIgnoreCase("SYSTEM")
				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("ANONYMOUS");
	}

	private User getMaskedOwnerInfo(OwnerInfo info) {

		info.setMobileNumber(null);
		info.setUuid(null);
		info.setUserName(null);
		info.setGender(null);
		info.setAltContactNumber(null);
		info.setPwdExpiryDate(null);

		return info;
	}


	/**
	 * Filter the connection from connection activated or modified state
	 *
	 * @param connectionList
	 * @return
	 */
	public List<GarbageConnection> filterConnections(List<GarbageConnection> connectionList) {
		HashMap<String, Connection> connectionHashMap = new LinkedHashMap<>();
		connectionList.forEach(connection -> {
			if (!StringUtils.isEmpty(connection.getConnectionNo())) {
				if (connectionHashMap.get(connection.getConnectionNo()) == null
						&& GCConstants.FINAL_CONNECTION_STATES.contains(connection.getApplicationStatus())) {
					connectionHashMap.put(connection.getConnectionNo(), connection);
				} else if (connectionHashMap.get(connection.getConnectionNo()) != null
						&& GCConstants.FINAL_CONNECTION_STATES.contains(connection.getApplicationStatus())) {
					if (connectionHashMap.get(connection.getConnectionNo()).getApplicationStatus()
							.equals(connection.getApplicationStatus())) {
						HashMap additionalDetail1 = new HashMap<>();
						HashMap additionalDetail2 = new HashMap<>();
						additionalDetail1 = mapper.convertValue(
								connectionHashMap.get(connection.getConnectionNo()).getAdditionalDetails(),
								HashMap.class);
						additionalDetail2 = mapper.convertValue(connection.getAdditionalDetails(), HashMap.class);
						BigDecimal creationDate1 = (BigDecimal) additionalDetail1.get(GCConstants.APP_CREATED_DATE);
						BigDecimal creationDate2 = (BigDecimal) additionalDetail2.get(GCConstants.APP_CREATED_DATE);
						if (creationDate1.compareTo(creationDate2) == -1) {
							connectionHashMap.put(connection.getConnectionNo(), connection);
						}
					} else if (connection.getApplicationStatus().equals(GCConstants.MODIFIED_FINAL_STATE )) {
							connectionHashMap.put(connection.getConnectionNo(), connection);
					} 
				}
				
			}
		});
		return new ArrayList(connectionHashMap.values());
	}

	public List<GarbageConnection> enrichPropertyDetails(List<GarbageConnection> garbageConnectionList, SearchCriteria criteria, RequestInfo requestInfo) {
		List<GarbageConnection> finalConnectionList = new ArrayList<>();
		if (CollectionUtils.isEmpty(garbageConnectionList))
			return finalConnectionList;

		Set<String> propertyIds = new HashSet<>();
		Map<String, List<OwnerInfo>> propertyToOwner = new HashMap<>();
		for (GarbageConnection garbageConnection : garbageConnectionList) {
			if (!StringUtils.isEmpty(garbageConnection.getPropertyId()))
				propertyIds.add(garbageConnection.getPropertyId());
		}
		if (!CollectionUtils.isEmpty(propertyIds)) {
			PropertyCriteria propertyCriteria = new PropertyCriteria();
			if (!StringUtils.isEmpty(criteria.getTenantId())) {
				propertyCriteria.setTenantId(criteria.getTenantId());
			}
			propertyCriteria.setPropertyIds(propertyIds);
			List<Property> propertyList = gcServicesUtil.getPropertyDetails(serviceRequestRepository.fetchResult(gcServicesUtil.getPropertyURL(propertyCriteria,requestInfo),
					RequestInfoWrapper.builder().requestInfo(requestInfo).build()));

			if (!CollectionUtils.isEmpty(propertyList)) {
				for (Property property : propertyList) {
					propertyToOwner.put(property.getPropertyId(), property.getOwners());
				}
			}

			for (GarbageConnection garbageConnection : garbageConnectionList) {
				HashMap<String, Object> additionalDetail = new HashMap<>();
				HashMap<String, Object> addDetail = mapper
						.convertValue(garbageConnection.getAdditionalDetails(), HashMap.class);

				for (Map.Entry<String, Object> entry : addDetail.entrySet()) {
					if (additionalDetail.getOrDefault(entry.getKey(), null) == null) {
						additionalDetail.put(entry.getKey(), addDetail.get(entry.getKey()));
					}
				}
				List<OwnerInfo> ownerInfoList = propertyToOwner.get(garbageConnection.getPropertyId());
				if (!CollectionUtils.isEmpty(ownerInfoList)) {
					additionalDetail.put("ownerName", ownerInfoList.get(0).getName());
				}
				garbageConnection.setAdditionalDetails(additionalDetail);
				finalConnectionList.add(garbageConnection);
			}


		}
		return finalConnectionList;
	}

	public void enrichProcessInstance(List<GarbageConnection> garbageConnectionList, SearchCriteria criteria,
									  RequestInfo requestInfo) {
		if (CollectionUtils.isEmpty(garbageConnectionList))
			return;

		PlainAccessRequest apiPlainAccessRequest = requestInfo.getPlainAccessRequest();

		Map<String, ProcessInstance> processInstances = null;
		Set<String> applicationNumbers = garbageConnectionList.stream().map(GarbageConnection::getApplicationNo).collect(Collectors.toSet());

		if (criteria.getTenantId() != null)
			processInstances = wfService.getProcessInstances(requestInfo, applicationNumbers,
					criteria.getTenantId(), null);
		else
			processInstances = wfService.getProcessInstances(requestInfo, applicationNumbers,
					requestInfo.getUserInfo().getTenantId(), null);

		for (GarbageConnection garbageConnection : garbageConnectionList) {
			if (!ObjectUtils.isEmpty(processInstances.get(garbageConnection.getApplicationNo()))) {
				ProcessInstance processInstance = processInstances.get(garbageConnection.getApplicationNo());
				garbageConnection.getProcessInstance().setBusinessService(processInstance.getBusinessService());
				garbageConnection.getProcessInstance().setModuleName(processInstance.getModuleName());
				if (!ObjectUtils.isEmpty(processInstance.getAssignes()))
					garbageConnection.getProcessInstance().setAssignes(processInstance.getAssignes());
			}
		}
		requestInfo.setPlainAccessRequest(apiPlainAccessRequest);
	}

	public void enrichDocumentDetails(List<GarbageConnection> garbageConnectionList, SearchCriteria criteria,
									  RequestInfo requestInfo) {
		if (CollectionUtils.isEmpty(garbageConnectionList))
			return;

		if(!criteria.getIsFilestoreIdRequire() || garbageConnectionList.size()>1){
			for(int i= 0; i<garbageConnectionList.size();i++){
				List<Document> documentList = garbageConnectionList.get(i).getDocuments();
				for(int j =0; (documentList !=null && j<documentList.size()); j++){
					documentList.get(j).setFileStoreId(null);
				}
				garbageConnectionList.get(i).setDocuments(documentList);
			}

		}
		else{
			List<String> uuids = new ArrayList<>();
			if(garbageConnectionList.get(0).getConnectionHolders() != null){
				for(OwnerInfo connectionHolder : garbageConnectionList.get(0).getConnectionHolders()){
					uuids.add(connectionHolder.getUuid());
				}
			}

			PropertyCriteria propertyCriteria = new PropertyCriteria();
			propertyCriteria.setPropertyIds(Collections.singleton(garbageConnectionList.get(0).getPropertyId()));
			propertyCriteria.setTenantId(garbageConnectionList.get(0).getTenantId());

			List<Property> propertyList = gcServicesUtil.getPropertyDetails(serviceRequestRepository.fetchResult(gcServicesUtil.getPropertyURL(propertyCriteria,requestInfo),
					RequestInfoWrapper.builder().requestInfo(requestInfo).build()));
			if(propertyList != null && propertyList.size()==1){
				List<OwnerInfo> ownerInfoList = propertyList.get(0).getOwners();
				for(OwnerInfo ownerInfo: ownerInfoList)
					uuids.add(ownerInfo.getUuid());
			}

			for(String uuid : uuids){
				Map<String, Object> auditObject = new HashMap<>();
				auditObject.put("id",UUID.randomUUID().toString());
				auditObject.put("timestamp",System.currentTimeMillis());
				auditObject.put("userId",uuid);
				auditObject.put("accessBy", requestInfo.getUserInfo().getUuid());
				auditObject.put("purpose",DOCUMENT_ACCESS_AUDIT_MSG);

				producer.push(config.getDocumentAuditTopic(),uuid, auditObject);
			}


		}


	}

	/**
	 * Method to take un-mask the connectionHolder details coming in from _update api
	 *
	 * @param garbageConnection GarbageConnection
	 * @param requestInfo RequestInfo
	 * @return unmasked ConnectionHolder details
	 */
	public OwnerInfo getConnectionHolderDetailsForUpdateCall(GarbageConnection garbageConnection, RequestInfo requestInfo) {
		if (ObjectUtils.isEmpty(garbageConnection))
			return null;
		Set<String> connectionHolderIds = new HashSet<>();
		if (!CollectionUtils.isEmpty(garbageConnection.getConnectionHolders())) {
			connectionHolderIds.addAll(garbageConnection.getConnectionHolders().stream()
					.map(OwnerInfo::getUuid).collect(Collectors.toSet()));
		}
		if (CollectionUtils.isEmpty(connectionHolderIds))
			return null;

		unmaskingUtil.getOwnerDetailsUnmasked(garbageConnection, requestInfo);
		UserDetailResponse userDetailResponse = new UserDetailResponse();

		List<GarbageConnection> garbageConnectionList = new ArrayList<>();
		garbageConnectionList.add(garbageConnection);
		userDetailResponse.setUser(garbageConnection.getConnectionHolders());
		enrichConnectionHolderInfo(userDetailResponse, garbageConnectionList, requestInfo);
		return userDetailResponse.getUser().get(0);
	}

}

