package org.egov.noc.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch EDCR details
 */
@Slf4j
@Service
public class EDCRService {

    @Autowired
    private NOCConfiguration config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Fetches building height from EDCR
     * 
     * @param edcrNumber EDCR number
     * @param requestInfo Request info
     * @return Building height or null
     */
    public Double fetchBuildingHeight(String edcrNumber, RequestInfo requestInfo) {
        if (edcrNumber == null || edcrNumber.isEmpty()) {
            return null;
        }

        try {
            StringBuilder uri = new StringBuilder(config.getEdcrHost());
            uri.append(config.getEdcrScrutinyDetailsEndpoint());
            uri.append("?tenantId=").append(config.getEdcrTenantId());
            uri.append("&edcrNumber=").append(edcrNumber);
//  Before sending RequestInfo, remove plainAccessRequest to avoid serialization issues
            Map<String, Object> cleanRequestInfoMap = createCleanRequestInfoWrapper(requestInfo);

            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) 
                    serviceRequestRepository.fetchResult(uri, cleanRequestInfoMap);

            if (response != null) {
                Object buildingHeight = JsonPath.read(response, 
                        "$.edcrDetail[0].planDetail.blocks[0].building.buildingHeight");
                if (buildingHeight != null) {
                    return Double.valueOf(buildingHeight.toString());
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error fetching building height for EDCR {}", edcrNumber, e);
            return null;
        }
    }

    /**
     * Creates RequestInfoWrapper without plainAccessRequest field
     * Builds Map directly from cleaned JSON to avoid re-serialization issues
     * 
     * @param originalRequestInfo Original request info
     * @return RequestInfoWrapper as Map with cleaned RequestInfo
     */
    private Map<String, Object> createCleanRequestInfoWrapper(RequestInfo originalRequestInfo) {
        try {
            String json = objectMapper.writeValueAsString(originalRequestInfo);
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(json);
            jsonNode.remove("plainAccessRequest");

            @SuppressWarnings("unchecked")
            Map<String, Object> requestInfoMap = objectMapper.convertValue(jsonNode, Map.class);
            Map<String, Object> requestInfoWrapperMap = new LinkedHashMap<>();
            requestInfoWrapperMap.put("RequestInfo", requestInfoMap);
            
            return requestInfoWrapperMap;
        } catch (Exception e) {
            // Consolidated error handling
            log.error("Failed to create clean RequestInfo wrapper by removing plainAccessRequest", e);
            // Fallback to sending without removal
            @SuppressWarnings("unchecked")
            Map<String, Object> requestInfoMap = objectMapper.convertValue(originalRequestInfo, Map.class);
            Map<String, Object> requestInfoWrapperMap = new LinkedHashMap<>();
            requestInfoWrapperMap.put("RequestInfo", requestInfoMap);
            return requestInfoWrapperMap;
        }
    }
}

