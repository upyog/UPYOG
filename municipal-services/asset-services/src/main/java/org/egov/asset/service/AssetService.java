package org.egov.asset.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.dto.AssetAssignmentDTO;
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
import org.egov.asset.web.models.CreationReason;
import org.egov.asset.web.models.RequestInfoWrapper;
import org.egov.asset.web.models.UserSearchCriteria;
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
		assetValidator.validateSearch(requestInfo, criteria);
		List<String> roles = new ArrayList<>();
		for (Role role : requestInfo.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		// if ((criteria.tenantIdOnly() || criteria.isEmpty()) &&
		// roles.contains(AssetConstants.ASSET_INITIATOR)) {
		if ((/* criteria.tenantIdOnly() || */ criteria.isEmpty())) {
			log.debug("loading data of created and by me");
			assets = this.getAssetCreatedForByMe(criteria, requestInfo);
			log.debug("no of assets retuning by the search query" + assets.size());
		} else if( roles.contains(AssetConstants.EMPLOYEE)) {
			
				criteria.setCreatedBy(null);
				assets = getAssetsFromCriteria(criteria);
			}
			

		if (criteria.getApplicationNo() != null) {
			return assets.stream().map(asset -> modelMapper.map(asset, AssetDTO.class)).collect(Collectors.toList());
		} else {
			return assets.stream().map(this::convertToAssetSearchDTO).collect(Collectors.toList());
		}
	}

	private AssetSearchDTO convertToAssetSearchDTO(Asset asset) {
		AssetSearchDTO assetSearchDTO = modelMapper.map(asset, AssetSearchDTO.class);
		if (asset.getAssetAssignment() != null) {
			assetSearchDTO.setAssetAssignment(modelMapper.map(asset.getAssetAssignment(), AssetAssignmentDTO.class));
		}
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
		assetRepository.updateAssignment(assetRequest);
		return assetRequest.getAsset();
	}

	public AssetActionResponse getActionsOnApplication(AssetActionRequest assetActionRequest) {
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		AssetActionResponse assetActionResponse =null;
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
						assetActionRequest.getRequestInfo().getUserInfo().getRoles());

				StringBuilder uri = new StringBuilder(assetConfiguration.getWfHost());
				uri.append(assetConfiguration.getWfBusinessServiceSearchPath());
				uri.append("?tenantId=").append(applicationTenantId);
				uri.append("&businessServices=").append(applicationBusinessId);
				RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
						.requestInfo(assetActionRequest.getRequestInfo()).build();
				LinkedHashMap<String, Object> responseObject = (LinkedHashMap<String, Object>) restCallRepository
						.fetchResult(uri, requestInfoWrapper);

				BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(responseObject,
						BusinessServiceResponse.class);
				if (null == businessServiceResponse
						|| CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
					throw new CustomException("NO_BUSINESS_SERVICE_FOUND",
							"Business service not found for application number: " + applicationNumber);
				}
				List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
						.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), applicationStatus))
						.collect(Collectors.toList());

				// filtering actions based on roles
				List<String> actions = new ArrayList<>();
				stateList.stream().forEach(state -> {
					state.getActions().stream().filter(
							action -> action.getRoles().stream().anyMatch(role -> rolesWithinTenant.contains(role)))
							.forEach(action -> {
								actions.add(action.getAction());
							});
				});

				applicationActionMaps.put(applicationNumber, actions);

			});

			List<AssetApplicationDetail> nextActionList = new ArrayList<>();
			applicationActionMaps.entrySet().stream().forEach(entry -> {
				nextActionList.add(
						AssetApplicationDetail.builder().applicationNumber(entry.getKey()).action(entry.getValue()).build());
			});
			
			 assetActionResponse = AssetActionResponse.builder().applicationDetails(nextActionList).build();
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return assetActionResponse;
	}

	public AssetActionResponse getCountOfAllApplicationTypes(AssetActionRequest actionRequest) {
		AssetActionResponse assetActionResponse =null;
		List<String> statusList = null;
		try {
			assetActionResponse = AssetActionResponse.builder().build();
			if(null!=actionRequest) {
				statusList = assetRepository.getTypesOfAllApplications(actionRequest.getIsHistoryCall(),actionRequest.getTenantId());
			}
			if(!CollectionUtils.isEmpty(statusList)) {
				assetActionResponse.setApplicationTypesCount(statusList.stream().filter(status -> StringUtils.isNotEmpty(status))
						.collect(Collectors.groupingBy(String::toString,Collectors.counting())));
			}
			
		} catch (Exception e) {
			throw new CustomException("FAILED_TO_FETCH", "Failed to fetch Application types.");
		}
		return assetActionResponse;
	}

	List<String> getRolesWithinTenant(String tenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}
	
	public AssetActionResponse getAllcounts() {
		AssetActionResponse response = new AssetActionResponse();
        List<Map<String, Object>> statusList = null;
        statusList = assetRepository.getAllCounts();
        
        if (!CollectionUtils.isEmpty(statusList)) {
        	response.setCountsData(
		                statusList.stream()
		                        .filter(Objects::nonNull) // Ensure no null entries
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
	

}
