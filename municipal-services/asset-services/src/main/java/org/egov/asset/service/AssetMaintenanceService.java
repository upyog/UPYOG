package org.egov.asset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.AssetMaintenanceRepository;
import org.egov.asset.repository.querybuilder.AssetMaintenanceQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetMaintenanceRowMapper;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.util.AssetValidator;
import org.egov.asset.util.MdmsUtil;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.egov.asset.web.models.maintenance.AssetMaintenanceRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.egov.asset.web.models.Asset;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AssetMaintenanceService {

    @Autowired
    private AssetMaintenanceRepository assetMaintenanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MdmsUtil mdmsUtil;

    @Autowired
    private AssetValidator assetValidator;

    @Autowired
    private EnrichmentService enrichmentService;


    @Autowired
    private AssetUtil assetUtil;

    @Autowired
    private AssetService assetService;

    /**
     * Create a new AssetMaintenance record.
     *
     * @param request AssetMaintenanceRequest containing the maintenance details.
     * @return The created AssetMaintenance record.
     */
    public AssetMaintenance createMaintenance(AssetMaintenanceRequest request) {
        log.debug("Asset maintenance service method create called");

        // Validate and enrich the request
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = requestInfo.getUserInfo().getTenantId().split("\\.")[0];
        Object mdmsData = mdmsUtil.mDMSCall(requestInfo, tenantId);

        AssetMaintenance maintenance = request.getAssetMaintenance();

        // Validate request data
        if (maintenance == null || maintenance.getAssetId() == null) {
            throw new IllegalArgumentException("Invalid maintenance data");
        }

        // Fetch the asset details
        Asset asset = assetUtil.fetchAssetById(maintenance.getAssetId(), tenantId);

        // Update the asset status if applicable
        assetUtil.updateAssetStatusAndUsage(asset, null, maintenance.getAssetMaintenanceStatus());

        // Enrich the maintenance request
        enrichmentService.enrichMaintenanceCreateOperations(request);

        // Push the request to Kafka
        assetMaintenanceRepository.save(request);

        // Update the asset in the system
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

        // Validate and enrich the request
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = requestInfo.getUserInfo().getTenantId().split("\\.")[0];
        Object mdmsData = mdmsUtil.mDMSCall(requestInfo, tenantId);

        AssetMaintenance maintenance = request.getAssetMaintenance();

        // Validate existence of the record
        if (maintenance == null || maintenance.getMaintenanceId() == null) {
            throw new CustomException("UPDATE_ERROR", "AssetMaintenanceRequest Not found in the System: " + maintenance);
        }

        // Enrich the maintenance request
        enrichmentService.enrichMaintenanceUpdateOperations(request);

        // Push the update request to Kafka
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
    public List<AssetMaintenance> searchMaintenances(AssetMaintenanceSearchCriteria searchCriteria, RequestInfo requestInfo) {

            return  assetMaintenanceRepository.search(searchCriteria);
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
