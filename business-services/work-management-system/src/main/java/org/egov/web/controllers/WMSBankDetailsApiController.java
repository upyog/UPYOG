package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSBankDetailsService;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSORService;
import org.egov.service.WMSWorkService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationResponse;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationResponse;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationResponse;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSSchemeRequest;
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
@Api(tags = "BankDetails Master")
public class WMSBankDetailsApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSBankDetailsService bankDetailsService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSBankDetailsApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSBankDetailsService bankDetailsService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.bankDetailsService = bankDetailsService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/bank/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New BankDetails for WMS")
    public ResponseEntity<WMSBankDetailsApplicationResponse> v1RegistrationCreateBankDetails(@ApiParam(value = "Details for the new BankDetails Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSBankDetailsRequest wmsBankDetailsRequest) {
    	List<WMSBankDetailsApplication> applications = bankDetailsService.registerWMSBankDetailsRequest(wmsBankDetailsRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsBankDetailsRequest.getRequestInfo(), true);
        
        WMSBankDetailsApplicationResponse response = WMSBankDetailsApplicationResponse.builder().wmsBankDetailsApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    @RequestMapping(value="/v1/bank/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch BankDetails for WMS")
    public ResponseEntity<WMSBankDetailsApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSBankDetailsApplicationSearchCriteria bankDetailsApplicationSearchCriteria) {
        List<WMSBankDetailsApplication> applications = bankDetailsService.fetchBankDetailsApplications(requestInfoWrapper.getRequestInfo(), bankDetailsApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSBankDetailsApplicationResponse response = WMSBankDetailsApplicationResponse.builder().wmsBankDetailsApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/bank/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update BankDetails for WMS")
    public ResponseEntity<WMSBankDetailsApplicationResponse> v1BankDetailsUpdatePost(@ApiParam(value = "Details for the new BankDetails(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSBankDetailsRequest bankDetailsRequest) {
        List<WMSBankDetailsApplication> applications = bankDetailsService.updateBankDetailsMaster(bankDetailsRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(bankDetailsRequest.getRequestInfo(), true);
        WMSBankDetailsApplicationResponse response = WMSBankDetailsApplicationResponse.builder().wmsBankDetailsApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/bank/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search BankDetails for WMS")
    public ResponseEntity<WMSBankDetailsApplicationResponse> v1RegistrationSearchBankDetails(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSBankDetailsApplicationSearchCriteria wmsBankDetailsApplicationSearchCriteria) {
        List<WMSBankDetailsApplication> applications = bankDetailsService.searchWMSBankDetailsApplications(requestInfoWrapper.getRequestInfo(), wmsBankDetailsApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSBankDetailsApplicationResponse response = WMSBankDetailsApplicationResponse.builder().wmsBankDetailsApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
