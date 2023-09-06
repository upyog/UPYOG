package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractAgreementService;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractAgreementApplicationResponse;
import org.egov.web.models.WMSContractAgreementApplicationSearchCriteria;
import org.egov.web.models.WMSContractAgreementRequest;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationResponse;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
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
@Api(tags = "Contract Agreement")
public class WMSContractAgreementApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSContractAgreementService contractAgreementService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSContractAgreementApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSContractAgreementService contractAgreementService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.contractAgreementService = contractAgreementService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/contractagreement/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Contract Agreement for WMS")
    public ResponseEntity<WMSContractAgreementApplicationResponse> v1RegistrationCreateContractAgreement(@ApiParam(value = "Details for the new Contract Agreement Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSContractAgreementRequest wmsContractAgreementRequest) {
    	List<WMSContractAgreementApplication> applications = contractAgreementService.registerWMSContractAgreementRequest(wmsContractAgreementRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsContractAgreementRequest.getRequestInfo(), true);
        
        WMSContractAgreementApplicationResponse response = WMSContractAgreementApplicationResponse.builder().wmsContractAgreementApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/contractagreement/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch contract agreement for WMS")
    public ResponseEntity<WMSContractAgreementApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSContractAgreementApplicationSearchCriteria contractAgreementApplicationSearchCriteria) {
        List<WMSContractAgreementApplication> applications = contractAgreementService.fetchContractAgreementApplications(requestInfoWrapper.getRequestInfo(), contractAgreementApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSContractAgreementApplicationResponse response = WMSContractAgreementApplicationResponse.builder().wmsContractAgreementApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/contractagreement/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update contract agreement for WMS")
    public ResponseEntity<WMSContractAgreementApplicationResponse> v1ContractAgreementUpdatePost(@ApiParam(value = "Details for the new ContractAgreement(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSContractAgreementRequest contractAgreementRequest) {
        List<WMSContractAgreementApplication> applications = contractAgreementService.updateContractAgreementMaster(contractAgreementRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(contractAgreementRequest.getRequestInfo(), true);
        WMSContractAgreementApplicationResponse response = WMSContractAgreementApplicationResponse.builder().wmsContractAgreementApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @RequestMapping(value="/v1/contractagreement/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search contract agreement for WMS")
    public ResponseEntity<WMSContractAgreementApplicationResponse> v1RegistrationSearchContractAgreement(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSContractAgreementApplicationSearchCriteria wmsContractAgreementApplicationSearchCriteria) {
        List<WMSContractAgreementApplication> applications = contractAgreementService.searchWMSContractAgreementApplications(requestInfoWrapper.getRequestInfo(), wmsContractAgreementApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSContractAgreementApplicationResponse response = WMSContractAgreementApplicationResponse.builder().wmsContractAgreementApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
