package org.egov.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSContractorService;
import org.egov.service.WMSMeasurementBookService;
import org.egov.service.WMSORService;
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
import org.egov.web.models.WMSMeasurementBookApplication;
import org.egov.web.models.WMSMeasurementBookApplicationResponse;
import org.egov.web.models.WMSMeasurementBookApplicationSearchCriteria;
import org.egov.web.models.WMSMeasurementBookRequest;
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
@Api(tags = "Measurement Book")
public class WMSMeasurementBookApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSMeasurementBookService measurementBookService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
 

    @Autowired
    public WMSMeasurementBookApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSMeasurementBookService measurementBookService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.measurementBookService = measurementBookService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/mb/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Measurement Book for WMS")
    public ResponseEntity<WMSMeasurementBookApplicationResponse> v1RegistrationCreateMeasurementBook(@ApiParam(value = "Details for the new Measurement Book Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSMeasurementBookRequest wmsMeasurementBookRequest) {
    	List<WMSMeasurementBookApplication> applications = measurementBookService.registerWMSMeasurementBookRequest(wmsMeasurementBookRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsMeasurementBookRequest.getRequestInfo(), true);
        
        WMSMeasurementBookApplicationResponse response = WMSMeasurementBookApplicationResponse.builder().wmsMeasurementBookApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/mb/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Measurement Book for WMS")
    public ResponseEntity<WMSMeasurementBookApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSMeasurementBookApplicationSearchCriteria measurementBookApplicationSearchCriteria) {
        List<WMSMeasurementBookApplication> applications = measurementBookService.fetchMeasurementBookApplications(requestInfoWrapper.getRequestInfo(), measurementBookApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSMeasurementBookApplicationResponse response = WMSMeasurementBookApplicationResponse.builder().wmsMeasurementBookApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/mb/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update Measurement Book for WMS")
    public ResponseEntity<WMSMeasurementBookApplicationResponse> v1ContractorUpdatePost(@ApiParam(value = "Details for the new Contractor(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSMeasurementBookRequest measurementBookRequest) {
        List<WMSMeasurementBookApplication> applications = measurementBookService.updateMeasurementBookMaster(measurementBookRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(measurementBookRequest.getRequestInfo(), true);
        WMSMeasurementBookApplicationResponse response = WMSMeasurementBookApplicationResponse.builder().wmsMeasurementBookApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/mb/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search mb for WMS")
    public ResponseEntity<WMSMeasurementBookApplicationResponse> v1RegistrationSearchContractor(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSMeasurementBookApplicationSearchCriteria wmsMeasurementBookApplicationSearchCriteria) {
        List<WMSMeasurementBookApplication> applications = measurementBookService.searchWMSMeasurementBookApplications(requestInfoWrapper.getRequestInfo(), wmsMeasurementBookApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSMeasurementBookApplicationResponse response = WMSMeasurementBookApplicationResponse.builder().wmsMeasurementBookApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    
}
