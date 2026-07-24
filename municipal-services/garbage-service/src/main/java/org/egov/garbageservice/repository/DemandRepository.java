package org.egov.garbageservice.repository;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.DemandRequest;
import org.egov.garbageservice.contract.bill.DemandResponse;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

@Repository
@Slf4j
public class DemandRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GarbageServiceConfig config;

    public List<Demand> searchAllDemands(RequestInfo requestInfo, String tenantId, String consumerCode, String businessService) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandSearchEndpoint());
        url.append("?tenantId=").append(tenantId);
        url.append("&consumerCode=").append(consumerCode);
        url.append("&businessService=").append(businessService);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper(requestInfo);
        HttpEntity<RequestInfoWrapper> requestEntity = new HttpEntity<>(requestInfoWrapper, headers);

        try {
            ResponseEntity<DemandResponse> response =
                    restTemplate.postForEntity(url.toString(), requestEntity, DemandResponse.class);
            return response.getBody().getDemands();
        } catch (Exception e) {
            log.error("Error while fetching demands from url {}: {}", url, e.getMessage(), e);
            throw new ServiceCallException("Error while fetching demands: " + e.getMessage());
        }
    }
    public List<Demand> searchDemand(RequestInfo requestInfo, String tenantId, String consumerCode, String businessService) {
        // This is a placeholder. The actual implementation might be different based on the billing service API.
        return searchAllDemands(requestInfo, tenantId, consumerCode, businessService);
    }

    public void updateDemand(RequestInfo requestInfo, List<Demand> demands) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandUpdateEndpoint());
        DemandRequest demandRequest = new DemandRequest(requestInfo, demands);
        try {
            restTemplate.postForObject(url.toString(), demandRequest, DemandResponse.class);
        } catch (Exception e) {
            throw new ServiceCallException("Error while updating demands");
        }
    }

    public void saveDemand(RequestInfo requestInfo, List<Demand> demands) {
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandCreateEndpoint());
        DemandRequest demandRequest = new DemandRequest(requestInfo, demands);
        try {
            restTemplate.postForObject(url.toString(), demandRequest, DemandResponse.class);
        } catch (Exception e) {
            throw new ServiceCallException("Error while saving demands");
        }
    }
}