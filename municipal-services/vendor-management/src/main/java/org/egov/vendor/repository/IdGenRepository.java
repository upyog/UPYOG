package org.egov.vendor.repository;

import digit.models.coremodels.IdGenerationRequest;
import digit.models.coremodels.IdGenerationResponse;
import digit.models.coremodels.IdRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.egov.vendor.config.VendorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class IdGenRepository {

    private final RestTemplate restTemplate;

    private final VendorConfiguration config;

    @Autowired
    public IdGenRepository(RestTemplate restTemplate, VendorConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Call iDgen to generateIds
     *
     * @param requestInfo The rquestInfo of the request
     * @param tenantId    The tenantiD of the bpa
     * @param name        Name of the foramt
     * @param format      Format of the ids
     * @param count       Total Number of idGen ids required
     * @return
     */
    public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) {
        log.info("idgen request id name and format: " + tenantId + " and " + name + " and " + format);
        List<IdRequest> reqList = new ArrayList<>();

        reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
        IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        IdGenerationResponse response = null;
        try {
            response = restTemplate.postForObject(config.getIdGenHost() + config.getIdGenPath(), req,
                    IdGenerationResponse.class);
            log.info("Id gen Response: " + response.toString());
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
