package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkOrderService;
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
import org.egov.web.models.WMSWorkOrderApplication;
import org.egov.web.models.WMSWorkOrderApplicationResponse;
import org.egov.web.models.WMSWorkOrderApplicationSearchCriteria;
import org.egov.web.models.WMSWorkOrderRequest;
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
@Api(tags = "Work Order")
public class WMSWorkOrderApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSWorkOrderService workOrderService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSWorkOrderApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSWorkOrderService workOrderService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workOrderService = workOrderService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/workorder/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New WorkOrder for WMS")
    public ResponseEntity<WMSWorkOrderApplicationResponse> v1RegistrationCreateWorkOrder(@ApiParam(value = "Details for the new WorkOrder Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkOrderRequest wmsWorkOrderRequest) {
    	List<WMSWorkOrderApplication> applications = workOrderService.registerWMSWorkOrderRequest(wmsWorkOrderRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsWorkOrderRequest.getRequestInfo(), true);
        
        WMSWorkOrderApplicationResponse response = WMSWorkOrderApplicationResponse.builder().wmsWorkOrderApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/workorder/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch WorkOrder for WMS")
    public ResponseEntity<WMSWorkOrderApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkOrderApplicationSearchCriteria workOrderApplicationSearchCriteria) {
        List<WMSWorkOrderApplication> applications = workOrderService.fetchWorkOrderApplications(requestInfoWrapper.getRequestInfo(), workOrderApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkOrderApplicationResponse response = WMSWorkOrderApplicationResponse.builder().wmsWorkOrderApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/workorder/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update WorkOrder for WMS")
    public ResponseEntity<WMSWorkOrderApplicationResponse> v1WorkOrderUpdatePost(@ApiParam(value = "Details for the new Contractor(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSWorkOrderRequest workOrderRequest) {
        List<WMSWorkOrderApplication> applications = workOrderService.updateWorkOrderMaster(workOrderRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(workOrderRequest.getRequestInfo(), true);
        WMSWorkOrderApplicationResponse response = WMSWorkOrderApplicationResponse.builder().wmsWorkOrderApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @RequestMapping(value="/v1/workorder/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search WorkOrder for WMS")
    public ResponseEntity<WMSWorkOrderApplicationResponse> v1RegistrationSearchWorkOrder(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSWorkOrderApplicationSearchCriteria wmsWorkOrderApplicationSearchCriteria) {
        List<WMSWorkOrderApplication> applications = workOrderService.searchWMSWorkOrderApplications(requestInfoWrapper.getRequestInfo(), wmsWorkOrderApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSWorkOrderApplicationResponse response = WMSWorkOrderApplicationResponse.builder().wmsWorkOrderApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
