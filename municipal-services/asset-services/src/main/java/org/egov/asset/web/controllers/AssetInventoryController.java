package org.egov.asset.web.controllers;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.service.AssetInventoryService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AssetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/assets/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class AssetInventoryController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private AssetInventoryService assetInventoryService;

    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> createInventory(
            @ApiParam(value = "Details for the new asset inventory + RequestInfo metadata.", required = true) 
            @Valid @RequestBody AssetRequest assetRequest) {
        
        Asset asset = assetInventoryService.createInventory(assetRequest);
        List<AssetDTO> assets = new ArrayList<>();
        assets.add(asset);
        
        AssetResponse response = AssetResponse.builder()
                .assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_update", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> updateInventory(
            @ApiParam(value = "Details for updating existing asset inventory + RequestInfo metadata.", required = true) 
            @Valid @RequestBody AssetRequest assetRequest) {
        
        Asset asset = assetInventoryService.updateInventory(assetRequest);
        List<AssetDTO> assets = new ArrayList<>();
        assets.add(asset);
        
        AssetResponse response = AssetResponse.builder()
                .assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public ResponseEntity<AssetResponse> searchInventory(
            @ApiParam(value = "Details for searching asset inventory + RequestInfo metadata.", required = true) 
            @Valid @RequestBody AssetRequest assetRequest) {
        
        List<AssetDTO> assets = assetInventoryService.searchInventory(assetRequest);
        
        AssetResponse response = AssetResponse.builder()
                .assets(assets)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}