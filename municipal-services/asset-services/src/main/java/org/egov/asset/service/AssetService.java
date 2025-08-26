package org.egov.asset.service;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.egov.asset.dto.AssetAssignmentDTO;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.dto.AssetSearchDTO;
import org.egov.asset.repository.AssetRepository;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.util.AssetValidator;
import org.egov.asset.util.MdmsUtil;
import org.egov.asset.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    WorkflowService workflowService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Validates and creates a new Asset record in the system.
     *
     * @param assetRequest The request containing asset details to be created.
     * @return The created Asset object.
     */
    public Asset create(AssetRequest assetRequest) {
        log.debug("Asset create service method called");
        RequestInfo requestInfo = assetRequest.getRequestInfo();
        String tenantId = assetRequest.getAsset().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);

        // Application cannot be created at the state level
        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        // Approval number should not be set during asset creation
        if (!StringUtils.isEmpty(assetRequest.getAsset().getApprovalNo())) {
            assetRequest.getAsset().setApprovalNo(null);
        }

        // Validate the asset creation request
        assetValidator.validateCreate(assetRequest, mdmsData);

        // Enrich the asset creation request with necessary details
        enrichmentService.enrichAssetCreateRequest(assetRequest, mdmsData);

        // Update the workflow for asset creation
        workflowService.updateWorkflow(assetRequest, CreationReason.CREATE);

        // Save the asset data to the repository
        assetRepository.save(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Searches for assets based on the given criteria and request information.
     *
     * @param criteria    The search criteria.
     * @param requestInfo The request information containing user details.
     * @return A list of AssetDTO objects matching the search criteria.
     */
    public List<AssetDTO> search(AssetSearchCriteria criteria, RequestInfo requestInfo) {
        List<Asset> assets = new LinkedList<>();

        // Validate the search request
        assetValidator.validateSearch(requestInfo, criteria);

        List<String> roles = new ArrayList<>();
        if (requestInfo.getUserInfo() != null) {
            for (Role role : requestInfo.getUserInfo().getRoles()) {
                roles.add(role.getCode());
            }
        }

        // If tenant ID is the only criteria or no criteria is provided
        if ((criteria.tenantIdOnly() || criteria.isEmpty())) {
            log.debug("Loading data of assets created by the current user");
            assets = this.getAssetCreatedForByMe(criteria, requestInfo);
            log.debug("Number of assets returned by the search query: " + assets.size());
        } else {
            assets = getAssetsFromCriteria(criteria);
        }

        // Convert the assets to the appropriate DTOs
        if (criteria.getApplicationNo() != null) {
            return assets.stream()
                    .map(asset -> modelMapper.map(asset, AssetDTO.class))
                    .collect(Collectors.toList());
        } else {
            return assets.stream()
                    .map(this::convertToAssetSearchDTO)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Converts an Asset entity to an AssetSearchDTO object.
     *
     * @param asset The Asset entity to be converted.
     * @return The AssetSearchDTO object.
     */
    private AssetSearchDTO convertToAssetSearchDTO(Asset asset) {
        AssetSearchDTO assetSearchDTO = modelMapper.map(asset, AssetSearchDTO.class);
        if (asset.getAssetAssignment() != null) {
            assetSearchDTO.setAssetAssignment(modelMapper.map(asset.getAssetAssignment(), AssetAssignmentDTO.class));
        }
        return assetSearchDTO;
    }

    /**
     * Retrieves assets created by the current user.
     *
     * @param criteria    The search criteria.
     * @param requestInfo The request information containing user details.
     * @return A list of assets created by the current user.
     */
    private List<Asset> getAssetCreatedForByMe(AssetSearchCriteria criteria, RequestInfo requestInfo) {
        List<Asset> assets;
        UserSearchCriteria userSearchRequest = new UserSearchCriteria();
        if (criteria.getTenantId() != null) {
            userSearchRequest.setTenantId(criteria.getTenantId());
        }
        List<String> uuids = new ArrayList<>();
        if (requestInfo.getUserInfo() != null && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
            uuids.add(requestInfo.getUserInfo().getUuid());
            criteria.setCreatedBy(uuids);
        }
        log.debug("Loading data of assets created by user: " + uuids);
        assets = getAssetsFromCriteria(criteria);
        return assets;
    }

    /**
     * Retrieves assets based on the given search criteria.
     *
     * @param criteria The search criteria.
     * @return A list of assets matching the criteria.
     */
    public List<Asset> getAssetsFromCriteria(AssetSearchCriteria criteria) {
        List<Asset> assets = assetRepository.getAssetData(criteria);
        if (assets.isEmpty()) {
            return Collections.emptyList();
        }
        return assets;
    }

    /**
     * Validates and updates an existing Asset record in the system.
     *
     * @param assetRequest The request containing asset details to be updated.
     * @return The updated Asset object.
     */
    public Asset update(@Valid AssetRequest assetRequest) {
        log.debug("Asset update service method called");
        RequestInfo requestInfo = assetRequest.getRequestInfo();
        String tenantId = assetRequest.getAsset().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);
        Asset asset = assetRequest.getAsset();

        // Check if the asset exists
        if (asset.getId() == null) {
            throw new CustomException(AssetErrorConstants.UPDATE_ERROR, "Asset Not found in the System: " + asset);
        }

        // Enrich the asset update request with necessary details
        enrichmentService.enrichAssetUpdateRequest(assetRequest, mdmsData);

        // Update the workflow for asset update
        workflowService.updateWorkflow(assetRequest, CreationReason.UPDATE);

        // Update the asset data in the repository
        assetRepository.update(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Validates and updates an existing Asset record in the system.
     *
     * @param assetRequest The request containing asset details to be updated.
     * @return The updated Asset object.
     */
    public Asset updateAssetInSystem(@Valid AssetRequest assetRequest) {
        log.debug("Asset update service method called");
        RequestInfo requestInfo = assetRequest.getRequestInfo();
        String tenantId = assetRequest.getAsset().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);
        Asset asset = assetRequest.getAsset();

        // Check if the asset exists
        if (asset.getId() == null) {
            throw new CustomException(AssetErrorConstants.UPDATE_ERROR, "Asset Not found in the System: " + asset);
        }

        // Enrich the asset update request with necessary details
        enrichmentService.enrichAssetUpdateRequest(assetRequest, mdmsData);

        // Update the asset data in the repository
        assetRepository.updateAssetInSystem(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Assigns an asset to a new owner or location.
     *
     * @param assetRequest The request containing asset assignment details.
     * @return The Asset object after assignment.
     */
    public Asset assignment(@Valid AssetRequest assetRequest) {
        log.debug("Asset assignment service method called");

        // Application cannot be created at the state level
        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        // Enrich the asset assignment request
        enrichmentService.enrichAssetOtherOperationsCreateRequest(assetRequest);

        // Save the asset assignment data to the repository
        assetRepository.saveAssignment(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Updates an existing asset assignment.
     * This method validates the tenant ID to ensure that the application is not created at the state level.
     * @param assetRequest The request containing asset assignment details to be updated.
     * @return The updated Asset object.
     */
    public Asset updateAssignment(@Valid AssetRequest assetRequest) {
        log.debug("Asset assignment service method called");

        // Validate that the application is not created at the state level
        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        // Enrich the asset assignment update request
        enrichmentService.enrichAssetOtherOperationsUpdateRequest(assetRequest);

        // Update the asset assignment in the repository
        assetRepository.updateAssignment(assetRequest);

        return assetRequest.getAsset();
    }

    public List<AssetAssignment> getAssetAssignmentDetails(String tenantId, String assetId) {
        return assetRepository.getAssetAssignmentDetails(tenantId, assetId);
    }
}
