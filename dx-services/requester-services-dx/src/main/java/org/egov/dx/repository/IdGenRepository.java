package org.egov.dx.repository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.egov.common.contract.request.RequestInfo;
import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.IdGenerationRequest;
import org.egov.dx.web.models.IdGenerationResponse;
import org.egov.dx.web.models.IdRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class IdGenRepository {

    private Configurations config;
    private RestTemplate restTemplate;

    @Autowired
    IdGenRepository(RestTemplate restTemplate, Configurations config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }


    public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) {

        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(new IdRequest(name, tenantId, format));
        }
        IdGenerationRequest req = new IdGenerationRequest(requestInfo, reqList);
        String uri = UriComponentsBuilder
                .fromHttpUrl(config.getIdGenHost())
                .path(config.getIdGenPath())
                .build()
                .toUriString();
        try {
            return restTemplate.postForObject(uri, req,
                    IdGenerationResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("ID Gen Service failure ", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("ID Gen Service failure", e);
            throw new CustomException("IDGEN_SERVICE_ERROR", "Failed to generate ID, unknown error occurred");
        }
    }


}
