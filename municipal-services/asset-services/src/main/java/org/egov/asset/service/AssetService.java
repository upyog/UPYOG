package org.egov.asset.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.dto.AssetAssignmentDTO;
import org.egov.asset.dto.AssetAddressDTO;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.dto.AssetSearchDTO;
import org.egov.asset.repository.AssetRepository;
import org.egov.asset.repository.RestCallRepository;
import org.egov.asset.util.AssetConstants;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.util.AssetValidator;
import org.egov.asset.util.MdmsUtil;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetActionRequest;
import org.egov.asset.web.models.AssetActionResponse;
import org.egov.asset.web.models.AssetApplicationDetail;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.egov.asset.web.models.AssetUpdate;
import org.egov.asset.web.models.AssetUpdateRequest;
import org.egov.asset.web.models.CreationReason;
import org.egov.asset.web.models.RequestInfoWrapper;
import org.egov.asset.web.models.UserSearchCriteria;
import org.egov.asset.web.models.workflow.Action;
import org.egov.asset.web.models.workflow.BusinessServiceResponse;
import org.egov.asset.web.models.workflow.State;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.BooleanUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Service
@Slf4j
public class AssetService {

	@Autowired
	MdmsUtil util;
	@Autowired
	AssetRepository assetRepository;
	@Autowired
	AssetValidator assetValidator;
	@Autowired
	EnrichmentService enrichmentService;
	@Autowired
	WorkflowIntegrator workflowIntegrator;
	@Autowired
	WorkflowService workflowService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AssetUtil assetUtil;

	@Autowired
	private AssetConfiguration assetConfiguration;

	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * does all the validations required to create Asset Record in the system
	 * 
	 * @param assetRequest
	 * @return
	 */
	public Asset create(AssetRequest assetRequest) {
		log.debug("Asset create service method called");
		RequestInfo requestInfo = assetRequest.getRequestInfo();
		String tenantId = assetRequest.getAsset().getTenantId().split("\\.")[0];
//		Object mdmsData = util.mDMSCall(requestInfo, tenantId);

		// Application can not be created statelevel
		if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
			throw new CustomException(AssetErrorConstants.INVALID_TENANT,
					" Application cannot be create at StateLevel");
		}

		// Since approval number should be generated at approve stage
		if (!StringUtils.isEmpty(assetRequest.getAsset().getApprovalNo())) {
			assetRequest.getAsset().setApprovalNo(null);
		}

//		assetValidator.validateCreate(assetRequest, mdmsData);

		// enrichmentService.enrichAssetCreateRequest(assetRequest, mdmsData);
		enrichmentService.enrichAssetCreateRequest(assetRequest);
		// workflowIntegrator.callWorkFlow(assetRequest);
		workflowService.updateWorkflow(assetRequest, CreationReason.CREATE);
		// assetRequest.getAsset().setApplicant("100");
		assetRepository.save(assetRequest);
//		return bpaRequest.getBPA();

