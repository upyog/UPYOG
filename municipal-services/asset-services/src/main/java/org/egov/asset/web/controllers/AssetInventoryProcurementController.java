package org.egov.asset.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.service.AssetInventoryProcurementService;
import org.egov.asset.web.models.*;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/assets/procurement")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AssetInventoryProcurementController {

    @Autowired
    private AssetInventoryProcurementService procurementService;

    @PostMapping("/_create")
    public ResponseEntity<AssetInventoryProcurementResponse> createProcurementRequest(
            @Valid @RequestBody AssetInventoryProcurementCreateRequest request) {
        
        AssetInventoryProcurementRequest procurementRequest = procurementService.createProcurementRequest(request);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(request.getRequestInfo().getApiId())
                .ver(request.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(request.getRequestInfo().getMsgId())
                .msgId(request.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        AssetInventoryProcurementResponse response = AssetInventoryProcurementResponse.builder()
                .responseInfo(responseInfo)
                .procurementRequest(procurementRequest)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_update")
    public ResponseEntity<AssetInventoryProcurementResponse> updateProcurementRequest(
            @Valid @RequestBody AssetInventoryProcurementUpdateRequest request) {
        
        AssetInventoryProcurementRequest procurementRequest = procurementService.updateProcurementRequest(request);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(request.getRequestInfo().getApiId())
                .ver(request.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(request.getRequestInfo().getMsgId())
                .msgId(request.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        AssetInventoryProcurementResponse response = AssetInventoryProcurementResponse.builder()
                .responseInfo(responseInfo)
                .procurementRequest(procurementRequest)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_search")
    public ResponseEntity<AssetInventoryProcurementSearchResponse> searchProcurementRequest(
            @Valid @RequestBody AssetInventoryProcurementSearchRequest request) {
        
        List<AssetInventoryProcurementRequest> procurementRequests = procurementService.searchProcurementRequest(request);
        
        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(request.getRequestInfo().getApiId())
                .ver(request.getRequestInfo().getVer())
                .ts(System.currentTimeMillis())
                .resMsgId(request.getRequestInfo().getMsgId())
                .msgId(request.getRequestInfo().getMsgId())
                .status("successful")
                .build();

        AssetInventoryProcurementSearchResponse response = AssetInventoryProcurementSearchResponse.builder()
                .responseInfo(responseInfo)
                .procurementRequests(procurementRequests)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}