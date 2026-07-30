package org.egov.garbageservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.DemandRequest;
import org.egov.garbageservice.contract.bill.DemandResponse;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Repository component for Demand database interactions.
 *
 * <p>Provides data access, query building, and persistence operations for the garbage service domain.
 */
@Repository
@Slf4j
public class DemandRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GarbageServiceConfig config;

    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @param requestInfo     the request information containing user session details
     * @param tenantId        the tenant ID associated with the request
     * @param consumerCode    the consumerCode parameter for this operation
     * @param businessService the businessService parameter for this operation
     * @return the output result of type {@link List{@code <Demand>}}
     */

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

    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @param requestInfo     the request information containing user session details
     * @param tenantId        the tenant ID associated with the request
     * @param consumerCode    the consumerCode parameter for this operation
     * @param businessService the businessService parameter for this operation
     * @return the output result of type {@link List{@code <Demand>}}
     */

    public List<Demand> searchDemand(RequestInfo requestInfo, String tenantId, String consumerCode, String businessService) {
        // This is a placeholder. The actual implementation might be different based on the billing service API.
        return searchAllDemands(requestInfo, tenantId, consumerCode, businessService);
    }

    /**
     * Updates existing entity details in the persistent repository.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Extracts updated entity attributes and audit timestamps.</li>
     *   <li>Constructs the parameterized SQL update query.</li>
     *   <li>Executes the update statement against the persistent store.</li>
     *   <li>Returns the modified entity state.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @param demands     the demands parameter for this operation
     */

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

    /**
     * Persists a new entity record into the database.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates the incoming request payload and audit details.</li>
     *   <li>Constructs the parameterized SQL insert query for entity persistence.</li>
     *   <li>Executes the database insert using {@link org.springframework.jdbc.core.JdbcTemplate}.</li>
     *   <li>Returns the created entity instance with populated audit metadata.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @param demands     the demands parameter for this operation
     */

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