		return assetRequest.getAsset();

	}

	public List<AssetDTO> search(AssetSearchCriteria criteria, RequestInfo requestInfo) {
		List<Asset> assets = new LinkedList<>();
		//assetValidator.validateSearch(requestInfo, criteria);
		List<String> roles = new ArrayList<>();
		for (Role role : requestInfo.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		
		// if ((criteria.tenantIdOnly() || criteria.isEmpty()) &&
		// roles.contains(AssetConstants.ASSET_INITIATOR)) {
		
//		if ((criteria.tenantIdOnly() ||  criteria.isEmpty())) {
//			log.debug("loading data of created and by me");
//			assets = this.getAssetCreatedForByMe(criteria, requestInfo);
//			log.debug("no of assets retuning by the search query" + assets.size());
//		} else 
		if (roles.contains(AssetConstants.EMPLOYEE)) {
			List<String> listOfStatus = getAccountStatusListByRoles(criteria.getTenantId(),
					requestInfo.getUserInfo().getRoles());
			 if (CollectionUtils.isEmpty(listOfStatus)) {
			 	throw new CustomException("SEARCH_ACCOUNT_BY_ROLES",
			 			"Search can't be performed by this Employee due to lack of roles.");
			 }
			 criteria.setListOfstatus(listOfStatus);
			 criteria.setCreatedBy(null);
			 assets = getAssetsFromCriteria(criteria);
		} else if (roles.contains(AssetConstants.CITIZEN)) {
			criteria.setStatus("APPROVED");
//			criteria.setBookingStatus("AVAILABLE");
			
			assets = getAssetsFromCriteria(criteria);
		} else {
			assets = getAssetsFromCriteria(criteria);
		}

		if (criteria.getApplicationNo() != null) {
			return assets.stream().map(asset -> modelMapper.map(asset, AssetDTO.class)).collect(Collectors.toList());
		} else {
			return assets.stream().map(this::convertToAssetSearchDTO).collect(Collectors.toList());
		}
	}

	private List<String> getAccountStatusListByRoles(String tenantId, List<Role> roles) {
		List<String> rolesWithinTenant = getRolesByTenantId(tenantId, roles);
		Set<String> statusWithRoles = new HashSet();
		try {
			if (!CollectionUtils.isEmpty(rolesWithinTenant)) {
				for (String r : rolesWithinTenant) {
					if (r.equalsIgnoreCase(AssetConstants.ASSET_APPROVER)) {
						statusWithRoles.add(AssetConstants.STATUS_PENDINGFORAPPROVAL);
						statusWithRoles.add(AssetConstants.STATUS_APPROVED);
						statusWithRoles.add(AssetConstants.STATUS_REJECTED);
					}
					if (r.equalsIgnoreCase(AssetConstants.ASSET_CREATOR)) {
						statusWithRoles.add(AssetConstants.STATUS_PENDINGFORMODIFICATION);
						statusWithRoles.add(AssetConstants.STATUS_PENDINGFORAPPROVAL);
						statusWithRoles.add(AssetConstants.STATUS_APPROVED);
						statusWithRoles.add(AssetConstants.STATUS_INITIATE);
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return new ArrayList<>(statusWithRoles);
	}

	private List<String> getRolesByTenantId(String tenantId, List<Role> roles) {
		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}

	private AssetSearchDTO convertToAssetSearchDTO(Asset asset) {
		AssetSearchDTO assetSearchDTO = modelMapper.map(asset, AssetSearchDTO.class);
		if (asset.getAssetAssignment() != null) {
			assetSearchDTO.setAssetAssignment(modelMapper.map(asset.getAssetAssignment(), AssetAssignmentDTO.class));
		}
		assetSearchDTO.setAddressDetails(modelMapper.map(asset.getAddressDetails(), AssetAddressDTO.class));

		return assetSearchDTO;
	}

	private List<Asset> getAssetCreatedForByMe(AssetSearchCriteria criteria, RequestInfo requestInfo) {
		List<Asset> assets = null;
		UserSearchCriteria userSearchRequest = new UserSearchCriteria();
		if (criteria.getTenantId() != null) {
			userSearchRequest.setTenantId(criteria.getTenantId());
		}
		List<String> uuids = new ArrayList<>();
		if (requestInfo.getUserInfo() != null && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
		}
		log.debug("loading data of created and by me" + uuids.toString());
		assets = getAssetsFromCriteria(criteria);
		return assets;
	}

	/**
	 * Returns the assets with enriched owners from user service
	 * 
	 * @param criteria    The object containing the parameters on which to search
	 * @param requestInfo The search request's requestInfo
	 * @return List of assets for the given criteria
	 */
	public List<Asset> getAssetsFromCriteria(AssetSearchCriteria criteria) {
		List<Asset> assets = assetRepository.getAssetData(criteria);
		if (assets.isEmpty())
			return Collections.emptyList();
		return assets;
	}

	/**
	 * does all the validations required to update Asset Record in the system
	 * 
	 * @param assetRequest
	 */
	public Asset update(@Valid AssetRequest assetRequest) {
		log.debug("Asset create service method called");
		RequestInfo requestInfo = assetRequest.getRequestInfo();
		String tenantId = assetRequest.getAsset().getTenantId().split("\\.")[0];
		Object mdmsData = util.mDMSCall(requestInfo, tenantId);
		Asset asset = assetRequest.getAsset();
		if (asset.getId() == null) {
			throw new CustomException(AssetErrorConstants.UPDATE_ERROR, "Asset Not found in the System" + asset);
		}

		enrichmentService.enrichAssetUpdateRequest(assetRequest, mdmsData);
		workflowService.updateWorkflow(assetRequest, CreationReason.UPDATE);
//		wfIntegrator.callWorkFlow(assetRequest);
		assetRepository.update(assetRequest);

		return assetRequest.getAsset();
	}

	public Asset assignment(@Valid AssetRequest assetRequest) {
		log.debug("Asset assignment service method called");
		// RequestInfo requestInfo = assetRequest.getRequestInfo();

		// Application can not be created statelevel
		if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
			throw new CustomException(AssetErrorConstants.INVALID_TENANT,
					" Application cannot be create at StateLevel");
		}
		enrichmentService.enrichAssetOtherOperationsCreateRequest(assetRequest);
		assetRepository.saveAssignment(assetRequest);
		return assetRequest.getAsset();
	}

	public Asset updateAssignment(@Valid AssetRequest assetRequest) {
		log.debug("Asset assignment service method called");
		// RequestInfo requestInfo = assetRequest.getRequestInfo();

		// Application can not be created statelevel
		if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
			throw new CustomException(AssetErrorConstants.INVALID_TENANT,
					" Application cannot be create at StateLevel");
		}
		enrichmentService.enrichAssetOtherOperationsUpdateRequest(assetRequest);
		// assetRepository.updateAssignment(assetRequest);
		return assetRequest.getAsset();
	}

	public AssetActionResponse getActionsOnApplication(AssetActionRequest assetActionRequest) {
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		AssetActionResponse assetActionResponse = null;
		try {
			if (CollectionUtils.isEmpty(assetActionRequest.getApplicationNumbers())) {
				throw new CustomException("INVALID_REQUEST", "Please provide Asset Application numbers");
			}
			assetActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
				AssetSearchCriteria assetSearchCriteria = AssetSearchCriteria.builder().applicationNo(applicationNumber)
						.build();

				List<Asset> assets = assetRepository.getAssetData(assetSearchCriteria);
				if (CollectionUtils.isEmpty(assets)) {
					throw new CustomException("ASSET_NOT_FOUND", "No Asset found with provided input.");
				}
				Asset asset = null != assets ? assets.get(0) : null;
				String applicationStatus = asset.getStatus();
				String applicationTenantId = asset.getTenantId();
				String applicationBusinessId = assetUtil.ASSET_BUSINESS_SERVICE;
				List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId,
						assetActionRequest.getRequestInfo().getUserInfo().getRoles(), assetUtil.ASSET_BUSINESS_SERVICE);

				StringBuilder uri = new StringBuilder(assetConfiguration.getWfHost());
				uri.append(assetConfiguration.getWfBusinessServiceSearchPath());
				uri.append("?tenantId=").append(applicationTenantId);
				uri.append("&businessServices=").append(applicationBusinessId);
				RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
						.requestInfo(assetActionRequest.getRequestInfo()).build();
				Optional<Object> response = restCallRepository.fetchResult(uri, requestInfoWrapper);
				if (!response.isPresent()) {
					throw new CustomException("WF_SEARCH_FAILED", "Failed to fetch WF business service.");
				}
				LinkedHashMap<String, Object> responseObject = (LinkedHashMap<String, Object>) restCallRepository
						.fetchResult(uri, requestInfoWrapper).get();

				BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(responseObject,
						BusinessServiceResponse.class);
				if (null == businessServiceResponse
						|| CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
					throw new CustomException("NO_BUSINESS_SERVICE_FOUND",
							"Business service not found for application number: " + applicationNumber);
				}
				List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
						.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), applicationStatus)
								&& !StringUtils.equalsIgnoreCase(state.getApplicationStatus(),
										AssetConstants.STATUS_APPROVED))
						.collect(Collectors.toList());

				// filtering actions based on roles
				List<String> actions = new ArrayList<>();
				/*
				 * stateList.stream().forEach(state -> { state.getActions().stream().filter(
				 * action -> action.getRoles().stream().anyMatch(role ->
				 * rolesWithinTenant.contains(role))) .forEach(action -> {
				 * actions.add(action.getAction()); }); });
				 */

				for (State state : stateList) {
					List<Action> actionsList = state.getActions(); // Explicitly noting that this is a List<Action>
					for (Action action : actionsList) {
						List<String> rolesList = action.getRoles(); // Explicitly noting that this is a List<Role>
						for (String role : rolesList) {
							if (rolesWithinTenant.contains(role)) {
								actions.add(action.getAction());
								break; // Break to avoid adding the same action multiple times if multiple roles match
							}
						}
					}
				}

				applicationActionMaps.put(applicationNumber, actions);

			});

			List<AssetApplicationDetail> nextActionList = new ArrayList<>();
			applicationActionMaps.entrySet().stream().forEach(entry -> {
				nextActionList.add(AssetApplicationDetail.builder().applicationNumber(entry.getKey())
						.action(entry.getValue()).build());
			});

			assetActionResponse = AssetActionResponse.builder().applicationDetails(nextActionList).build();

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return assetActionResponse;
	}

	public AssetActionResponse getCountOfAllApplicationTypes(AssetActionRequest actionRequest) {
		AssetActionResponse assetActionResponse = null;
		List<String> statusList = null;
		try {
			assetActionResponse = AssetActionResponse.builder().build();
			if (null != actionRequest) {
				statusList = assetRepository.getTypesOfAllApplications(actionRequest.getIsHistoryCall(),
						actionRequest.getTenantId());
			}
			if (!CollectionUtils.isEmpty(statusList)) {
				assetActionResponse
						.setApplicationTypesCount(statusList.stream().filter(status -> StringUtils.isNotEmpty(status))
								.collect(Collectors.groupingBy(String::toString, Collectors.counting())));
			}

		} catch (Exception e) {
			throw new CustomException("FAILED_TO_FETCH", "Failed to fetch Application types.");
		}
		return assetActionResponse;
	}

	List<String> getRolesWithinTenant(String tenantId, List<Role> roles, String assetBusinessService) {

		List<String> roleCodes = new ArrayList<>();
		try {
			if (StringUtils.equalsIgnoreCase(assetBusinessService, AssetConstants.ASSET_BusinessService)) {
				for (Role role : roles) {
					if (StringUtils.equalsIgnoreCase(role.getCode(), AssetConstants.ASSET_CREATOR)) {
						roleCodes.add(AssetConstants.ASSET_WF_CREATOR);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), AssetConstants.ASSET_APPROVER)) {
						roleCodes.add(AssetConstants.ASSET_WF_APPROVER);
					}
				}
			}

			if (StringUtils.equalsIgnoreCase(assetBusinessService, AssetConstants.ASSET_BusinessService)) {
				for (Role role : roles) {
					if (StringUtils.equalsIgnoreCase(role.getCode(), AssetConstants.ASSET_WF_CREATOR)) {
						roleCodes.add(AssetConstants.ASSET_WF_CREATOR);
					}
					if (StringUtils.equalsIgnoreCase(role.getCode(), AssetConstants.ASSET_WF_APPROVER)) {
						roleCodes.add(AssetConstants.ASSET_WF_APPROVER);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Roles does not exists!!!");
		}

		/*
		 * List<String> roleCodes = roles.stream() .filter(role ->
		 * StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)) .map(role ->
		 * role.getCode()) .collect(Collectors.toList());
		 */
		/*
		 * if(!CollectionUtils.isEmpty(roles)) { for (Role role : roles) { if
		 * (StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)) {
		 * roleCodes.add(role.getCode()); } } }
		 */

		return roleCodes;
	}

	/*
	 * public AssetActionResponse getAllcounts() { AssetActionResponse response =
	 * new AssetActionResponse(); List<Map<String, Object>> statusList = null;
	 * statusList = assetRepository.getAllCounts();
	 * 
	 * if (!CollectionUtils.isEmpty(statusList)) { response.setCountsData(
	 * statusList.stream() .filter(Objects::nonNull) // Ensure no null entries
	 * .filter(status -> StringUtils.isNotEmpty(status.toString())) // Validate
	 * non-empty entries .collect(Collectors.toList())); // Collect the filtered
	 * list
	 * 
	 * if (statusList.get(0).containsKey("total_applications")) { Object
	 * totalApplicationsObj = statusList.get(0).get("total_applications"); if
	 * (totalApplicationsObj instanceof Number) { // Ensure the value is a number
	 * response.setApplicationTotalCount(((Number)
	 * totalApplicationsObj).longValue()); } else { throw new
	 * IllegalArgumentException("total_applications is not a valid number"); } } }
	 * return response; }
	 */

	public AssetActionResponse getAllcounts() {
		AssetActionResponse response = new AssetActionResponse();
		List<Map<String, Object>> statusList = null;
		statusList = assetRepository.getAllCounts();

		if (!CollectionUtils.isEmpty(statusList)) {
			response.setCountsData(statusList.stream().filter(Objects::nonNull) // Ensure no null entries
					.filter(status -> StringUtils.isNotEmpty(status.toString())) // Validate non-empty entries
					.collect(Collectors.toList())); // Collect the filtered list

			if (statusList.get(0).containsKey("total_applications")) {
				Object totalApplicationsObj = statusList.get(0).get("total_applications");
				if (totalApplicationsObj instanceof Number) { // Ensure the value is a number
					response.setApplicationTotalCount(((Number) totalApplicationsObj).longValue());
				} else {
					throw new IllegalArgumentException("total_applications is not a valid number");
				}
			}
		}
		return response;
	}

	public List<AssetUpdate> updateAsset(@Valid AssetUpdateRequest assetRequest) {
		Map<String, AssetUpdate> appNoToSiteBookingMap = null;
		try {
			validateAssetUpdateRequest(assetRequest);
			appNoToSiteBookingMap = searchAssetFromRequest(assetRequest);
			assetRequest = validateAndEnrichUpdateAsset(assetRequest, appNoToSiteBookingMap);
			if (!AssetConstants.BOOKING_BOOKED_STATUS.equals(assetRequest.getAssetUpdate().get(0).getBookingStatus())) {
				workflowService.updateWorkflowForAssetUpdate(assetRequest, CreationReason.UPDATE);
			}
			assetRepository.updateAsset(assetRequest);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return assetRequest.getAssetUpdate();
	}

	private @Valid AssetUpdateRequest validateAndEnrichUpdateAsset(@Valid AssetUpdateRequest assetRequest,
			Map<String, AssetUpdate> appNoToSiteBookingMap) {

		List<AssetUpdate> existingAsset = null;
		AssetUpdate updateTemp = null;
		AssetUpdateRequest tempRequest = AssetUpdateRequest.builder().requestInfo(assetRequest.getRequestInfo())
				.assetUpdate(new ArrayList<>()).build();
		assetRequest.getAssetUpdate().stream().forEach(booking -> {
			AssetUpdate assetUpdate = appNoToSiteBookingMap.get(booking.getApplicationNo());
			if (null == assetUpdate) {
				throw new CustomException("APPLICATION_NOT_FOUND",
						"Application id not found: " + booking.getApplicationNo());
			}
			if (!booking.getIsOnlyWorkflowCall()) {
				booking.setApplicationNo(assetUpdate.getApplicationNo());
				booking.setStatus(assetUpdate.getStatus());
				booking.setAuditDetails(assetUpdate.getAuditDetails());
				booking.getAuditDetails().setLastModifiedBy(assetUpdate.getAuditDetails().getLastModifiedBy());
				booking.getAuditDetails().setLastModifiedTime(assetUpdate.getAuditDetails().getLastModifiedTime());
				booking.setAssetStatus(assetUpdate.getStatus());
				tempRequest.getAssetUpdate().add(booking);
			} else {
				Boolean isWfCall = booking.getIsOnlyWorkflowCall();
				String tempApplicationNo = booking.getApplicationNo();
				String action = booking.getWorkflowAction();
				String status = AssetConstants.getStatusOrAction(action, true);
				String comments = booking.getComments();

				AssetUpdate assetTemp = objectMapper.convertValue(appNoToSiteBookingMap.get(booking.getApplicationNo()),
						AssetUpdate.class);
				if (null == assetTemp) {
					throw new CustomException("FAILED_SEARCH_ASSET_UPDATE",
							"Asset Application not found for workflow call.");
				}
				assetTemp.setIsOnlyWorkflowCall(isWfCall);
				assetTemp.setApplicationNo(tempApplicationNo);
				assetTemp.setWorkflowAction(action);
				assetTemp.setComments(comments);
				assetTemp.setStatus(status);
				assetTemp.setAssetStatus(status);
				tempRequest.getAssetUpdate().add(assetTemp);

			}
		});

		return tempRequest;
	}

	private Map<String, AssetUpdate> searchAssetFromRequest(@Valid AssetUpdateRequest assetRequest) {
		AssetSearchCriteria criteria = null;
		Map<String, AssetUpdate> bookingMap = new HashMap<>();
		List<AssetUpdate> assetUpdateDataFromDB = null;
		String applicationNo = assetRequest.getAssetUpdate().get(0).getApplicationNo();
		try {
			criteria = AssetSearchCriteria.builder()
					.applicationNo(assetRequest.getAssetUpdate().get(0).getApplicationNo()).build();
			assetUpdateDataFromDB = assetRepository.getAssetDataFromDB(criteria);
			bookingMap = assetUpdateDataFromDB.stream()
					.collect(Collectors.toMap(AssetUpdate::getApplicationNo, booking -> booking));

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return bookingMap;
	}

	private void validateAssetUpdateRequest(@Valid AssetUpdateRequest assetRequest) {
		try {
			AssetUpdateRequest activeRequest = AssetUpdateRequest.builder().requestInfo(assetRequest.getRequestInfo())
					.build();
			if (null == assetRequest.getAssetUpdate()) {
				throw new CustomException("EMPTY_REQUEST", "Provide Assets to update.");
			}
			if (BooleanUtils.isFalse(assetRequest.getAssetUpdate().get(0).getIsOnlyWorkflowCall())) {
				if (StringUtils.isEmpty(assetRequest.getAssetUpdate().get(0).getId())) {
					throw new CustomException("EMPTY_REQUEST", "Provide Asset ids to update.");
				}
			}
			if (BooleanUtils.isTrue(assetRequest.getAssetUpdate().get(0).getIsOnlyWorkflowCall())) {
				if (StringUtils.isEmpty(assetRequest.getAssetUpdate().get(0).getApplicationNo())) {
					throw new CustomException("EMPTY_REQUEST", "Provide Asset Application number to update.");
				}
			}
			if (null != assetRequest.getAssetUpdate()) {
				searchValidateAndUpdateAsset(assetRequest);
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private void searchValidateAndUpdateAsset(AssetUpdateRequest activeRequest) {
		List<AssetUpdate> updateDataFromDB = null;
		AssetSearchCriteria searchCritria = new AssetSearchCriteria();
		String applicationNo = activeRequest.getAssetUpdate().get(0).getApplicationNo();
		try {
			searchCritria = AssetSearchCriteria.builder().applicationNo(applicationNo).build();
			if (null != searchCritria) {
				updateDataFromDB = assetRepository.getAssetDataFromDB(searchCritria);
			}
			if (null == updateDataFromDB) {
				throw new CustomException("INVALID_APPLICATION_NUMBER",
						"Please provide the correct application number");
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

}
