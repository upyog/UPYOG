
package org.egov.vendor.web.controller;

import digit.models.coremodels.RequestInfoWrapper;
import org.egov.vendor.repository.dto.VendorDetailsDTO;
import org.egov.vendor.utils.ResponseInfoFactory;
import org.egov.vendor.web.models.*;
import org.egov.vendor.service.VendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class VendorController {

    private final VendorService contractorService;
    private final ResponseInfoFactory responseInfoFactory;

    public VendorController(VendorService contractorService, ResponseInfoFactory responseInfoFactory) {
        this.contractorService = contractorService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @GetMapping
    public List<VendorAdditionalDetails> getAllContractors() {
        return contractorService.getAllVendors();
    }

    @PostMapping("_create")
    public ResponseEntity<VendorAdditionalDetailsResponse> createContractor(@RequestBody VendorAdditionalDetailsRequest request) {

        VendorAdditionalDetails vendorAdditionalDetail = contractorService.saveVendor(request);
        List<VendorAdditionalDetails> vendorAdditionalDetails = new ArrayList<VendorAdditionalDetails>();
        vendorAdditionalDetails.add(vendorAdditionalDetail);
        VendorAdditionalDetailsResponse response = VendorAdditionalDetailsResponse.builder().vendorAdditionalDetails(vendorAdditionalDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("_update")
    public ResponseEntity<VendorAdditionalDetailsResponse> updateContractor(@RequestBody VendorAdditionalDetailsRequest request) {

        VendorAdditionalDetails vendorAdditionalDetail = contractorService.updateVendor(request);
        List<VendorAdditionalDetails> vendorAdditionalDetails = new ArrayList<VendorAdditionalDetails>();
        vendorAdditionalDetails.add(vendorAdditionalDetail);
        VendorAdditionalDetailsResponse response = VendorAdditionalDetailsResponse.builder().vendorAdditionalDetails(vendorAdditionalDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContractor(@PathVariable String  id) {
        contractorService.deleteVendor(id);
        return ResponseEntity.ok().build();
    }

    //@RequestMapping(value = "/_search", method = RequestMethod.POST)
    @PostMapping("/_search")
    public ResponseEntity<VendorAdditionalDetailsResponse> searchVendors(
            @RequestBody RequestInfoWrapper request,
            @Valid @ModelAttribute SearchCriteria searchCriteria) {

        // Perform the search using the search criteria and request info
        List<VendorAdditionalDetails> vendorAdditionalDetails = contractorService.searchVendors(searchCriteria, request.getRequestInfo());

        // Build the response
        VendorAdditionalDetailsResponse response = VendorAdditionalDetailsResponse.builder()
                .vendorAdditionalDetails(vendorAdditionalDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();

        // Return the response with HTTP status OK
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/vendorPlusAdditional/_search")
    public ResponseEntity<VendorPlusAdditionalDetailsResponse> searchVendorsAndDetails(
            @RequestBody RequestInfoWrapper request,
            @Valid @ModelAttribute SearchCriteria searchCriteria) {

        // Perform the search using the search criteria and request info
        List<VendorDetailsDTO> vendorDetails = contractorService.searchVendorsAndDetails(searchCriteria, request.getRequestInfo());

        // Build the response
        VendorPlusAdditionalDetailsResponse response = VendorPlusAdditionalDetailsResponse.builder()
                .vendorDetailsDTO(vendorDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();

         //Return the response with HTTP status OK
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
