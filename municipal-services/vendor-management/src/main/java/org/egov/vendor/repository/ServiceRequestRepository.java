package org.egov.vendor.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
@Slf4j
public class ServiceRequestRepository {

    private final ObjectMapper mapper;

    private final RestTemplate restTemplate;


    /**
     * Fetches results from a REST service using the uri and object
     *
     * @return Object
     * @author vishal
     * @updated Bimal
     */
    @Autowired
    public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }


    public Object fetchResult(StringBuilder uri, Object request) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        log.debug("URI: " + uri.toString());
        try {
            log.debug("Request: " + mapper.writeValueAsString(request));
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("External Service threw an Exception: ", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Exception while fetching from searcher: ", e);
        }

        return response;
    }

    public <T> T fetchResultWithPathParams(StringBuilder uri, Map<String, String> pathParams, Class<T> responseType) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            // Build the URI with path parameters
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                uri = new StringBuilder(uri.toString().replace(placeholder, entry.getValue()));
            }

            log.debug("Resolved URI: {}", uri);

            // Make the GET request
            return restTemplate.getForObject(uri.toString(), responseType);

        } catch (HttpClientErrorException e) {
            log.error("External Service threw an Exception: {}", e.getMessage(), e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Exception while fetching from service: {}", e.getMessage(), e);
        }
        return null;
    }

}