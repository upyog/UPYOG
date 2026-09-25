package org.egov.noc.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.web.model.aai.AAIApplicationStatus;
import org.egov.noc.web.model.aai.AAIStatusResponse;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for integrating with AAI NOCAS to fetch NOC status updates
 */
@Slf4j
@Service
public class AAINOCASIntegrationService {

    @Autowired
    private NOCConfiguration config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    /**
     * Fetches NOC statuses from AAI NOCAS API
     *
     * @return AAI status response
     */
    public AAIStatusResponse fetchNOCStatusFromAAI() {
        if (!config.getAaiNocasEnabled()) {
            return AAIStatusResponse.builder()
                    .success(false)
                    .errorMessage("AAI integration disabled")
                    .build();
        }

        try {
            AAIStatusResponse response = callAAINOCASAPI();
            log.info("AAI sync: fetched {} applications", 
                    response.getApplicationStatuses() != null ? response.getApplicationStatuses().size() : 0);
            return response;
        } catch (Exception e) {
            log.error("AAI sync failed", e);
            throw new CustomException(NOCConstants.AAI_INTEGRATION_ERROR, "Failed to fetch AAI status");
        }
    }

    /**
     * Fetches NOC status for a single UNIQUE ID using filter search API
     * 
     * @param uniqueId UNIQUE ID (BPA application number / sourceRefId)
     * @param requestInfo Request info
     * @return AAI status response
     */
    public AAIStatusResponse fetchNOCStatusByUniqueId(String uniqueId, RequestInfo requestInfo) {
        if (!config.getAaiNocasEnabled()) {
            return AAIStatusResponse.builder()
                    .success(false)
                    .errorMessage("AAI integration disabled")
                    .build();
        }

        if (StringUtils.isEmpty(uniqueId)) {
            return AAIStatusResponse.builder()
                    .success(false)
                    .errorMessage("UNIQUE ID is required")
                    .build();
        }

        try {
            AAIStatusResponse response = callAAIFilterSearchAPI(uniqueId);
            log.info("AAI sync: fetched UNIQUE ID {}", uniqueId);
            return response;
        } catch (Exception e) {
            log.error("AAI sync failed for UNIQUE ID: {}", uniqueId, e);
            throw new CustomException(NOCConstants.AAI_INTEGRATION_ERROR, "Failed to fetch AAI status for UNIQUE ID: " + uniqueId);
        }
    }

