package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSORService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-12T17:07:08.384+05:30")
@Slf4j
@ToString
@RestController
@RequestMapping("/work-management-service")
@Api(tags = "State Schedule Rates Master ")
public class WMSApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSORService sorService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSORService sorService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.sorService = sorService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/sor/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New SOR for WMS")
    public ResponseEntity<SORApplicationResponse> v1RegistrationCreateSOR(@ApiParam(value = "Details for the new SOR Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSSORRequest sorRequest) {
    	List<ScheduleOfRateApplication> applications = sorService.registerWSMRequest(sorRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(sorRequest.getRequestInfo(), true);
        //sorRequest.builder().
        SORApplicationResponse response = SORApplicationResponse.builder().sorApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }

    @RequestMapping(value="/v1/sor/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search SOR for WMS")
    public ResponseEntity<SORApplicationResponse> v1RegistrationSearchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute SORApplicationSearchCriteria sorApplicationSearchCriteria) {
        List<ScheduleOfRateApplication> applications = sorService.searchSORApplications(requestInfoWrapper.getRequestInfo(), sorApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        SORApplicationResponse response = SORApplicationResponse.builder().sorApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    @RequestMapping(value="/v1/sor/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Upadate SOR for WMS")
    public ResponseEntity<SORApplicationResponse> v1RegistrationUpdatePost(@ApiParam(value = "Details for the new (s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSSORRequest sorRequest) {
        List<ScheduleOfRateApplication> applications = sorService.updateBtApplication(sorRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(sorRequest.getRequestInfo(), true);
        SORApplicationResponse response = SORApplicationResponse.builder().sorApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
