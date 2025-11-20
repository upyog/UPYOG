package org.upyog.adv.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.repository.ServiceRequestRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.IdGenerationRequest;
import digit.models.coremodels.IdGenerationResponse;
import digit.models.coremodels.IdRequest;
import digit.models.coremodels.IdResponse;
/**
 * Utility class for interacting with the ID generation service in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Generates unique IDs for various entities such as bookings, applications, and slots.
 * - Constructs and sends requests to the ID generation service.
 * - Processes responses from the ID generation service to extract generated IDs.
 * 
 * Methods:
 * - `getIdList`: Generates a list of unique IDs based on the provided parameters.
 * 
 * Dependencies:
 * - ServiceRequestRepository: Handles HTTP requests to the ID generation service.
 * - ObjectMapper: Used for JSON serialization and deserialization.
 * 
 * Configuration:
 * - `egov.idgen.host`: Base URL of the ID generation service.
 * - `egov.idgen.path`: API path for the ID generation service.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
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