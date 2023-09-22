package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSContractorSubTypeService;
import org.egov.service.WMSORService;
import org.egov.service.WMSVendorClassService;
import org.egov.service.WMSVendorTypeService;
import org.egov.service.WMSWorkService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationResponse;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSContractorSubTypeApplicationResponse;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeRequest;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationResponse;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSSchemeRequest;
import org.egov.web.models.WMSVendorClassApplication;
import org.egov.web.models.WMSVendorClassApplicationResponse;
import org.egov.web.models.WMSVendorClassApplicationSearchCriteria;
import org.egov.web.models.WMSVendorClassRequest;
import org.egov.web.models.WMSVendorTypeApplication;
import org.egov.web.models.WMSVendorTypeApplicationResponse;
import org.egov.web.models.WMSVendorTypeApplicationSearchCriteria;
import org.egov.web.models.WMSVendorTypeRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationResponse;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/*@CrossOrigin(
	    origins = {
	        "http://localhost:3000", 
	        "https://staging.example.com", 
	        "https://app.example.com"
	        },
	    methods = {
	                RequestMethod.OPTIONS,
	                RequestMethod.GET,
	                RequestMethod.PUT,
	                RequestMethod.DELETE,
	                RequestMethod.POST
	})*/
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-12T17:07:08.384+05:30")
@Slf4j
@ToString
@RestController
@RequestMapping("/wms-services")
@Api(tags = "VendorType Master")
public class WMSVendorClassApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSVendorClassService vendorClassService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSVendorClassApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSVendorClassService vendorClassService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.vendorClassService = vendorClassService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/vendorc/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New VendorClass for WMS")
    public ResponseEntity<WMSVendorClassApplicationResponse> v1RegistrationCreateVendorClass(@ApiParam(value = "Details for the new VendorClass Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSVendorClassRequest wmsVendorClassRequest) {
    	List<WMSVendorClassApplication> applications = vendorClassService.registerWMSVendorClassRequest(wmsVendorClassRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsVendorClassRequest.getRequestInfo(), true);
        
        WMSVendorClassApplicationResponse response = WMSVendorClassApplicationResponse.builder().wmsVendorClassApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/vendorc/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch VendorClass for WMS")
    public ResponseEntity<WMSVendorClassApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSVendorClassApplicationSearchCriteria vendorClassApplicationSearchCriteria) {
        List<WMSVendorClassApplication> applications = vendorClassService.fetchVendorClassApplications(requestInfoWrapper.getRequestInfo(), vendorClassApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSVendorClassApplicationResponse response = WMSVendorClassApplicationResponse.builder().wmsVendorClassApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/vendorc/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update VendorClass for WMS")
    public ResponseEntity<WMSVendorClassApplicationResponse> v1VendorClassUpdatePost(@ApiParam(value = "Details for the new VendorClass(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSVendorClassRequest vendorClassRequest) {
        List<WMSVendorClassApplication> applications = vendorClassService.updateVendorClassMaster(vendorClassRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(vendorClassRequest.getRequestInfo(), true);
        WMSVendorClassApplicationResponse response = WMSVendorClassApplicationResponse.builder().wmsVendorClassApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/vendorc/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search VendorClass for WMS")
    public ResponseEntity<WMSVendorClassApplicationResponse> v1RegistrationSearchVendorClass(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSVendorClassApplicationSearchCriteria wmsVendorClassApplicationSearchCriteria) {
        List<WMSVendorClassApplication> applications = vendorClassService.searchWMSVendorClassApplications(requestInfoWrapper.getRequestInfo(), wmsVendorClassApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSVendorClassApplicationResponse response = WMSVendorClassApplicationResponse.builder().wmsVendorClassApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
