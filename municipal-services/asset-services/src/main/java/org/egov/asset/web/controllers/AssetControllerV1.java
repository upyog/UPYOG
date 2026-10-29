package org.egov.asset.web.controllers;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.service.AssetCalculationClient;
import org.egov.asset.service.AssetService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.*;
import org.egov.asset.web.models.calcontract.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
@Slf4j
@RestController
@RequestMapping("/v1/assets")
public class AssetControllerV1 {

    private final ResponseInfoFactory responseInfoFactory;
    private final AssetService assetService;
    private final AssetCalculationClient assetCalculationClient;

    public AssetControllerV1(ResponseInfoFactory responseInfoFactory, AssetService assetService,
                             AssetCalculationClient assetCalculationClient) {
        this.responseInfoFactory = responseInfoFactory;
        this.assetService = assetService;
        this.assetCalculationClient = assetCalculationClient;
    }

    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetsCreatePost(
            @Parameter(description = "Details for the new asset(s) + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.create(assetRequest);
        List<AssetDTO> assets = new ArrayList<>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetsSearchPost(
            @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute AssetSearchCriteria searchCriteria) {
        List<AssetDTO> assets = assetService.search(searchCriteria, requestInfoWrapper.getRequestInfo());
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_update", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetsUpdatePost(
            @Parameter(description = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.update(assetRequest);
        List<AssetDTO> assets = new ArrayList<>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "assignment/_create", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetAssginCreatePost(
            @Parameter(description = "Details for the new asset(s) + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.assignment(assetRequest);
        List<AssetDTO> assets = new ArrayList<>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "assignment/_update", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetsAssignmentUpdatePost(
            @Parameter(description = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.updateAssignment(assetRequest);
        List<AssetDTO> assets = new ArrayList<>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "assignment/_search", method = RequestMethod.POST)
    public ResponseEntity<AssignmentRes> v1AssetsAssignmentSearch(
            @Parameter(description = "Details for existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        List<AssetAssignment> assetAssignmentDetails = assetService.getAssetAssignmentDetails(
                assetRequest.getAsset().getId());
        AssignmentRes response = AssignmentRes.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(null, true))
                .assetAssignments(assetAssignmentDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "depreciation/_process", method = RequestMethod.POST)
    public ResponseEntity<CalculationRes> triggerDepreciationCalculation(
            @Parameter(description = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        CalculationRes apiresponse = assetCalculationClient.triggerDepreciationCalculation(assetRequest);
        log.info("Depreciaiton api response : {}", apiresponse.getMessage());
        return new ResponseEntity<>(apiresponse, HttpStatus.OK);
    }

    @RequestMapping(value = "depreciation/list", method = RequestMethod.POST)
    public ResponseEntity<DepreciationRes> getAssetDepreciationList(
            @Parameter(description = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        DepreciationRes apiresponse = assetCalculationClient.getAssetDepreciationList(assetRequest.getAsset().getId());
        List<DepreciationDetail> clonedDetails = new ArrayList<>(apiresponse.getDepreciation());
        DepreciationRes response = DepreciationRes.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(null, true))
                .depreciation(clonedDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
