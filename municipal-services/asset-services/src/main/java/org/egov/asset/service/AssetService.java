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
import org.egov.tracer.model.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class AssetService {

    private final MdmsUtil util;
    private final AssetRepository assetRepository;
    private final AssetValidator assetValidator;
    private final EnrichmentService enrichmentService;
    private final WorkflowService workflowService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public AssetService(MdmsUtil util, AssetRepository assetRepository, AssetValidator assetValidator,
                        EnrichmentService enrichmentService, WorkflowService workflowService,
                        ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.util = util;
        this.assetRepository = assetRepository;
        this.assetValidator = assetValidator;
        this.enrichmentService = enrichmentService;
        this.workflowService = workflowService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

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

        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        if (!StringUtils.isEmpty(assetRequest.getAsset().getApprovalNo())) {
            assetRequest.getAsset().setApprovalNo(null);
        }

        assetValidator.validateCreate(mdmsData);
        enrichmentService.enrichAssetCreateRequest(assetRequest, mdmsData);
        workflowService.updateWorkflow(assetRequest);
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
        assetValidator.validateSearch(requestInfo, criteria);

        List<Asset> assets;
        if (Boolean.TRUE.equals(criteria.getIsInterServiceCall())) {
            log.debug("Inter-service call detected, fetching assets without user-based filtering");
            assets = getAssetsFromCriteria(criteria);
            log.debug("Number of assets returned by the search query: " + assets.size());
        } else if (criteria.tenantIdOnly() || criteria.isEmpty()) {
            log.debug("Loading data of assets created by the current user");
            assets = getAssetCreatedForByMe(criteria, requestInfo);
            log.debug("Number of assets returned by the search query: " + assets.size());
        } else {
            assets = getAssetsFromCriteria(criteria);
        }

        if (criteria.getApplicationNo() != null) {
            return assets.stream()
                    .<AssetDTO>map(asset -> modelMapper.map(asset, AssetDTO.class))
                    .toList();
        }
        return assets.stream()
                .<AssetDTO>map(this::convertToAssetSearchDTO)
                .toList();
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
            assetSearchDTO.setAssetAssignment(
                    modelMapper.map(asset.getAssetAssignment(), AssetAssignmentDTO.class));
        }

        if (asset.getAdditionalDetails() != null) {
            assetSearchDTO.setAdditionalDetails(objectMapper.valueToTree(asset.getAdditionalDetails()));
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
        if (criteria.getTenantId() != null) {
            UserSearchCriteria userSearchRequest = new UserSearchCriteria();
            userSearchRequest.setTenantId(criteria.getTenantId());
        }
        List<String> uuids = new ArrayList<>();
        if (requestInfo.getUserInfo() != null && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
            uuids.add(requestInfo.getUserInfo().getUuid());
            criteria.setCreatedBy(uuids);
        }
        log.debug("Loading data of assets created by user: " + uuids);
        return getAssetsFromCriteria(criteria);
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

        if (asset.getId() == null) {
            throw new CustomException(AssetErrorConstants.UPDATE_ERROR, "Asset Not found in the System: " + asset);
        }

        enrichmentService.enrichAssetUpdateRequest(assetRequest, mdmsData);
        workflowService.updateWorkflow(assetRequest);
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

        if (asset.getId() == null) {
            throw new CustomException(AssetErrorConstants.UPDATE_ERROR, "Asset Not found in the System: " + asset);
        }

        enrichmentService.enrichAssetUpdateRequest(assetRequest, mdmsData);
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

        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        enrichmentService.enrichAssetOtherOperationsCreateRequest(assetRequest);
        assetRepository.saveAssignment(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Updates an existing asset assignment.
     *
     * @param assetRequest The request containing asset assignment details to be updated.
     * @return The updated Asset object.
     */
    public Asset updateAssignment(@Valid AssetRequest assetRequest) {
        log.debug("Asset assignment service method called");

        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        enrichmentService.enrichAssetOtherOperationsUpdateRequest(assetRequest);
        assetRepository.updateAssignment(assetRequest);

        return assetRequest.getAsset();
    }

    public List<AssetAssignment> getAssetAssignmentDetails(String assetId) {
        return assetRepository.getAssetAssignmentDetails(assetId);
    }
}
