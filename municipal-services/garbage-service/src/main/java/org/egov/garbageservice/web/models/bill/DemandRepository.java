package org.egov.garbageservice.web.models.bill;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Client repository that calls the external billing/demand service for demand operations.
 * <p>
 * Behavior:
 * - {@link #saveDemand(RequestInfo, List)} — POST create demands; returns created {@link Demand} list.
 * - {@link #updateDemand(RequestInfo, List)} — POST update demands; returns updated list.
 * - {@link #search(String, Set, RequestInfoWrapper, String)} — GET by tenant, businessService, consumerCodes.
 * - {@link #searchId(String, Set, RequestInfoWrapper, String)} — GET by demand IDs (URI adapted from search template).
 * - Uses {@link GrbgConstants} endpoints, {@link RestCallRepository}, and {@link ObjectMapper} for HTTP and parsing.
 * <p>
 * Notes:
 * - Does not store demands in garbage-service DB; persistence is on the billing service.
 * - Parsing failures throw {@link CustomException} with PARSING ERROR.
 * - searchId reuses the search endpoint URL with consumerCode replaced by demandId.
 */
@Service("billDemandRepository")
public class DemandRepository {


    @Autowired
    private RestCallRepository restCallRepository;

    @Autowired
    private GarbageServiceConfig config;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Creates demand
     *
     * @param requestInfo The RequestInfo of the calculation Request
     * @param demands     The demands to be created
     * @return The list of demand created
     */
    public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demands) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandCreateEndpoint());
        DemandRequest request = new DemandRequest(requestInfo, demands);
        Object result = restCallRepository.fetchResult(url, request);
        DemandResponse response = null;
        try {
            response = objectMapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of create demand");
        }
        return response.getDemands();
    }


    /**
     * Updates the demand
     *
     * @param requestInfo The RequestInfo of the calculation Request
     * @param demands     The demands to be updated
     * @return The list of demand updated
     */
    public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandUpdateEndpoint());
        DemandRequest request = new DemandRequest(requestInfo, demands);
        Object result = restCallRepository.fetchResult(url, request);
        DemandResponse response = null;
        try {
            response = objectMapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of update demand");
        }
        return response.getDemands();

    }


    /**
     * Searches for demands based on the provided tenantId and consumerCodes.
     *
     * @param tenantId           the tenant ID to search within
     * @param consumerCodes      the set of consumer codes for which demands are queried
     * @param requestInfoWrapper the wrapper containing request information
     * @param businessService    the service context for the demand search
     * @return a {@link DemandResponse} object containing the matching demands
     */
    public DemandResponse search(String tenantId, Set<String> consumerCodes, RequestInfoWrapper requestInfoWrapper,
                                 String businessService) {

        String uri = config.getBillingHost().concat(config.getDemandSearchEndpoint());
        uri = uri.replace("{1}", tenantId);
        uri = uri.replace("{2}", businessService);
        uri = uri.replace("{3}", StringUtils.join(consumerCodes, ','));

        Object result = restCallRepository.fetchResult(new StringBuilder(uri), requestInfoWrapper);
        DemandResponse response = null;

        try {
            response = objectMapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response from Demand Search");
        }

        return response;
    }

    /**
     * Searches for a demand using its unique identifier.
     *
     * @param tenantId           the tenant ID where the demand resides
     * @param demandIds          the set of unique identifiers of the demands
     * @param requestInfoWrapper the wrapper containing request information
     * @param businessService    the service context for the demand search
     * @return a {@link DemandResponse} object containing the matched demands
     */
    public DemandResponse searchId(String tenantId, Set<String> demandIds, RequestInfoWrapper requestInfoWrapper,
                                   String businessService) {

        String uri = config.getBillingHost().concat(config.getDemandSearchEndpoint());
        uri = uri.replace("consumerCode", "demandId");
        uri = uri.replace("{1}", tenantId);
        uri = uri.replace("{2}", businessService);
        uri = uri.replace("{3}", StringUtils.join(demandIds, ','));

        Object result = restCallRepository.fetchResult(new StringBuilder(uri), requestInfoWrapper);
        DemandResponse response = null;

        try {
            response = objectMapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response from Demand Search");
        }

        return response;
    }


}
