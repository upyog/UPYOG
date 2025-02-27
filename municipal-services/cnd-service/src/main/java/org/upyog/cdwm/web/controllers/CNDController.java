package org.upyog.cdwm.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.upyog.cdwm.constants.CNDConstants;
import org.upyog.cdwm.service.impl.CNDServiceImpl;
import org.upyog.cdwm.util.CNDServiceUtil;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDApplicationResponse;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;
import org.upyog.cdwm.web.models.CNDServiceSearchResponse;
import org.upyog.cdwm.web.models.ResponseInfo;
import org.upyog.cdwm.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling Construction and Demolition (CND) service requests.
 */
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-02-12T16:11:18.767+05:30")
@Controller
@Slf4j
public class CNDController {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final CNDServiceImpl cndService;

    /**
     * Constructor for dependency injection.
     * 
     * @param objectMapper ObjectMapper instance
     * @param request HttpServletRequest instance
     * @param cndService CNDServiceImpl instance
     */
    @Autowired
    public CNDController(ObjectMapper objectMapper, HttpServletRequest request, CNDServiceImpl cndService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.cndService = cndService;
    }

    /**
     * API endpoint to create a new CND application.
     * 
     * @param cndApplicationRequest Request body containing application details
     * @return ResponseEntity with application response details
     */
    @PostMapping(value = "/_create")
    public ResponseEntity<CNDApplicationResponse> createConstructionDemolitionRequest(
        @ApiParam(value = "Details for the CND application, payment, and documents", required = true)
        @Valid @RequestBody CNDApplicationRequest cndApplicationRequest) {
        
        log.info("Received CND Application Request: {}", cndApplicationRequest);

        CNDApplicationDetail cndApplicationDetail = cndService.createConstructionAndDemolitionRequest(cndApplicationRequest);
        ResponseInfo info = CNDServiceUtil.createReponseInfo(
            cndApplicationRequest.getRequestInfo(),
            CNDConstants.BOOKING_CREATED, StatusEnum.SUCCESSFUL);
        
        CNDApplicationResponse response = CNDApplicationResponse.builder()
            .cndApplicationDetails(cndApplicationDetail)
            .responseInfo(info)
            .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * API endpoint to search CND application details.
     * 
     * @param requestInfoWrapper Wrapper containing request info
     * @param cndServiceSearchCriteria Criteria for searching CND applications
     * @return ResponseEntity with search results
     */
    @PostMapping("/_search")
    public ResponseEntity<CNDServiceSearchResponse> searchCNDApplicationDetails(
        @ApiParam(value = "Details for the CND application time, payment, and documents", required = true)
        @Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
        @ModelAttribute CNDServiceSearchCriteria cndServiceSearchCriteria) {

        log.info("Received CND Application Search Request: {}", cndServiceSearchCriteria);

        List<CNDApplicationDetail> applications = cndService.getCNDApplicationDetails(
            requestInfoWrapper.getRequestInfo(), cndServiceSearchCriteria);

        int count = cndService.getApplicationsCount(cndServiceSearchCriteria, requestInfoWrapper.getRequestInfo());

        ResponseInfo responseInfo = CNDServiceUtil.createReponseInfo(
            requestInfoWrapper.getRequestInfo(), CNDConstants.BOOKING_DETAIL_FOUND, StatusEnum.SUCCESSFUL);

        CNDServiceSearchResponse response = CNDServiceSearchResponse.builder()
            .cndApplicationDetails(applications)
            .responseInfo(responseInfo)
            .count(count)
            .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
