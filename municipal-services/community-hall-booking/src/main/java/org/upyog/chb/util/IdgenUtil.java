package org.upyog.chb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.IdGenerationRequest;
import digit.models.coremodels.IdGenerationResponse;
import digit.models.coremodels.IdRequest;
import digit.models.coremodels.IdResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.repository.ServiceRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This utility class provides methods for interacting with the ID generation service
 * to generate unique IDs for various entities in the Community Hall Booking module.
 * 
 * Purpose:
 * - To generate unique identifiers for bookings, slots, and other entities.
 * - To simplify the process of creating ID generation requests and handling responses.
 * 
 * Dependencies:
 * - ServiceRequestRepository: Sends HTTP requests to the ID generation service.
 * - ObjectMapper: Serializes and deserializes JSON objects for requests and responses.
 * 
 * Features:
 * - Constructs ID generation requests with the required parameters.
 * - Sends requests to the ID generation service and processes the responses.
 * - Handles exceptions and logs errors for debugging and monitoring purposes.
 * 
 * Fields:
 * - idGenHost: The base URL of the ID generation service.
 * - idGenPath: The endpoint path for ID generation requests.
 * 
 * Methods:
 * 1. getIdList:
 *    - Sends a request to the ID generation service to generate a list of unique IDs.
 *    - Processes the response and returns the generated IDs.
 * 
 * 2. getId:
 *    - Generates a single unique ID by interacting with the ID generation service.
 * 
 * Usage:
 * - This class is used throughout the module to generate unique IDs for various entities.
 * - It ensures consistent and reusable logic for ID generation across the application.
 */
@Component
public class IdgenUtil {

    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository restRepo;

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, Integer count) {
        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
        }

        IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        StringBuilder uri = new StringBuilder(idGenHost).append(idGenPath);
        IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request), IdGenerationResponse.class);

        List<IdResponse> idResponses = response.getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}