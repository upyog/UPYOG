package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationResponse;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
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
@Api(tags = "Work Master")
public class WMSWorkApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSWorkService workService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSWorkApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSWorkService workService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workService = workService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/work/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Work for WMS")
    public ResponseEntity<WMSWorkApplicationResponse> v1RegistrationCreateWork(@ApiParam(value = "Details for the new Work Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkRequest wmsWorkRequest) {
    	List<WMSWorkApplication> applications = workService.registerWMSWOrkRequest(wmsWorkRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsWorkRequest.getRequestInfo(), true);
        //sorRequest.builder().
        WMSWorkApplicationResponse response = WMSWorkApplicationResponse.builder().wmsWorkApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }

    @RequestMapping(value="/v1/work/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Work for WMS")
    public ResponseEntity<WMSWorkApplicationResponse> v1RegistrationSearchWork(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkApplicationSearchCriteria wmsWorkApplicationSearchCriteria) {
        List<WMSWorkApplication> applications = workService.searchWMSWorkApplications(requestInfoWrapper.getRequestInfo(), wmsWorkApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkApplicationResponse response = WMSWorkApplicationResponse.builder().wmsWorkApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    @RequestMapping(value="/v1/work/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Upadate Work for WMS")
    public ResponseEntity<WMSWorkApplicationResponse> v1RegistrationUpdateWork(@ApiParam(value = "Details for the new (s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkRequest wmsWorkRequest) {
        List<WMSWorkApplication> applications = workService.updateWMSWorkApplication(wmsWorkRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsWorkRequest.getRequestInfo(), true);
        WMSWorkApplicationResponse response = WMSWorkApplicationResponse.builder().wmsWorkApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
