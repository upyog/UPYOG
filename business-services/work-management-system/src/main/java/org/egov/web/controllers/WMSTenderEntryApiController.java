package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSTenderEntryService;
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
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSTenderEntryApplicationResponse;
import org.egov.web.models.WMSTenderEntryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderEntryRequest;
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
@RequestMapping("/wms-services")
@Api(tags = "Tender Entry")
public class WMSTenderEntryApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSTenderEntryService tenderEntryService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSTenderEntryApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSTenderEntryService tenderEntryService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.tenderEntryService = tenderEntryService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/tenderentry/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New TenderEntry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1RegistrationCreateTenderEntry(@ApiParam(value = "Details for the new Tender Entry Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSTenderEntryRequest wmsTenderEntryRequest) {
    	List<WMSTenderEntryApplication> applications = tenderEntryService.registerWMSTenderEntryRequest(wmsTenderEntryRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsTenderEntryRequest.getRequestInfo(), true);
        
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/tenderentry/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch TenderEntry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSTenderEntryApplicationSearchCriteria tenderEntryApplicationSearchCriteria) {
        List<WMSTenderEntryApplication> applications = tenderEntryService.fetchTenderEntryApplications(requestInfoWrapper.getRequestInfo(), tenderEntryApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/tenderentry/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update TenderEntry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1TenderEntryUpdatePost(@ApiParam(value = "Details for the new TenderEntry(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSTenderEntryRequest tenderEntryRequest) {
        List<WMSTenderEntryApplication> applications = tenderEntryService.updateTenderEntryMaster(tenderEntryRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(tenderEntryRequest.getRequestInfo(), true);
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/tenderentry/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Tender Entry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1RegistrationSearchTenderEntry(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSTenderEntryApplicationSearchCriteria wmsTenderEntryApplicationSearchCriteria) {
        List<WMSTenderEntryApplication> applications = tenderEntryService.searchWMSTenderEntryApplications(requestInfoWrapper.getRequestInfo(), wmsTenderEntryApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
