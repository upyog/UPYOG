package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationResponse;
import org.egov.web.models.WMSContractorRequest;
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
@RequestMapping("/wms-services")
@Api(tags = "Contractor Master")
public class WMSContractorApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSContractorService contractorService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSContractorApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSContractorService contractorService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.contractorService = contractorService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/contractor/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Contractor for WMS")
    public ResponseEntity<WMSContractorApplicationResponse> v1RegistrationCreateContractor(@ApiParam(value = "Details for the new Contractor Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSContractorRequest wmsContractorRequest) {
    	List<WMSContractorApplication> applications = contractorService.registerWMSContractorRequest(wmsContractorRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsContractorRequest.getRequestInfo(), true);
        
        WMSContractorApplicationResponse response = WMSContractorApplicationResponse.builder().wmsContractorApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }

    
}
