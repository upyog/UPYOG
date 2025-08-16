package org.upyog.pgrai.repository;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.web.models.Idgen.IdGenerationRequest;
import org.upyog.pgrai.web.models.Idgen.IdGenerationResponse;
import org.upyog.pgrai.web.models.Idgen.IdRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for interacting with the ID generation service.
 * Provides methods to generate unique IDs using the IDGen service.
 */
@Repository
public class IdGenRepository {

    private RestTemplate restTemplate;
    private PGRConfiguration config;

    /**
     * Constructor for `IdGenRepository`.
     *
     * @param restTemplate The `RestTemplate` instance for making HTTP requests.
     * @param config       The configuration object for PGR.
     */
    @Autowired
    public IdGenRepository(RestTemplate restTemplate, PGRConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Calls the IDGen service to generate unique IDs.
     *
     * @param requestInfo The `RequestInfo` object containing request metadata.
     * @param tenantId    The tenant ID for which the IDs are generated.
     * @param name        The name of the ID format.
     * @param format      The format of the IDs to be generated.
     * @param count       The total number of IDs required.
     * @return An `IdGenerationResponse` containing the generated IDs.
     * @throws ServiceCallException If an error occurs while calling the IDGen service.
     * @throws CustomException      If a general exception occurs during the process.
     */
    public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) {
        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
        }
        IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        IdGenerationResponse response = null;
        try {
            response = restTemplate.postForObject(config.getIdGenHost() + config.getIdGenPath(), req, IdGenerationResponse.class);
        } catch (HttpClientErrorException e) {
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put(e.getCause().getClass().getName(), e.getMessage());
            throw new CustomException(map);
        }
        return response;
    }
}