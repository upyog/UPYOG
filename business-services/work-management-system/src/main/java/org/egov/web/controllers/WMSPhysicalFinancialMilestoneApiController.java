package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSPhysicalFinancialMilestoneService;
import org.egov.service.WMSWorkService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationResponse;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplication;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplicationResponse;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalFinancialMilestoneRequest;
import org.egov.web.models.WMSSORRequest;
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
@Api(tags = "Physical Financial Milestone")
public class WMSPhysicalFinancialMilestoneApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSPhysicalFinancialMilestoneService physicalFinancialMilestoneService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSPhysicalFinancialMilestoneApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSPhysicalFinancialMilestoneService physicalFinancialMilestoneService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.physicalFinancialMilestoneService = physicalFinancialMilestoneService;
    }
    @CrossOrigin( "http://*:3000")
    @ResponseBody
    @RequestMapping(value="/v1/pfmilestone/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Physical Financial Milestone for WMS")
    public ResponseEntity<WMSPhysicalFinancialMilestoneApplicationResponse> v1RegistrationCreatePhysicalFinancialMilestone(@ApiParam(value = "Details for the new Physical Financial Milestone Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSPhysicalFinancialMilestoneRequest wmsPhysicalFinancialMilestoneRequest) {
    	List<WMSPhysicalFinancialMilestoneApplication> applications = physicalFinancialMilestoneService.registerWMSPhysicalFinancialMilestoneRequest(wmsPhysicalFinancialMilestoneRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsPhysicalFinancialMilestoneRequest.getRequestInfo(), true);
        
        WMSPhysicalFinancialMilestoneApplicationResponse response = WMSPhysicalFinancialMilestoneApplicationResponse.builder().wmsPhysicalFinancialMilestoneApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @CrossOrigin( "http://*:3000")
    @RequestMapping(value="/v1/pfmilestone/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Physical Financial Milestone for WMS")
    public ResponseEntity<WMSPhysicalFinancialMilestoneApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSPhysicalFinancialMilestoneApplicationSearchCriteria physicalFinancialMilestoneApplicationSearchCriteria) {
        List<WMSPhysicalFinancialMilestoneApplication> applications = physicalFinancialMilestoneService.fetchPhysicalFinancialMilestoneApplications(requestInfoWrapper.getRequestInfo(), physicalFinancialMilestoneApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSPhysicalFinancialMilestoneApplicationResponse response = WMSPhysicalFinancialMilestoneApplicationResponse.builder().wmsPhysicalFinancialMilestoneApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    @CrossOrigin( "http://*:3000")
    @RequestMapping(value="/v1/pfmilestone/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Physical Financial Milestone for WMS")
    public ResponseEntity<WMSPhysicalFinancialMilestoneApplicationResponse> v1PhysicalFinancialMilestoneUpdatePost(@ApiParam(value = "Details for the new Physical Financial Milestone(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSPhysicalFinancialMilestoneRequest physicalFinancialMilestoneRequest) {
        List<WMSPhysicalFinancialMilestoneApplication> applications = physicalFinancialMilestoneService.updatePhysicalFinancialMilestoneMaster(physicalFinancialMilestoneRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(physicalFinancialMilestoneRequest.getRequestInfo(), true);
        WMSPhysicalFinancialMilestoneApplicationResponse response = WMSPhysicalFinancialMilestoneApplicationResponse.builder().wmsPhysicalFinancialMilestoneApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @CrossOrigin( "http://*:3000")
    @RequestMapping(value="/v1/pfmilestone/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Physical Financial Milestone for WMS")
    public ResponseEntity<WMSPhysicalFinancialMilestoneApplicationResponse> v1RegistrationSearchPhysicalFinancialMilestone(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSPhysicalFinancialMilestoneApplicationSearchCriteria wmsPhysicalFinancialMilestoneApplicationSearchCriteria) {
        List<WMSPhysicalFinancialMilestoneApplication> applications = physicalFinancialMilestoneService.searchWMSPhysicalFinancialMilestoneApplications(requestInfoWrapper.getRequestInfo(), wmsPhysicalFinancialMilestoneApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSPhysicalFinancialMilestoneApplicationResponse response = WMSPhysicalFinancialMilestoneApplicationResponse.builder().wmsPhysicalFinancialMilestoneApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
