package org.egov.web.controllers;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSFunctionService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSFunctionApplication;
import org.egov.web.models.WMSFunctionApplicationResponse;
import org.egov.web.models.WMSFunctionApplicationSearchCriteria;
import org.egov.web.models.WMSFunctionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Api(tags = "Function Master")
public class WMSFunctionApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSFunctionService functionService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSFunctionApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSFunctionService functionService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.functionService = functionService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/func/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New BankDetails for WMS")
    public ResponseEntity<WMSFunctionApplicationResponse> v1RegistrationCreateFunction(@ApiParam(value = "Details for the new Function Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSFunctionRequest wmsFunctionRequest) {
    	List<WMSFunctionApplication> applications = functionService.registerWMSFunctionRequest(wmsFunctionRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsFunctionRequest.getRequestInfo(), true);
        
        WMSFunctionApplicationResponse response = WMSFunctionApplicationResponse.builder().wmsFunctionApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/func/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Function for WMS")
    public ResponseEntity<WMSFunctionApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSFunctionApplicationSearchCriteria functionApplicationSearchCriteria) {
        List<WMSFunctionApplication> applications = functionService.fetchFunctionApplications(requestInfoWrapper.getRequestInfo(), functionApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSFunctionApplicationResponse response = WMSFunctionApplicationResponse.builder().wmsFunctionApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/func/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Function for WMS")
    public ResponseEntity<WMSFunctionApplicationResponse> v1FunctionUpdatePost(@ApiParam(value = "Details for the new Function(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSFunctionRequest functionRequest) {
        List<WMSFunctionApplication> applications = functionService.updateFunctionMaster(functionRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(functionRequest.getRequestInfo(), true);
        WMSFunctionApplicationResponse response = WMSFunctionApplicationResponse.builder().wmsFunctionApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/func/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Function for WMS")
    public ResponseEntity<WMSFunctionApplicationResponse> v1RegistrationSearchFunction(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSFunctionApplicationSearchCriteria wmsFunctionApplicationSearchCriteria) {
        List<WMSFunctionApplication> applications = functionService.searchWMSFunctionApplications(requestInfoWrapper.getRequestInfo(), wmsFunctionApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSFunctionApplicationResponse response = WMSFunctionApplicationResponse.builder().wmsFunctionApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    

    
}
