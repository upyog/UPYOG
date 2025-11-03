package org.egov.asset.web.controllers;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.service.VendorService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/vendor")
public class VendorController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private VendorService vendorService;

    @RequestMapping(value = "/_create", method = RequestMethod.POST)
    public ResponseEntity<VendorResponse> createVendor(
            @ApiParam(value = "Details for the new vendor + RequestInfo metadata.", required = true)
            @Valid @RequestBody VendorRequest vendorRequest) {

        Vendor vendor = vendorService.createVendor(vendorRequest);

        org.egov.common.contract.response.ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(vendorRequest.getRequestInfo(), Boolean.TRUE);

        VendorResponse response = VendorResponse.builder()
                .vendors(Arrays.asList(vendor))
                .responseInfo(ResponseInfo.builder()
                        .apiId(responseInfo.getApiId())
                        .ver(responseInfo.getVer())
                        .ts(responseInfo.getTs())
                        .resMsgId(responseInfo.getResMsgId())
                        .msgId(responseInfo.getMsgId())
                        .status(responseInfo.getStatus())
                        .build())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_update", method = RequestMethod.POST)
    public ResponseEntity<VendorResponse> updateVendor(
            @ApiParam(value = "Details for updating existing vendor + RequestInfo metadata.", required = true)
            @Valid @RequestBody VendorRequest vendorRequest) {

        Vendor vendor = vendorService.updateVendor(vendorRequest);

        org.egov.common.contract.response.ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(vendorRequest.getRequestInfo(), Boolean.TRUE);

        VendorResponse response = VendorResponse.builder()
                .vendors(Arrays.asList(vendor))
                .responseInfo(ResponseInfo.builder()
                        .apiId(responseInfo.getApiId())
                        .ver(responseInfo.getVer())
                        .ts(responseInfo.getTs())
                        .resMsgId(responseInfo.getResMsgId())
                        .msgId(responseInfo.getMsgId())
                        .status(responseInfo.getStatus())
                        .build())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public ResponseEntity<VendorResponse> searchVendor(
            @ApiParam(value = "Vendor search criteria + RequestInfo metadata.", required = true)
            @Valid @RequestBody VendorRequest vendorRequest) {

        VendorSearchCriteria searchCriteria = VendorSearchCriteria.builder()
                .tenantId(vendorRequest.getVendor().getTenantId())
                .vendorName(vendorRequest.getVendor().getVendorName())
                .contactNumber(vendorRequest.getVendor().getContactNumber())
                .status(vendorRequest.getVendor().getStatus())
                .build();

        List<Vendor> vendors = vendorService.searchVendor(searchCriteria);

        org.egov.common.contract.response.ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(vendorRequest.getRequestInfo(), Boolean.TRUE);

        VendorResponse response = VendorResponse.builder()
                .vendors(vendors)
                .responseInfo(ResponseInfo.builder()
                        .apiId(responseInfo.getApiId())
                        .ver(responseInfo.getVer())
                        .ts(responseInfo.getTs())
                        .resMsgId(responseInfo.getResMsgId())
                        .msgId(responseInfo.getMsgId())
                        .status(responseInfo.getStatus())
                        .build())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}