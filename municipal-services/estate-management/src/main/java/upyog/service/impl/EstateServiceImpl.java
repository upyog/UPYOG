package upyog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import upyog.service.*;
import upyog.config.EstateConfiguration;
import upyog.config.ServiceConstants;
import upyog.repository.EstateRepository;
import upyog.util.EstateUtil;
import upyog.web.models.*;
import upyog.web.models.billing.Demand;
import upyog.service.DemandService;
import upyog.util.MdmsUtil;

import java.util.List;

@Service
@Slf4j
public class EstateServiceImpl implements EstateService {

    @Autowired
    private EnrichmentService enrichmentService;
    @Autowired
    private EstateRepository estateRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EstateConfiguration estateConfiguration;
    @Autowired
    private AssetService assetService;
    @Autowired
    private DemandService demandService;
    /**
     * Creates a new asset in the estate management system.
     *
     * <p>This method performs the following operations:
     * <ol>
     *   <li>Validates that the request contains at least one asset</li>
     *   <li>If a reference asset number is provided, validates it against the external asset service</li>
     *   <li>Enriches the asset with system-generated IDs and audit information</li>
     *   <li>Publishes the asset creation event to Kafka for persistence</li>
     * </ol>
     * </p>
     *
     * @param request The AssetRequest containing asset details and request metadata
     * @return The created Asset object with generated IDs and enriched data
     * @throws CustomException with code INVALID_REQUEST if asset list is null or empty
     * @throws CustomException with code ASSET_NOT_FOUND if reference asset doesn't exist in asset service
     */
    @Override
    public Asset createAsset(AssetRequest request) {
        // Validate request
        if (request.getAssets() == null || request.getAssets().isEmpty()) {
            log.error("Asset creation failed: Request does not contain any assets");
            throw new CustomException(
                    ServiceConstants.EstateConstants.INVALID_REQUEST,
                    "Asset request must contain at least one asset"
            );
        }

        Asset asset = request.getAssets().get(0);
        log.info("Processing asset creation for tenant: {}, asset type: {}",
                asset.getTenantId(), asset.getAssetType());

        // Validate reference asset number if provided
        if (StringUtils.hasText(asset.getRefAssetNo())) {
            log.info("Validating reference asset number: {}", asset.getRefAssetNo());

            boolean assetExists = assetService.isAssetExist(
                    asset.getRefAssetNo(),
                    asset.getTenantId(),
                    request.getRequestInfo()
            );

            if (!assetExists) {
                log.error("Asset validation failed: Reference asset {} not found in asset service",
                        asset.getRefAssetNo());
                throw new CustomException(
                        "ASSET_NOT_FOUND",
                        "Asset with reference number " + asset.getRefAssetNo() +
                        " does not exist in Asset Service"
                );
            }
            log.info("Reference asset number validated successfully");
        }

        // Enrich asset with generated IDs and audit details
        enrichmentService.enrichAssetRequest(request);

        // Publish to Kafka for async persistence
        estateRepository.save(estateConfiguration.getEstateAssetSaveTopic(), request);

        log.info("Asset created successfully. Estate No: {}, Asset ID: {}",
                asset.getEstateNo(), asset.getAssetId());
        return asset;
    }






    /**
     * Creates a new allotment for an asset in the estate management system.
     *
     * <p>This method performs the following operations:
     * <ol>
     *   <li>Validates that the request contains at least one allotment</li>
     *   <li>Checks if a user UUID is provided in the allotment</li>
     *   <li>If user UUID is not provided, searches for user by mobile number or creates a new user</li>
     *   <li>Sets the user UUID in the audit details for tracking</li>
     *   <li>Enriches the allotment with system-generated IDs and audit information</li>
     *   <li>Publishes the allotment creation event to Kafka for persistence</li>
     * </ol>
     * </p>
     *
     * @param request The AllotmentRequest containing allotment details and request metadata
     * @return AllotmentResponse containing the created allotment
     * @throws CustomException with code INVALID_REQUEST if allotment list is null or empty
     */
    @Override
    public AllotmentResponse createAllotment(AllotmentRequest request) {
        if (request.getAllotments() == null || request.getAllotments().isEmpty()) {
            log.error("Allotment creation failed: Request does not contain any allotments");
            throw new CustomException(
                    ServiceConstants.EstateConstants.INVALID_REQUEST,
                    "Allotment request must contain at least one allotment"
            );
        }

        Allotment allotment = request.getAllotments().get(0);
        log.info("Processing allotment for asset: {}, allottee: {}",
                allotment.getAssetNo(), allotment.getAlloteeName());

        // If user UUID not provided, search or create user
        if (!StringUtils.hasText(allotment.getUserUuid())) {
            log.info("User UUID not provided, checking if user exists with mobile: {}",
                    allotment.getMobileNo());
            org.egov.common.contract.user.UserDetailResponse userResponse =
                    userService.createOrGetUser(request.getRequestInfo(), allotment);

            if (userResponse != null && userResponse.getUser() != null && !userResponse.getUser().isEmpty()) {
                allotment.setUserUuid(userResponse.getUser().get(0).getUuid());
                log.info("User UUID set: {}", allotment.getUserUuid());
            }
        }

        enrichmentService.enrichAllotmentRequest(request);
        
        // Create demand for the allotment
        demandService.createDemand(request, true);
        
        estateRepository.save(estateConfiguration.getEstateAllotmentSaveTopic(), request);

        log.info("Allotment created successfully. ID: {}", allotment.getAllotmentId());

        AllotmentResponse response = new AllotmentResponse();
        response.setAllotments(request.getAllotments());
        return response;
    }

