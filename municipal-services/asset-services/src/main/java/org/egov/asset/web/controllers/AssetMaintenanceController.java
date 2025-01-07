package org.egov.asset.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.service.AssetMaintenanceService;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.egov.asset.web.models.maintenance.AssetMaintenanceRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/asset-maintenance")
public class AssetMaintenanceController {

    @Autowired
    private AssetMaintenanceService maintenanceService;

    /**
     * Endpoint to create an asset maintenance record.
     *
     * @param request The request object containing asset maintenance details.
     * @return Success message on pushing the create request to Kafka.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createAssetMaintenance(@Valid @RequestBody AssetMaintenanceRequest request) {
        log.info("Received request to create Asset Maintenance: {}", request);
        maintenanceService.createMaintenance(request);
        return ResponseEntity.ok("Asset Maintenance creation request pushed to Kafka.");
    }

    /**
     * Endpoint to update an asset maintenance record.
     *
     * @param request The request object containing updated asset maintenance details.
     * @return Success message on pushing the update request to Kafka.
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateAssetMaintenance(@Valid @RequestBody AssetMaintenanceRequest request) {
        log.info("Received request to update Asset Maintenance: {}", request);
        maintenanceService.updateMaintenance(request);
        return ResponseEntity.ok("Asset Maintenance update request pushed to Kafka.");
    }

    /**
     * Endpoint to search asset maintenance records based on criteria.
     *
     * @param criteria   The search criteria object.
     * @param requestInfo The request info object containing metadata.
     * @return List of AssetMaintenance records matching the criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<List<AssetMaintenance>> searchAssetMaintenance(
            @RequestBody AssetMaintenanceSearchCriteria criteria,
            @RequestHeader("RequestInfo") RequestInfo requestInfo) {
        log.info("Received request to search Asset Maintenance with criteria: {}", criteria);
        List<AssetMaintenance> results = maintenanceService.searchMaintenances(criteria, requestInfo);
        return ResponseEntity.ok(results);
    }
}
