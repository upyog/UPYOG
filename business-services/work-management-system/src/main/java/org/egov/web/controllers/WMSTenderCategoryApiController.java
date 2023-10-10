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
import org.egov.service.WMSTenderCategoryService;
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
import org.egov.web.models.WMSTenderCategoryApplication;
import org.egov.web.models.WMSTenderCategoryApplicationResponse;
import org.egov.web.models.WMSTenderCategoryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderCategoryRequest;
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
@Api(tags = "TenderCategory Master")
public class WMSTenderCategoryApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSTenderCategoryService tenderCategoryService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSTenderCategoryApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSTenderCategoryService tenderCategoryService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.tenderCategoryService = tenderCategoryService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/tcategory/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New TenderCategory for WMS")
    public ResponseEntity<WMSTenderCategoryApplicationResponse> v1RegistrationCreateTenderCategory(@ApiParam(value = "Details for the new TenderCategory Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSTenderCategoryRequest wmsTenderCategoryRequest) {
    	List<WMSTenderCategoryApplication> applications = tenderCategoryService.registerWMSTenderCategoryRequest(wmsTenderCategoryRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsTenderCategoryRequest.getRequestInfo(), true);
        
        WMSTenderCategoryApplicationResponse response = WMSTenderCategoryApplicationResponse.builder().wmsTenderCategoryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/tcategory/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch TenderCategory for WMS")
    public ResponseEntity<WMSTenderCategoryApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSTenderCategoryApplicationSearchCriteria tenderCategoryApplicationSearchCriteria) {
        List<WMSTenderCategoryApplication> applications = tenderCategoryService.fetchTenderCategoryApplications(requestInfoWrapper.getRequestInfo(), tenderCategoryApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSTenderCategoryApplicationResponse response = WMSTenderCategoryApplicationResponse.builder().wmsTenderCategoryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/tcategory/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update TenderCategory for WMS")
    public ResponseEntity<WMSTenderCategoryApplicationResponse> v1TenderCategoryUpdatePost(@ApiParam(value = "Details for the new TenderCategory(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSTenderCategoryRequest tenderCategoryRequest) {
        List<WMSTenderCategoryApplication> applications = tenderCategoryService.updateTenderCategoryMaster(tenderCategoryRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(tenderCategoryRequest.getRequestInfo(), true);
        WMSTenderCategoryApplicationResponse response = WMSTenderCategoryApplicationResponse.builder().wmsTenderCategoryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/tcategory/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search TenderCategory for WMS")
    public ResponseEntity<WMSTenderCategoryApplicationResponse> v1RegistrationSearchTenderCategory(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSTenderCategoryApplicationSearchCriteria wmsTenderCategoryApplicationSearchCriteria) {
        List<WMSTenderCategoryApplication> applications = tenderCategoryService.searchWMSTenderCategoryApplications(requestInfoWrapper.getRequestInfo(), wmsTenderCategoryApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSTenderCategoryApplicationResponse response = WMSTenderCategoryApplicationResponse.builder().wmsTenderCategoryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    

    
}
