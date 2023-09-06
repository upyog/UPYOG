package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkAwardApprovalService;
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
import org.egov.web.models.WMSWorkAwardApprovalApplication;
import org.egov.web.models.WMSWorkAwardApprovalApplicationResponse;
import org.egov.web.models.WMSWorkAwardApprovalApplicationSearchCriteria;
import org.egov.web.models.WMSWorkAwardApprovalRequest;
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
@Api(tags = " Work Award Approval")
public class WMSWorkAwardApprovalApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSWorkAwardApprovalService workAwardApprovalService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSWorkAwardApprovalApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSWorkAwardApprovalService workAwardApprovalService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workAwardApprovalService = workAwardApprovalService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/workaward/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Work Award Approval for WMS")
    public ResponseEntity<WMSWorkAwardApprovalApplicationResponse> v1RegistrationCreateWorkAwardApproval(@ApiParam(value = "Details for the new Work Award Approval Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkAwardApprovalRequest wmsWorkAwardApprovalRequest) {
    	List<WMSWorkAwardApprovalApplication> applications = workAwardApprovalService.registerWMSWorkAwardApprovalRequest(wmsWorkAwardApprovalRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsWorkAwardApprovalRequest.getRequestInfo(), true);
        
        WMSWorkAwardApprovalApplicationResponse response = WMSWorkAwardApprovalApplicationResponse.builder().wmsWorkAwardApprovalApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/workaward/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Work Award Approval  for WMS")
    public ResponseEntity<WMSWorkAwardApprovalApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkAwardApprovalApplicationSearchCriteria workAwardApprovalApplicationSearchCriteria) {
        List<WMSWorkAwardApprovalApplication> applications = workAwardApprovalService.fetchWorkAwardApprovalApplications(requestInfoWrapper.getRequestInfo(), workAwardApprovalApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkAwardApprovalApplicationResponse response = WMSWorkAwardApprovalApplicationResponse.builder().wmsWorkAwardApprovalApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/workaward/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Work Award Approval  for WMS")
    public ResponseEntity<WMSWorkAwardApprovalApplicationResponse> v1WorkAwardApprovalUpdatePost(@ApiParam(value = "Details for the new Work Award Approval(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkAwardApprovalRequest workAwardApprovalRequest) {
        List<WMSWorkAwardApprovalApplication> applications = workAwardApprovalService.updateWorkAwardApprovalMaster(workAwardApprovalRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(workAwardApprovalRequest.getRequestInfo(), true);
        WMSWorkAwardApprovalApplicationResponse response = WMSWorkAwardApprovalApplicationResponse.builder().wmsWorkAwardApprovalApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/workaward/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Work Award Approval for WMS")
    public ResponseEntity<WMSWorkAwardApprovalApplicationResponse> v1RegistrationSearchContractWorkAwardApprovalor(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkAwardApprovalApplicationSearchCriteria wmsWorkAwardApprovalApplicationSearchCriteria) {
        List<WMSWorkAwardApprovalApplication> applications = workAwardApprovalService.searchWMSWorkAwardApprovalApplications(requestInfoWrapper.getRequestInfo(), wmsWorkAwardApprovalApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkAwardApprovalApplicationResponse response = WMSWorkAwardApprovalApplicationResponse.builder().wmsWorkAwardApprovalApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
