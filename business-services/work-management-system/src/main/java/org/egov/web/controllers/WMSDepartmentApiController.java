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
@Api(tags = "BankDetails Master")
public class WMSDepartmentApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSDepartmentService departmentService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSDepartmentApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSDepartmentService departmentService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.departmentService = departmentService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/dept/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Department for WMS")
    public ResponseEntity<WMSDepartmentApplicationResponse> v1RegistrationCreateDepartment(@ApiParam(value = "Details for the new Department Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSDepartmentRequest wmsDepartmentRequest) {
    	List<WMSDepartmentApplication> applications = departmentService.registerWMSDepartmentRequest(wmsDepartmentRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsDepartmentRequest.getRequestInfo(), true);
        
        WMSDepartmentApplicationResponse response = WMSDepartmentApplicationResponse.builder().wmsDepartmentApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/dept/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Department for WMS")
    public ResponseEntity<WMSDepartmentApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSDepartmentApplicationSearchCriteria departmentApplicationSearchCriteria) {
        List<WMSDepartmentApplication> applications = departmentService.fetchDepartmentApplications(requestInfoWrapper.getRequestInfo(), departmentApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSDepartmentApplicationResponse response = WMSDepartmentApplicationResponse.builder().wmsDepartmentApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/dept/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Department for WMS")
    public ResponseEntity<WMSDepartmentApplicationResponse> v1DepartmentUpdatePost(@ApiParam(value = "Details for the new Department(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSDepartmentRequest departmentRequest) {
        List<WMSDepartmentApplication> applications = departmentService.updateDepartmentMaster(departmentRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(departmentRequest.getRequestInfo(), true);
        WMSDepartmentApplicationResponse response = WMSDepartmentApplicationResponse.builder().wmsDepartmentApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/dept/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Department for WMS")
    public ResponseEntity<WMSDepartmentApplicationResponse> v1RegistrationSearchDepartment(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSDepartmentApplicationSearchCriteria wmsDepartmentApplicationSearchCriteria) {
        List<WMSDepartmentApplication> applications = departmentService.searchWMSDepartmentApplications(requestInfoWrapper.getRequestInfo(), wmsDepartmentApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSDepartmentApplicationResponse response = WMSDepartmentApplicationResponse.builder().wmsDepartmentApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    

    
}