    /**
     * Calls AAI NOCAS web service using GET request with form-urlencoded data
     * This method constructs the API URL with required query parameters:
     * - AuthorityID: AAI authority identifier from configuration
     * - AuthorityKey: AAI authority key (encoded with special handling for @ and !
     * characters)
     * 
     * @return AAI status response
     * @throws Exception if call fails
     */
    private AAIStatusResponse callAAINOCASAPI() throws Exception {
        try {
            StringBuilder uri = new StringBuilder(config.getAaiNocasApiUrl());
            String authorityId = URLEncoder.encode(config.getAaiNocasAuthorityId(), StandardCharsets.UTF_8.toString());
            String authorityKey = encodeAuthorityKey(config.getAaiNocasAuthorityKey());
            
            uri.append(uri.toString().contains("?") ? "&" : "?");
            uri.append("AuthorityID=").append(authorityId);
            uri.append("&AuthorityKey=").append(authorityKey);
            
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            
            Object response = serviceRequestRepository.getResultWithCustomHeaders(uri, headers);
            return parseAAIResponse(response);
        } catch (ServiceCallException e) {
            log.error("AAI API call failed: {}", e.getMessage());
            throw new Exception("AAI API call failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("AAI API call failed", e);
            throw new Exception("AAI API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Calls AAI NOCAS filter search API to fetch NOC status for a specific UNIQUE ID
     * 
     * This method constructs the filter search API URL with required query parameters:
     * - AuthorityID: AAI authority identifier from configuration
     * - AuthorityKey: AAI authority key (encoded with special handling for @ and ! characters)
     * - AuthorityFilterSearchData: The UNIQUE ID (BPA application number / sourceRefId) to search for
     * - FilterSearchType: Set to BYUNIQUEID to filter by UNIQUE ID
     * 
     * The method sends a GET request with application/x-www-form-urlencoded content type header
     * and parses the response to extract NOC status information for the given UNIQUE ID.
     * 
     * @param uniqueId UNIQUE ID (BPA application number / sourceRefId) to search for
     * @return AAIStatusResponse containing the NOC status for the given UNIQUE ID
     * @throws Exception if API URL is not configured, API call fails, or response parsing fails
     */
    private AAIStatusResponse callAAIFilterSearchAPI(String uniqueId) throws Exception {
        try {
            String filterSearchApiUrl = config.getAaiNocasFilterSearchApiUrl();
            if (StringUtils.isEmpty(filterSearchApiUrl)) {
                throw new Exception("AAI filter search API URL not configured");
            }

            String authorityId = config.getAaiNocasAuthorityId();
            String authorityKey = config.getAaiNocasAuthorityKey();
            
            String encodedAuthorityKey = encodeAuthorityKey(authorityKey);
            String encodedUniqueId = URLEncoder.encode(uniqueId, StandardCharsets.UTF_8.toString());
            
            StringBuilder uriBuilder = new StringBuilder(filterSearchApiUrl);
            uriBuilder.append(uriBuilder.toString().contains("?") ? "&" : "?");
            uriBuilder.append("AuthorityID=").append(URLEncoder.encode(authorityId, StandardCharsets.UTF_8.toString()));
            uriBuilder.append("&AuthorityKey=").append(encodedAuthorityKey);
            uriBuilder.append("&AuthorityFilterSearchData=").append(encodedUniqueId);
            uriBuilder.append("&FilterSearchType=BYUNIQUEID");
            
            StringBuilder uriStringBuilder = new StringBuilder(uriBuilder.toString());
            
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            
            Object response = serviceRequestRepository.getResultWithCustomHeaders(uriStringBuilder, headers);
            return parseAAIResponse(response);
        } catch (ServiceCallException e) {
            log.error("AAI filter search API call failed for UNIQUE ID {}: {}", uniqueId, e.getMessage());
            throw new Exception("AAI filter search API call failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("AAI filter search API call failed for UNIQUE ID: {}", uniqueId, e);
            throw new Exception("AAI filter search API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Encodes authority key with special handling for @ and ! characters
     * Only encodes @ to %40, leaves ! as is
     * 
     * @param authorityKey Authority key from config
     * @return Encoded authority key
     */
    private String encodeAuthorityKey(String authorityKey) {
        if (authorityKey == null) {
            return "";
        }
        return authorityKey.replace("@", "%40");
    }

    /**
     * Parses JSON response from AAI NOCAS API and converts to structured response
     * 
     * @param jsonResponse Raw JSON response from AAI API (Map structure)
     * @return AAIStatusResponse containing parsed application statuses
     */
    @SuppressWarnings("unchecked")
    private AAIStatusResponse parseAAIResponse(Object jsonResponse) {
        try {
            if (jsonResponse == null || !(jsonResponse instanceof Map)) {
                log.error("Invalid response from AAI API: {}", jsonResponse);
                return AAIStatusResponse.builder()
                        .success(false)
                        .errorMessage("Invalid response format from AAI API")
                        .build();
            }

            Map<String, Object> responseMap = (Map<String, Object>) jsonResponse;
            Boolean status = responseMap.get("Status") instanceof Boolean 
                    ? (Boolean) responseMap.get("Status") 
                    : Boolean.FALSE;
            String errorCode = (String) responseMap.getOrDefault("ErrorCode", "");
            String message = (String) responseMap.getOrDefault("Message", "");

            if (!Boolean.TRUE.equals(status) && !"00000".equals(errorCode)) {
                log.warn("AAI API returned error: ErrorCode={}, Message={}", errorCode, message);
                return AAIStatusResponse.builder()
                        .success(false)
                        .errorMessage(message != null ? message : "AAI API returned error")
                        .errorCode(errorCode)
                        .build();
            }

            List<AAIApplicationStatus> applicationStatuses = new ArrayList<>();
            Object dataObj = responseMap.get("Data");
            
            if (dataObj instanceof List) {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;
                for (Map<String, Object> dataItem : dataList) {
                    AAIApplicationStatus statusObj = mapToAAIApplicationStatus(dataItem);
                    if (statusObj != null) {
                        applicationStatuses.add(statusObj);
                    }
                }
            } else if (dataObj instanceof Map) {
                AAIApplicationStatus statusObj = mapToAAIApplicationStatus((Map<String, Object>) dataObj);
                if (statusObj != null) {
                    applicationStatuses.add(statusObj);
                }
            }

            return AAIStatusResponse.builder()
                    .applicationStatuses(applicationStatuses)
                    .success(status)
                    .errorCode(errorCode)
                    .errorMessage(message != null ? message : "AAI API returned error")
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse AAI NOCAS API response", e);
            return AAIStatusResponse.builder()
                    .success(false)
                    .errorMessage("Failed to parse AAI response: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Maps a single Data item from AAI response to AAIApplicationStatus
     * 
     * @param dataItem Data item from AAI response
     * @return AAIApplicationStatus object
     */
    private AAIApplicationStatus mapToAAIApplicationStatus(Map<String, Object> dataItem) {
        if (dataItem == null) {
            return null;
        }

        try {
            return AAIApplicationStatus.builder()
                    .nocasId((String) dataItem.get("NOCASID"))
                    .uniqueId((String) dataItem.get("UNIQUEID"))
                    .authorityName((String) dataItem.get("AuthorityName"))
                    .status((String) dataItem.get("STATUS"))
                    .pte((String) dataItem.get("PTE"))
                    .issueDate((String) dataItem.get("ISSUEDATE"))
                    .airportName((String) dataItem.get("AirportName"))
                    .remark((String) dataItem.get("REMARK"))
                    .fileName((String) dataItem.get("FILENAME"))
                    .actionType((String) dataItem.get("ActionType"))
                    .queryType((String) dataItem.get("QueryType"))
                    .searchType((String) dataItem.get("SearchType"))
                    .errorCode((String) dataItem.get("ErrorCode"))
                    .message((String) dataItem.get("Message"))
                    .statusFlag(dataItem.get("Status") instanceof Boolean 
                            ? (Boolean) dataItem.get("Status") 
                            : null)
                    .build();
        } catch (Exception e) {
            log.error("Error mapping AAI response item: {}", dataItem, e);
            return null;
        }
    }

    /**
     * Maps AAI status to NOC status
     * 
     * @param aaiStatus AAI status code
     * @return NOC status
     */
    public String mapAAIStatusToNOCStatus(String aaiStatus) {
        if (aaiStatus == null || aaiStatus.trim().isEmpty()) {
            return NOCConstants.APPLICATION_STATUS_INPROGRESS;
        }

        String statusUpper = aaiStatus.toUpperCase().trim();
        
        switch (statusUpper) {
            case NOCConstants.AAI_STATUS_ISSUED:
            case NOCConstants.AAI_STATUS_AUTOSETTLED:
            case NOCConstants.AAI_STATUS_APPROVED:
                return NOCConstants.APPROVED_STATE;
            case NOCConstants.AAI_STATUS_REJECTED:
            case NOCConstants.AAI_STATUS_VERIFICATIONREJECTED:
                return NOCConstants.APPLICATION_STATUS_REJECTED;
            case NOCConstants.AAI_STATUS_INPROCESS:
            default:
                return NOCConstants.APPLICATION_STATUS_INPROGRESS;
        }
    }
}

