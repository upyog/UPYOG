package org.egov.asset.web.controllers;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.service.AssetCalculationClient;
import org.egov.asset.service.AssetDisposeService;
import org.egov.asset.service.AssetService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.*;
import org.egov.asset.web.models.calcontract.*;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.disposal.AssetDisposalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Slf4j
@RestController
@RequestMapping("/v1/assets")
public class AssetControllerV1 {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    AssetService assetService;

    @Autowired
    AssetCalculationClient assetCalculationClient;


    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetsCreatePost(
            @ApiParam(value = "Details for the new asset(s) + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        //String accept = request.getHeader("Accept");
        //if (accept != null && accept.contains("application/json")) {
        Asset asset = assetService.create(assetRequest);
        List<AssetDTO> assets = new ArrayList<AssetDTO>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

        //return new ResponseEntity<AssetResponse>(HttpStatus.NOT_IMPLEMENTED);
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
            @ApiParam(value = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.update(assetRequest);
        List<AssetDTO> assets = new ArrayList<AssetDTO>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "assignment/_create", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetAssginCreatePost(
            @ApiParam(value = "Details for the new asset(s) + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.assignment(assetRequest);
        List<AssetDTO> assets = new ArrayList<AssetDTO>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @RequestMapping(value = "assignment/_update", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> v1AssetsAssignmentUpdatePost(
            @ApiParam(value = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        Asset asset = assetService.updateAssignment(assetRequest);
        List<AssetDTO> assets = new ArrayList<AssetDTO>();
        assets.add(asset);
        AssetResponse response = AssetResponse.builder().assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "assignment/_search", method = RequestMethod.POST)
    public ResponseEntity<AssignmentRes> v1AssetsAssignmentSearch(
            @ApiParam(value = "Details for existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        List<AssetAssignment> assetAssignmentDetails = assetService.getAssetAssignmentDetails(assetRequest.getRequestInfo().getUserInfo().getTenantId(), assetRequest.getAsset().getId());
        AssignmentRes response = AssignmentRes.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(null, true))
                .assetAssignments(assetAssignmentDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Trigger particular asset Depreciation calculation from start
    @RequestMapping(value = "depreciation/_process", method = RequestMethod.POST)
    public ResponseEntity<CalculationRes> triggerDepreciationCalculation(
            @ApiParam(value = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {

        CalculationRes apiresponse = assetCalculationClient.triggerDepreciationCalculation(assetRequest);
        log.info("Depreciaiton api response : {}", apiresponse.getMessage());
//        List<AssetDTO> assets = new ArrayList<AssetDTO>();
//        assets.add(assetRequest.getAsset());
//        AssetResponse response = AssetResponse.builder().assets(assets)
//                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>(apiresponse, HttpStatus.OK);
    }

    // Trigger particular asset Depreciation calculation from start
    @RequestMapping(value = "depreciation/list", method = RequestMethod.POST)
    public ResponseEntity<DepreciationRes> getAssetDepreciationList(
            @ApiParam(value = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
        DepreciationRes apiresponse = assetCalculationClient.getAssetDepreciationList(assetRequest.getRequestInfo().getUserInfo().getTenantId(), assetRequest.getAsset().getId());
        List<DepreciationDetail> clonedDetails = new ArrayList<>(apiresponse.getDepreciation());
        DepreciationRes response = DepreciationRes.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(null, true))
                .depreciation(clonedDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
