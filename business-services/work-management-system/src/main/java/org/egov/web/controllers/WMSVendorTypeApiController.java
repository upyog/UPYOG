package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSContractorSubTypeService;
import org.egov.service.WMSORService;
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
public class WMSVendorTypeApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSVendorTypeService vendorTypeService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSVendorTypeApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSVendorTypeService vendorTypeService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.vendorTypeService = vendorTypeService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/vendor/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New VendorType for WMS")
    public ResponseEntity<WMSVendorTypeApplicationResponse> v1RegistrationCreateVendorType(@ApiParam(value = "Details for the new VendorType Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSVendorTypeRequest wmsVendorTypeRequest) {
    	List<WMSVendorTypeApplication> applications = vendorTypeService.registerWMSVendorTypeRequest(wmsVendorTypeRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsVendorTypeRequest.getRequestInfo(), true);
        
        WMSVendorTypeApplicationResponse response = WMSVendorTypeApplicationResponse.builder().wmsVendorTypeApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/vendor/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch VendorType for WMS")
    public ResponseEntity<WMSVendorTypeApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSVendorTypeApplicationSearchCriteria vendorTypeApplicationSearchCriteria) {
        List<WMSVendorTypeApplication> applications = vendorTypeService.fetchVendorTypeApplications(requestInfoWrapper.getRequestInfo(), vendorTypeApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSVendorTypeApplicationResponse response = WMSVendorTypeApplicationResponse.builder().wmsVendorTypeApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/vendor/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update VendorType for WMS")
    public ResponseEntity<WMSVendorTypeApplicationResponse> v1VendorTypeUpdatePost(@ApiParam(value = "Details for the new VendorType(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSVendorTypeRequest vendorTypeRequest) {
        List<WMSVendorTypeApplication> applications = vendorTypeService.updateVendorTypeMaster(vendorTypeRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(vendorTypeRequest.getRequestInfo(), true);
        WMSVendorTypeApplicationResponse response = WMSVendorTypeApplicationResponse.builder().wmsVendorTypeApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/vendor/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search VendorType for WMS")
    public ResponseEntity<WMSVendorTypeApplicationResponse> v1RegistrationSearchVendorType(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSVendorTypeApplicationSearchCriteria wmsVendorTypeApplicationSearchCriteria) {
        List<WMSVendorTypeApplication> applications = vendorTypeService.searchWMSVendorTypeApplications(requestInfoWrapper.getRequestInfo(), wmsVendorTypeApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSVendorTypeApplicationResponse response = WMSVendorTypeApplicationResponse.builder().wmsVendorTypeApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
