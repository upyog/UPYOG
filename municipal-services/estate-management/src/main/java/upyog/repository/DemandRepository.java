package upyog.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import upyog.config.EstateConfiguration;
import upyog.config.ServiceConstants;
import upyog.web.models.billing.Demand;
import upyog.web.models.billing.DemandRequest;
import upyog.web.models.billing.DemandResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DemandRepository {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private EstateConfiguration config;

    @Autowired
    private ObjectMapper mapper;


    public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demand) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandCreateEndpoint());
        DemandRequest request = new DemandRequest(requestInfo, demand);

        Object result = serviceRequestRepository.fetchResult(url, request);
        DemandResponse response = null;
        try {
            response = mapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ServiceConstants.PARSING_ERROR_CODE, "Failed to parse response of create demand");
        }
        return response.getDemands();
    }


    public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandUpdateEndpoint());
        DemandRequest request = new DemandRequest(requestInfo, demands);
        Object result = serviceRequestRepository.fetchResult(url, request);
        DemandResponse response = null;
        try {
            response = mapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ServiceConstants.PARSING_ERROR_CODE, "Failed to parse response of update demand");
        }
        return response.getDemands();
    }

    /**
     * Searches for unpaid demands for the specified consumer.
     * Used while calculating penalty on outstanding dues.
     */
    public List<Demand> searchDemand(RequestInfo requestInfo, String tenantId, String consumerCode, String businessService) {
        return search(requestInfo, tenantId, consumerCode, businessService, null, null, true);
    }

    /**
     * Searches for all demands for the specified consumer,
     * including paid and unpaid demands.
     * Used to prevent duplicate demand generation for the same billing period.
     */
    public List<Demand> searchAllDemands(RequestInfo requestInfo, String tenantId, String consumerCode, String businessService) {
        return search(requestInfo, tenantId, consumerCode, businessService, null, null, false);
    }

    /**
     * Searches demands from the billing service based on the supplied criteria.
     *
     * @param requestInfo request information
     * @param tenantId tenant identifier
     * @param consumerCode consumer code
     * @param businessService business service name
     * @param periodFrom billing period start timestamp in epoch milliseconds
     * @param periodTo billing period end timestamp in epoch milliseconds
     * @param unpaidOnly true to fetch only unpaid demands; false to fetch all demands
     * @return matching demands or an empty list if no demands are found
     */
    private List<Demand> search(RequestInfo requestInfo, String tenantId, String consumerCode,
                                String businessService, Long periodFrom, Long periodTo, boolean unpaidOnly) {
        StringBuilder url = new StringBuilder(config.getBillingHost())
                .append(config.getDemandSearchEndpoint())
                .append("?tenantId=").append(tenantId)
                .append("&consumerCode=").append(consumerCode)
                .append("&businessService=").append(businessService);
        if (unpaidOnly)  url.append("&isPaymentCompleted=false");
        if (periodFrom != null) url.append("&periodFrom=").append(periodFrom);
        if (periodTo   != null) url.append("&periodTo=").append(periodTo);
        log.info("Searching demands at: {}", url);
        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);
        Object result = serviceRequestRepository.fetchResult(url, request);
        DemandResponse response = null;
        try {
            response = mapper.convertValue(result, DemandResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ServiceConstants.PARSING_ERROR_CODE, "Failed to parse response of search demand");
        }
        return response != null && response.getDemands() != null ? response.getDemands() : Collections.emptyList();
    }

}
