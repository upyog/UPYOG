package org.egov.asset.web.controllers;

import digit.models.coremodels.RequestInfoWrapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.service.AssetMaintenanceService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.disposal.AssetDisposalResponse;
import org.egov.asset.web.models.disposal.AssetDisposalSearchCriteria;
import org.egov.asset.web.models.maintenance.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/maintenance/v1")
public class AssetMaintenanceController {

    @Autowired
    private AssetMaintenanceService assetMaintenanceService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    /**
     * Endpoint to create an asset maintenance record.
     *
     * @param request The request object containing asset maintenance details.
     * @return Success message on pushing the create request to Kafka.
     */
    @PostMapping("/_create")
    public ResponseEntity<AssetMaintenanceResponse> createAssetMaintenance(@Valid @RequestBody AssetMaintenanceRequest request) {
        log.info("Received request to create Asset Maintenance: {}", request);
        AssetMaintenance assetMaintenance = assetMaintenanceService.createMaintenance(request);

        List<AssetMaintenance> assetMaintenanceList = new ArrayList<>(Collections.singletonList(assetMaintenance));
        AssetMaintenanceResponse response = AssetMaintenanceResponse.builder()
                .assetMaintenance(assetMaintenanceList)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an asset maintenance record.
     *
     * @param request The request object containing updated asset maintenance details.
     * @return Success message on pushing the update request to Kafka.
     */
    @PostMapping("/_update")
    public ResponseEntity<AssetMaintenanceResponse> updateAssetMaintenance(@Valid @RequestBody AssetMaintenanceRequest request) {
        log.info("Received request to update Asset Maintenance: {}", request);

        AssetMaintenance assetMaintenance = assetMaintenanceService.updateMaintenance(request);

        List<AssetMaintenance> assetMaintenanceList = new ArrayList<>(Collections.singletonList(assetMaintenance));
        AssetMaintenanceResponse response = AssetMaintenanceResponse.builder()
                .assetMaintenance(assetMaintenanceList)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to search asset maintenance records based on criteria.
     *
     * @param searchRequest The request info object containing metadata.
     * @return List of AssetMaintenance records matching the criteria.
     */
    @PostMapping("/_search")
    public ResponseEntity<AssetMaintenanceResponse> searchDisposals(
            @RequestBody AssetMaintenanceSearchRequest searchRequest ) {

        // Extract the search criteria and request info
        AssetMaintenanceSearchCriteria searchCriteria = searchRequest.getAssetMaintenanceSearchCriteria();
        RequestInfoWrapper requestInfoWrapper = searchRequest.getRequestInfoWrapper();

        List<AssetMaintenance> results = assetMaintenanceService.searchMaintenances(searchCriteria, requestInfoWrapper.getRequestInfo());
        AssetMaintenanceResponse response = AssetMaintenanceResponse.builder()
                .assetMaintenance(results)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
