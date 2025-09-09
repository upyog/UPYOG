package org.upyog.pgrai.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.upyog.pgrai.service.PGRService;
import org.upyog.pgrai.util.PGRConstants;
import org.upyog.pgrai.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upyog.pgrai.web.models.*;

import java.io.IOException;
import java.util.*;

import javax.validation.Valid;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

/**
 * Controller for handling Public Grievance Redressal (PGR) service requests.
 * Provides endpoints for creating, searching, updating, and counting service requests.
 *
 * This controller interacts with the PGR service layer to process requests and generate responses.
 * It also utilizes the ResponseInfoFactory to create standardized response metadata.
 *
 * Endpoints:
 * - Create a new service request.
 * - Search for service requests based on criteria.
 * - Perform a plain search for service requests.
 * - Update an existing service request.
 * - Count the number of service requests based on criteria.
 *
 * Dependencies:
 * - {@link ObjectMapper} for JSON serialization and deserialization.
 * - {@link PGRService} for business logic related to service requests.
 * - {@link ResponseInfoFactory} for creating response metadata.
 */
@Controller
@RequestMapping("/v1")
@Slf4j
public class RequestsApiController{

    private final ObjectMapper objectMapper;

    private PGRService pgrService;

    private ResponseInfoFactory responseInfoFactory;


    @Autowired
    public RequestsApiController(ObjectMapper objectMapper, PGRService pgrService, ResponseInfoFactory responseInfoFactory) {
        this.objectMapper = objectMapper;
        this.pgrService = pgrService;
        this.responseInfoFactory = responseInfoFactory;
    }


    /**
     * Handles the creation of a new service request.
     *
     * @param request The service request to be created.
     * @return ResponseEntity containing the created service request.
     * @throws IOException If an error occurs during processing.
     */
    @RequestMapping(value="/request/_create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> requestsCreatePost(@Valid @RequestBody ServiceRequest request) throws IOException {
        ServiceRequest enrichedReq = pgrService.create(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(enrichedReq.getService()).workflow(enrichedReq.getWorkflow()).build();
        ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).serviceWrappers(Collections.singletonList(serviceWrapper)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Searches for service requests based on the given criteria.
     *
     * @param requestInfoWrapper Wrapper containing request information.
     * @param criteria           Search criteria for service requests.
     * @return ResponseEntity containing the search results.
     */
    @RequestMapping(value="/request/_search", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> requestsSearchPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                              @Valid @ModelAttribute RequestSearchCriteria criteria) {
    	
    	String tenantId = criteria.getTenantId();
        List<ServiceWrapper> serviceWrappers = pgrService.search(requestInfoWrapper.getRequestInfo(), criteria);
        Map<String,Integer> dynamicData = pgrService.getDynamicData(tenantId);
        
        int complaintsResolved = dynamicData.get(PGRConstants.COMPLAINTS_RESOLVED);
	    int averageResolutionTime = dynamicData.get(PGRConstants.AVERAGE_RESOLUTION_TIME);
	    int complaintTypes = pgrService.getComplaintTypes();
        
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).serviceWrappers(serviceWrappers).complaintsResolved(complaintsResolved)
        		.averageResolutionTime(averageResolutionTime).complaintTypes(complaintTypes).build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Performs a plain search for service requests.
     *
     * @param requestInfoWrapper Wrapper containing request information.
     * @param requestSearchCriteria Search criteria for service requests.
     * @return ResponseEntity containing the search results.
     */
    @RequestMapping(value = "request/_plainsearch", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> requestsPlainSearchPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute RequestSearchCriteria requestSearchCriteria) {
        List<ServiceWrapper> serviceWrappers = pgrService.plainSearch(requestInfoWrapper.getRequestInfo(), requestSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).serviceWrappers(serviceWrappers).build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Updates an existing service request.
     *
     * @param request The service request to be updated.
     * @return ResponseEntity containing the updated service request.
     * @throws IOException If an error occurs during processing.
     */
    @RequestMapping(value="/request/_update", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> requestsUpdatePost(@Valid @RequestBody ServiceRequest request) throws IOException {
        ServiceRequest enrichedReq = pgrService.update(request);
        ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(enrichedReq.getService()).workflow(enrichedReq.getWorkflow()).build();
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        ServiceResponse response = ServiceResponse.builder().responseInfo(responseInfo).serviceWrappers(Collections.singletonList(serviceWrapper)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Counts the number of service requests based on the given criteria.
     *
     * @param requestInfoWrapper Wrapper containing request information.
     * @param criteria           Search criteria for counting service requests.
     * @return ResponseEntity containing the count of service requests.
     */
    @RequestMapping(value="/request/_count", method = RequestMethod.POST)
    public ResponseEntity<CountResponse> requestsCountPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                           @Valid @ModelAttribute RequestSearchCriteria criteria) {
        Integer count = pgrService.count(requestInfoWrapper.getRequestInfo(), criteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        CountResponse response = CountResponse.builder().responseInfo(responseInfo).count(count).build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
