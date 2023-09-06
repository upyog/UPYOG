package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkEstimationService;
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
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationResponse;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkEstimationApplication;
import org.egov.web.models.WMSWorkEstimationApplicationResponse;
import org.egov.web.models.WMSWorkEstimationApplicationSearchCriteria;
import org.egov.web.models.WMSWorkEstimationRequest;
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
@RequestMapping("/wms-services")
@Api(tags = "Work Estimation")
public class WMSWorkEstimationApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSWorkEstimationService workEstimationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSWorkEstimationApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSWorkEstimationService workEstimationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workEstimationService = workEstimationService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/workestimation/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Work Estimation for WMS")
    public ResponseEntity<WMSWorkEstimationApplicationResponse> v1RegistrationCreateWorkEstimation(@ApiParam(value = "Details for the new WorkEstimation Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkEstimationRequest wmsWorkEstimationRequest) {
    	List<WMSWorkEstimationApplication> applications = workEstimationService.registerWMSWorkEstimationRequest(wmsWorkEstimationRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsWorkEstimationRequest.getRequestInfo(), true);
        
        WMSWorkEstimationApplicationResponse response = WMSWorkEstimationApplicationResponse.builder().wmsWorkEstimationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/workestimation/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Work Estimation for WMS")
    public ResponseEntity<WMSWorkEstimationApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkEstimationApplicationSearchCriteria workEstimationApplicationSearchCriteria) {
        List<WMSWorkEstimationApplication> applications = workEstimationService.fetchWorkEstimationApplications(requestInfoWrapper.getRequestInfo(), workEstimationApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkEstimationApplicationResponse response = WMSWorkEstimationApplicationResponse.builder().wmsWorkEstimationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/workestimation/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Work Estimation for WMS")
    public ResponseEntity<WMSWorkEstimationApplicationResponse> v1ContractorUpdatePost(@ApiParam(value = "Details for the new Contractor(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkEstimationRequest workEstimationRequest) {
        List<WMSWorkEstimationApplication> applications = workEstimationService.updateWorkEstimationMaster(workEstimationRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(workEstimationRequest.getRequestInfo(), true);
        WMSWorkEstimationApplicationResponse response = WMSWorkEstimationApplicationResponse.builder().wmsWorkEstimationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/workestimation/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Work Estimation for WMS")
    public ResponseEntity<WMSWorkEstimationApplicationResponse> v1RegistrationSearchWorkEstimation(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkEstimationApplicationSearchCriteria wmsWorkEstimationApplicationSearchCriteria) {
        List<WMSWorkEstimationApplication> applications = workEstimationService.searchWMSWorkEstimationApplications(requestInfoWrapper.getRequestInfo(), wmsWorkEstimationApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkEstimationApplicationResponse response = WMSWorkEstimationApplicationResponse.builder().wmsWorkEstimationApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
