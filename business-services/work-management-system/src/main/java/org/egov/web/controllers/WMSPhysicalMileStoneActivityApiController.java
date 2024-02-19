package org.egov.web.controllers;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSPhysicalMileStoneActivityService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSPhysicalMileStoneActivityApplication;
import org.egov.web.models.WMSPhysicalMileStoneActivityApplicationResponse;
import org.egov.web.models.WMSPhysicalMileStoneActivityApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalMileStoneActivityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@CrossOrigin(
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
	})
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-12T17:07:08.384+05:30")
@Slf4j
@ToString
@RestController
@RequestMapping("/wms-services")
@Api(tags = "PhysicalMileStoneActivity Master")
public class WMSPhysicalMileStoneActivityApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSPhysicalMileStoneActivityService physicalMileStoneActivityService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSPhysicalMileStoneActivityApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSPhysicalMileStoneActivityService physicalMileStoneActivityService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.physicalMileStoneActivityService = physicalMileStoneActivityService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/pma/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New PhysicalMileStoneActivity for WMS")
    public ResponseEntity<WMSPhysicalMileStoneActivityApplicationResponse> v1RegistrationCreatePhysicalMileStoneActivity(@ApiParam(value = "Details for the new PhysicalMileStoneActivity Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSPhysicalMileStoneActivityRequest wmsPhysicalMileStoneActivityRequest) {
    	List<WMSPhysicalMileStoneActivityApplication> applications = physicalMileStoneActivityService.registerWMSPhysicalMileStoneActivityRequest(wmsPhysicalMileStoneActivityRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsPhysicalMileStoneActivityRequest.getRequestInfo(), true);
        
        WMSPhysicalMileStoneActivityApplicationResponse response = WMSPhysicalMileStoneActivityApplicationResponse.builder().wmsPhysicalMileStoneActivityApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/pma/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch PhysicalMileStoneActivity for WMS")
    public ResponseEntity<WMSPhysicalMileStoneActivityApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSPhysicalMileStoneActivityApplicationSearchCriteria physicalMileStoneActivityApplicationSearchCriteria) {
        List<WMSPhysicalMileStoneActivityApplication> applications = physicalMileStoneActivityService.fetchPhysicalMileStoneActivityApplications(requestInfoWrapper.getRequestInfo(), physicalMileStoneActivityApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSPhysicalMileStoneActivityApplicationResponse response = WMSPhysicalMileStoneActivityApplicationResponse.builder().wmsPhysicalMileStoneActivityApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/pma/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update PhysicalMileStoneActivity for WMS")
    public ResponseEntity<WMSPhysicalMileStoneActivityApplicationResponse> v1PhysicalMileStoneActivityUpdatePost(@ApiParam(value = "Details for the new BankDetails(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSPhysicalMileStoneActivityRequest physicalMileStoneActivityRequest) {
        List<WMSPhysicalMileStoneActivityApplication> applications = physicalMileStoneActivityService.updatePhysicalMileStoneActivityMaster(physicalMileStoneActivityRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(physicalMileStoneActivityRequest.getRequestInfo(), true);
        WMSPhysicalMileStoneActivityApplicationResponse response = WMSPhysicalMileStoneActivityApplicationResponse.builder().wmsPhysicalMileStoneActivityApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/pma/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search PhysicalMileStoneActivity for WMS")
    public ResponseEntity<WMSPhysicalMileStoneActivityApplicationResponse> v1RegistrationSearchPhysicalMileStoneActivity(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSPhysicalMileStoneActivityApplicationSearchCriteria wmsPhysicalMileStoneActivityApplicationSearchCriteria) {
        List<WMSPhysicalMileStoneActivityApplication> applications = physicalMileStoneActivityService.searchWMSPhysicalMileStoneActivityApplications(requestInfoWrapper.getRequestInfo(), wmsPhysicalMileStoneActivityApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSPhysicalMileStoneActivityApplicationResponse response = WMSPhysicalMileStoneActivityApplicationResponse.builder().wmsPhysicalMileStoneActivityApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
   

    
}
