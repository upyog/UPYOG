package upyog.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upyog.service.EstateService;
import upyog.util.ResponseInfoFactory;
import upyog.service.RentScheduler;
import upyog.web.models.*;

import java.util.List;

@RestController
@RequestMapping("/estate")
@Slf4j
@RequiredArgsConstructor
public class EstateController {

    @Autowired
    private final EstateService estateService;

    @Autowired
    private final ResponseInfoFactory responseInfoFactory;

    @Autowired
    private final RentScheduler rentScheduler;

    /**
     * Creates a new allotment in the estate management system.
     * Processes allotment request and generates unique allotment ID.
     *
     * @param request AllotmentRequest containing allotment details and request metadata
     * @return ResponseEntity containing created allotment details with HTTP 201 status
     */
    @PostMapping("/allotment/v1/_create")
    public ResponseEntity<AllotmentResponse> createAllotment(
            @Valid @RequestBody AllotmentRequest request) {
        log.info("Creating allotment for request: {}", request);
        AllotmentResponse response = estateService.createAllotment(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        response.setResponseInfo(responseInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Searches for allotments based on provided search criteria.
     * Supports filtering by allotment ID, tenant ID, asset number, allottee details, etc.
     *
     * @param criteria AllotmentSearchCriteria containing search parameters and request metadata
     * @return ResponseEntity containing list of matching allotments with HTTP 200 status
     */
    @PostMapping("/allotment/v1/_search")
    public ResponseEntity<AllotmentResponse> searchAllotments(
            @Valid @RequestBody AllotmentSearchRequest request) {
        log.info("Searching allotments with criteria: {}", request.getCriteria());
        AllotmentResponse response = estateService.searchAllotments(request.getCriteria());
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        response.setResponseInfo(responseInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/asset/v1/_update")
    public ResponseEntity<AssetResponse> updateAsset(@Valid @RequestBody AssetRequest request){
        log.info("Received asset update request for tenant: {}",
                request.getAssets() != null && !request.getAssets().isEmpty()
                        ? request.getAssets().get(0).getTenantId() : "unknown");
        Asset asset = estateService.updateAsset(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(
                request.getRequestInfo(), true);
        AssetResponse assetResponse = AssetResponse.builder()
                .assets(List.of(asset))
                .responseInfo(responseInfo)
                .build();
        log.info("Asset updated successfully with estate number: {}", asset.getEstateNo());
        return ResponseEntity.ok(assetResponse);
    }




    /**
     * Creates a new asset in the estate management system.
     * Validates asset reference number against the asset service if provided.
     * Generates unique asset ID and estate number.
     *
     * @param request AssetRequest containing asset details and request metadata
     * @return ResponseEntity containing created asset details with HTTP 201 status
     */
    @PostMapping("/asset/v1/_create")
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest request) {
        log.info("Received asset creation request for tenant: {}", 
                request.getAssets() != null && !request.getAssets().isEmpty() 
                        ? request.getAssets().get(0).getTenantId() : "unknown");
        
        Asset asset = estateService.createAsset(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(
                request.getRequestInfo(), true);
        
        AssetResponse assetResponse = AssetResponse.builder()
                .assets(List.of(asset))
                .responseInfo(responseInfo)
                .build();
        
        log.info("Asset created successfully with estate number: {}", asset.getEstateNo());
        return ResponseEntity.status(HttpStatus.CREATED).body(assetResponse);
    }

    /**
     * Searches for assets based on provided search criteria.
     * Supports filtering by asset number, tenant ID, classification, category, etc.
     *
     * @param searchRequest AssetSearchRequest containing search criteria and request metadata
     * @return ResponseEntity containing list of matching assets with HTTP 200 status
     */
    @PostMapping("/asset/v1/_search")
    public ResponseEntity<AssetResponse> searchAssets(@Valid @RequestBody AssetSearchRequest searchRequest) {
        log.info("Received asset search request with criteria: {}", searchRequest.getCriteria());
        
        AssetResponse response = estateService.searchAssets(searchRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(
                searchRequest.getRequestInfo(), true);
        response.setResponseInfo(responseInfo);
        
        log.info("Asset search completed. Found {} asset(s)", 
                response.getAssets() != null ? response.getAssets().size() : 0);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/advancePayment")
//    public ResponseEntity<PaymentResponse>

    @PostMapping("/scheduler/v1/_trigger")
    public ResponseEntity<String> triggerScheduler(@RequestBody SchedulerRequest request) {
        log.info("Manual scheduler trigger requested for billingDate: {}", request.getBillingDate());
        String result = rentScheduler.triggerManually(request.getRequestInfo(), request.getBillingDate());
        return ResponseEntity.ok(result);
    }
}
