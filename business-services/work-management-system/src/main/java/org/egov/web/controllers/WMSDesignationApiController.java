package org.egov.web.controllers;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSDepartmentService;
import org.egov.service.WMSDesignationService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSDepartmentApplication;
import org.egov.web.models.WMSDepartmentApplicationResponse;
import org.egov.web.models.WMSDepartmentApplicationSearchCriteria;
import org.egov.web.models.WMSDepartmentRequest;
import org.egov.web.models.WMSDesignationApplication;
import org.egov.web.models.WMSDesignationApplicationResponse;
import org.egov.web.models.WMSDesignationApplicationSearchCriteria;
import org.egov.web.models.WMSDesignationRequest;
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
@Api(tags = "Designation Master")
public class WMSDesignationApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSDesignationService designationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSDesignationApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSDesignationService designationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.designationService = designationService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/desn/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Designation for WMS")
    public ResponseEntity<WMSDesignationApplicationResponse> v1RegistrationCreateDesignation(@ApiParam(value = "Details for the new Designation Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSDesignationRequest wmsDesignationRequest) {
    	List<WMSDesignationApplication> applications = designationService.registerWMSDesignationRequest(wmsDesignationRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsDesignationRequest.getRequestInfo(), true);
        
        WMSDesignationApplicationResponse response = WMSDesignationApplicationResponse.builder().wmsDesignationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/desn/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Designation for WMS")
    public ResponseEntity<WMSDesignationApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSDesignationApplicationSearchCriteria designationApplicationSearchCriteria) {
        List<WMSDesignationApplication> applications = designationService.fetchDesignationApplications(requestInfoWrapper.getRequestInfo(), designationApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSDesignationApplicationResponse response = WMSDesignationApplicationResponse.builder().wmsDesignationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/desn/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Designation for WMS")
    public ResponseEntity<WMSDesignationApplicationResponse> v1DesignationUpdatePost(@ApiParam(value = "Details for the new Designation(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSDesignationRequest designationRequest) {
        List<WMSDesignationApplication> applications = designationService.updateDesignationMaster(designationRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(designationRequest.getRequestInfo(), true);
        WMSDesignationApplicationResponse response = WMSDesignationApplicationResponse.builder().wmsDesignationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/desn/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Designation for WMS")
    public ResponseEntity<WMSDesignationApplicationResponse> v1RegistrationSearchDesignation(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSDesignationApplicationSearchCriteria wmsDesignationApplicationSearchCriteria) {
        List<WMSDesignationApplication> applications = designationService.searchWMSDesignationApplications(requestInfoWrapper.getRequestInfo(), wmsDesignationApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSDesignationApplicationResponse response = WMSDesignationApplicationResponse.builder().wmsDesignationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    

    
}