    /**
     * Searches for allotments based on the provided criteria
     *
     * @param criteria The search criteria for allotments
     * @return AllotmentResponse containing the list of allotments
     */
    @Override
    public AllotmentResponse searchAllotments(AllotmentSearchCriteria criteria) {
        log.info("Searching allotments with criteria: {}", criteria);

        List<Allotment> allotments = estateRepository.searchAllotments(criteria);

        AllotmentResponse response = new AllotmentResponse();
        response.setAllotments(allotments);

        log.info("Found {} allotments", allotments.size());
        return response;
    }

    /**
     * Searches for assets based on the provided search criteria.
     *
     * <p>This method supports searching assets by various parameters including:
     * <ul>
     *   <li>Asset ID or Estate Number</li>
     *   <li>Tenant ID</li>
     *   <li>Asset classification, category, and subcategory</li>
     *   <li>Asset type and status</li>
     *   <li>Department</li>
     * </ul>
     * </p>
     *
     * @param searchRequest The AssetSearchRequest containing search criteria and request metadata
     * @return AssetResponse containing the list of matching assets (may be empty if no matches found)
     */
    @Override
    public AssetResponse searchAssets(AssetSearchRequest searchRequest) {
        AssetSearchCriteria criteria = searchRequest.getCriteria();
        log.info("Searching assets - TenantId: {}, EstateNo: {}, RefAssetNo: {}, BuildingName: {}, Locality: {}, Status: {}",
                criteria.getTenantId(),
                criteria.getEstateNo(),
                criteria.getRefAssetNo(),
                criteria.getBuildingName(),
                criteria.getLocality(),
                criteria.getAssetStatus());

        // Fetch assets from repository
        List<Asset> assets = estateRepository.searchAssets(criteria);

        // Build response
        AssetResponse response = AssetResponse.builder()
                .assets(assets)
                .build();

        log.info("Asset search completed successfully. Total assets found: {}",
                assets != null ? assets.size() : 0);
        return response;
    }

    /**
     * @param request
     * @return Updated asset
     */
    @Override
    public Asset updateAsset(AssetRequest request) {

        if(request.getAssets() == null || request.getAssets().isEmpty()){
            log.error("Asset update failed: Request does not contain any assets");
            throw new CustomException(
                    ServiceConstants.EstateConstants.INVALID_REQUEST,
                    "Asset request must contain at least one asset"
            );
        }
        Asset asset = request.getAssets().get(0);

        //Validate asset ID is provided for update
        if(!StringUtils.hasText(asset.getEstateNo())){
            log.error("Asset update failed: Asset ID is required for update");
            throw new CustomException(
                    "Estate_NO_REQUIRED",
                    "Estate Number is required for update operation"
            );
        }
        log.info("Processing asset update for Estate No: {}, Tenant:{}", asset.getEstateNo(), asset.getTenantId() );

        //check if Asset exits
        AssetSearchCriteria searchCriteria = new AssetSearchCriteria();
        // searchCriteria.setEstateNo(asset.getAssetId());
        if(StringUtils.hasText(asset.getEstateNo())){
            searchCriteria.setEstateNo(asset.getEstateNo());
        }
        searchCriteria.setTenantId(asset.getTenantId());

        List<Asset> existingAssets = estateRepository.searchAssets(searchCriteria);
        if(existingAssets == null || existingAssets.isEmpty()){
            log.error("Asset update failed: Asset with ID {} not found", asset.getAssetId());
            throw new CustomException(
                    "ASSET_NOT_FOUND",
                    "Asset with Estate Number " + asset.getEstateNo() + " not found"
            );
        }

        Asset existingAsset = existingAssets.get(0);

        asset.setAssetId(existingAsset.getAssetId());
        asset.setEstateNo(existingAsset.getEstateNo());
        //set audit details for update
        String userUuid = request.getRequestInfo().getUserInfo().getUuid();
        asset.setAuditDetails(EstateUtil.getAuditDetails(userUuid, false));

        // publish to kafka for async persistence
        estateRepository.save(estateConfiguration.getEstateAssetUpdateTopic(), request);

        log.info("Asset updated successfully. Asset ID:{}, Estate No. {}", asset.getAssetId(), asset.getEstateNo());


        return asset;
    }
}

