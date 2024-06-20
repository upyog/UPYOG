package org.egov.web.controllers;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSWorkStatusReportService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSWorkStatusReportApplication;
import org.egov.web.models.WMSWorkStatusReportApplicationResponse;
import org.egov.web.models.WMSWorkStatusReportApplicationSearchCriteria;
import org.egov.web.models.WMSWorkStatusReportRequest;
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
@Api(tags = "WorkStatusReport")
public class WMSWorkStatusReportApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSWorkStatusReportService workStatusReportService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSWorkStatusReportApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSWorkStatusReportService workStatusReportService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workStatusReportService = workStatusReportService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/wsr/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New WorkStatusReport for WMS")
    public ResponseEntity<WMSWorkStatusReportApplicationResponse> v1RegistrationCreateWorkStatusReport(@ApiParam(value = "Details for the new WorkStatusReport Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkStatusReportRequest wmsWorkStatusReportRequest) {
    	List<WMSWorkStatusReportApplication> applications = workStatusReportService.registerWMSWorkStatusReportRequest(wmsWorkStatusReportRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsWorkStatusReportRequest.getRequestInfo(), true);
        
        WMSWorkStatusReportApplicationResponse response = WMSWorkStatusReportApplicationResponse.builder().wmsWorkStatusReportApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/wsr/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch WorkStatusReport for WMS")
    public ResponseEntity<WMSWorkStatusReportApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkStatusReportApplicationSearchCriteria workStatusReportApplicationSearchCriteria) {
        List<WMSWorkStatusReportApplication> applications = workStatusReportService.fetchWorkStatusReportApplications(requestInfoWrapper.getRequestInfo(), workStatusReportApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkStatusReportApplicationResponse response = WMSWorkStatusReportApplicationResponse.builder().wmsWorkStatusReportApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/wsr/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update WorkStatusReport for WMS")
    public ResponseEntity<WMSWorkStatusReportApplicationResponse> v1WorkStatusReportUpdatePost(@ApiParam(value = "Details for the new WorkStatusReport(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkStatusReportRequest workStatusReportRequest) {
        List<WMSWorkStatusReportApplication> applications = workStatusReportService.updateWorkStatusReportMaster(workStatusReportRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(workStatusReportRequest.getRequestInfo(), true);
        WMSWorkStatusReportApplicationResponse response = WMSWorkStatusReportApplicationResponse.builder().wmsWorkStatusReportApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/wsr/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search WorkStatusReport for WMS")
    public ResponseEntity<WMSWorkStatusReportApplicationResponse> v1RegistrationSearchWorkStatusReport(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkStatusReportApplicationSearchCriteria wmsWorkStatusReportApplicationSearchCriteria) {
        List<WMSWorkStatusReportApplication> applications = workStatusReportService.searchWMSWorkStatusReportApplications(requestInfoWrapper.getRequestInfo(), wmsWorkStatusReportApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkStatusReportApplicationResponse response = WMSWorkStatusReportApplicationResponse.builder().wmsWorkStatusReportApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    

    
}
