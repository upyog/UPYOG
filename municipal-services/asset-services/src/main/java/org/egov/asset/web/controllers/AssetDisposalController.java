package org.egov.asset.web.controllers;

import digit.models.coremodels.RequestInfoWrapper;
import org.egov.asset.service.AssetDisposeService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.disposal.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v1/disposal")
public class AssetDisposalController {

    private final AssetDisposeService assetDisposeService;
    private final ResponseInfoFactory responseInfoFactory;

    public AssetDisposalController(AssetDisposeService assetDisposeService,
                                   ResponseInfoFactory responseInfoFactory) {
        this.assetDisposeService = assetDisposeService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping("/_create")
    public ResponseEntity<AssetDisposalResponse> createDisposal(@Valid @RequestBody AssetDisposalRequest request) {
        AssetDisposal disposal = assetDisposeService.createDisposal(request);
        List<AssetDisposal> disposals = Collections.singletonList(disposal);
        AssetDisposalResponse response = AssetDisposalResponse.builder()
                .assetDisposals(disposals)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/_update")
    public ResponseEntity<AssetDisposalResponse> updateDisposal(@Valid @RequestBody AssetDisposalRequest request) {
        AssetDisposal updatedDisposal = assetDisposeService.updateDisposal(request);
        List<AssetDisposal> disposals = Collections.singletonList(updatedDisposal);
        AssetDisposalResponse response = AssetDisposalResponse.builder()
                .assetDisposals(disposals)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_search")
    public ResponseEntity<AssetDisposalResponse> searchDisposals(
            @RequestBody AssetDisposalSearchRequest searchRequest) {
        AssetDisposalSearchCriteria searchCriteria = searchRequest.getAssetDisposalSearchCriteria();
        RequestInfoWrapper requestInfoWrapper = searchRequest.getRequestInfoWrapper();

        List<AssetDisposal> disposals = assetDisposeService.searchDisposals(searchCriteria, requestInfoWrapper.getRequestInfo());
        AssetDisposalResponse response = AssetDisposalResponse.builder()
                .assetDisposals(disposals)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
