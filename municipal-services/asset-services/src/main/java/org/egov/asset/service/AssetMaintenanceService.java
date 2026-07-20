package org.egov.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.AssetMaintenanceRepository;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.egov.asset.web.models.maintenance.AssetMaintenanceRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class AssetMaintenanceService {

    private final AssetMaintenanceRepository assetMaintenanceRepository;
    private final EnrichmentService enrichmentService;
    private final AssetUtil assetUtil;
    private final AssetService assetService;

    public AssetMaintenanceService(AssetMaintenanceRepository assetMaintenanceRepository,
                                   EnrichmentService enrichmentService, AssetUtil assetUtil,
                                   AssetService assetService) {
        this.assetMaintenanceRepository = assetMaintenanceRepository;
        this.enrichmentService = enrichmentService;
        this.assetUtil = assetUtil;
        this.assetService = assetService;
    }

    /**
     * Create a new AssetMaintenance record.
     *
     * @param request AssetMaintenanceRequest containing the maintenance details.
     * @return The created AssetMaintenance record.
     */
    public AssetMaintenance createMaintenance(AssetMaintenanceRequest request) {
        log.debug("Asset maintenance service method create called");

        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = requestInfo.getUserInfo().getTenantId().split("\\.")[0];

        AssetMaintenance maintenance = request.getAssetMaintenance();

        if (maintenance == null || maintenance.getAssetId() == null) {
            throw new IllegalArgumentException("Invalid maintenance data");
        }

        Asset asset = assetUtil.fetchAssetById(maintenance.getAssetId(), tenantId);
        assetUtil.updateAssetStatusAndUsage(asset, null, maintenance.getAssetMaintenanceStatus());

        enrichmentService.enrichMaintenanceCreateOperations(request);
        assetMaintenanceRepository.save(request);
        updateAssetInSystem(requestInfo, asset);

        return maintenance;
    }

    /**
     * Update an existing AssetMaintenance record.
     *
     * @param request AssetMaintenanceRequest containing updated maintenance details.
     * @return The updated AssetMaintenance record.
     */
    public AssetMaintenance updateMaintenance(@Valid AssetMaintenanceRequest request) {
        log.debug("Asset maintenance service method update called");

        AssetMaintenance maintenance = request.getAssetMaintenance();

        if (maintenance == null || maintenance.getMaintenanceId() == null) {
            throw new CustomException("UPDATE_ERROR", "AssetMaintenanceRequest Not found in the System: " + maintenance);
        }

        enrichmentService.enrichMaintenanceUpdateOperations(request);
        assetMaintenanceRepository.update(request);

        return maintenance;
    }

    /**
     * Search for AssetMaintenance records based on search criteria.
     *
     * @param searchCriteria Search criteria for filtering maintenance records.
     * @param requestInfo    RequestInfo object containing request metadata.
     * @return List of AssetMaintenance records matching the criteria.
     */
    public List<AssetMaintenance> searchMaintenances(AssetMaintenanceSearchCriteria searchCriteria,
                                                     RequestInfo requestInfo) {
        return assetMaintenanceRepository.search(searchCriteria);
    }

    /**
     * Update the asset in the system using AssetService.
     *
     * @param requestInfo The RequestInfo object.
     * @param asset       The Asset object to update.
     */
    private void updateAssetInSystem(RequestInfo requestInfo, Asset asset) {
        AssetRequest assetRequest = AssetRequest.builder()
                .requestInfo(requestInfo)
                .asset(asset)
                .build();
        assetService.updateAssetInSystem(assetRequest);
        log.info("Updated asset ID: {} with status: {} and usage: {}",
                asset.getId(), asset.getAssetStatus(), asset.getAssetUsage());
    }
}
