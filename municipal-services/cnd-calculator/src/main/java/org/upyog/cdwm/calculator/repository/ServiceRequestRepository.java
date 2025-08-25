package org.upyog.cdwm.calculator.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Repository class for making external service requests.
 * Uses {@link RestTemplate} to send HTTP requests and retrieve responses.
 */
@Repository
@Slf4j
public class ServiceRequestRepository {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    /**
     * Constructor for {@link ServiceRequestRepository}.
     *
     * @param mapper       The {@link ObjectMapper} instance for JSON processing.
     * @param restTemplate The {@link RestTemplate} instance for making HTTP requests.
     */
    @Autowired
    public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    /**
     * Sends an HTTP POST request to the given URI and returns the response.
     * Logs both the request and response for debugging purposes.
     *
     * @param uri     The URI to send the request to.
     * @param request The request payload.
     * @return The response as an Object, typically a Map.
     */
    public Object fetchResult(StringBuilder uri, Object request) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        log.info("Sending request to: {}", uri);
        log.debug("Request payload: {}", request);

        Object response = null;
        try {
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
            log.info("Received response successfully.");
            log.debug("Response payload: {}", response);
        } catch (HttpClientErrorException e) {
            log.error("External service error: {}", e.getResponseBodyAsString(), e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Exception while making service request", e);
        }

        return response;
    }

}
