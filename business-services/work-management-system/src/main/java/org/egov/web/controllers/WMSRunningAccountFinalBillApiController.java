package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSRunningAccountFinalBillService;
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
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationResponse;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSRunningAccountFinalBillRequest;
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



@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-12T17:07:08.384+05:30")
@Slf4j
@ToString
@RestController
@RequestMapping("/wms-services")
@Api(tags = "Contractor Master")
public class WMSRunningAccountFinalBillApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSRunningAccountFinalBillService runningAccountFinalBillService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSRunningAccountFinalBillApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSRunningAccountFinalBillService runningAccountFinalBillService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.runningAccountFinalBillService = runningAccountFinalBillService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/runningaccount/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Running Account FinalBill for WMS")
    public ResponseEntity<WMSRunningAccountFinalBillApplicationResponse> v1RegistrationCreateRunningAccountFinalBill(@ApiParam(value = "Details for the new Running Account Final Bill Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSRunningAccountFinalBillRequest wmsRunningAccountFinalBillRequest) {
    	List<WMSRunningAccountFinalBillApplication> applications = runningAccountFinalBillService.registerWMSRunningAccountFinalBillRequest(wmsRunningAccountFinalBillRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsRunningAccountFinalBillRequest.getRequestInfo(), true);
        
        WMSRunningAccountFinalBillApplicationResponse response = WMSRunningAccountFinalBillApplicationResponse.builder().wmsRunningAccountFinalBillApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/runningaccount/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Running Account Final Bill for WMS")
    public ResponseEntity<WMSRunningAccountFinalBillApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSRunningAccountFinalBillApplicationSearchCriteria runningAccountFinalBillApplicationSearchCriteria) {
        List<WMSRunningAccountFinalBillApplication> applications = runningAccountFinalBillService.fetchRunningAccountFinalBillApplications(requestInfoWrapper.getRequestInfo(), runningAccountFinalBillApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSRunningAccountFinalBillApplicationResponse response = WMSRunningAccountFinalBillApplicationResponse.builder().wmsRunningAccountFinalBillApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/runningaccount/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Running Account for WMS")
    public ResponseEntity<WMSRunningAccountFinalBillApplicationResponse> v1RunningaccountUpdatePost(@ApiParam(value = "Details for the new Running Account Final Bill(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSRunningAccountFinalBillRequest runningAccountFinalBillRequest) {
        List<WMSRunningAccountFinalBillApplication> applications = runningAccountFinalBillService.updateRunningAccountFinalBillMaster(runningAccountFinalBillRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(runningAccountFinalBillRequest.getRequestInfo(), true);
        WMSRunningAccountFinalBillApplicationResponse response = WMSRunningAccountFinalBillApplicationResponse.builder().wmsRunningAccountFinalBillApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @RequestMapping(value="/v1/runningaccount/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Running Account for WMS")
    public ResponseEntity<WMSRunningAccountFinalBillApplicationResponse> v1RegistrationSearchContractoRunningAccountFinalBill(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSRunningAccountFinalBillApplicationSearchCriteria wmsRunningAccountFinalBillApplicationSearchCriteria) {
        List<WMSRunningAccountFinalBillApplication> applications = runningAccountFinalBillService.searchWMSRunningAccountFinalBillApplications(requestInfoWrapper.getRequestInfo(), wmsRunningAccountFinalBillApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSRunningAccountFinalBillApplicationResponse response = WMSRunningAccountFinalBillApplicationResponse.builder().wmsRunningAccountFinalBillApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
