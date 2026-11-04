package org.egov.commons.service;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestCallService {
    private static final Logger LOG = LogManager.getLogger(RestCallService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Sends a POST request to the specified URI with a JSON request body and
     * returns the response as a generic object.
     * <p>
     * The request is serialized to JSON for logging purposes and sent with
     * {@code Content-Type: application/json} and
     * {@code Accept: application/json} headers. The response is deserialized
     * into a {@link java.util.Map}.
     * </p>
     *
     * @param uri the target endpoint URI to which the POST request will be sent
     * @param request the request payload object to be serialized and sent in the
     *                request body
     * @return the response returned by the remote service, typically as a
     *         {@link java.util.Map}; {@code null} if an error occurs during
     *         request serialization
     * @throws HttpClientErrorException if the remote service returns a 4xx
     *                                  client error response
     */
    public Object fetchResult(StringBuilder uri, Object request) {
        Object response = null;
        try {
            LOG.info("URI: " + uri.toString());
            LOG.info("Request: " + objectMapper.writeValueAsString(request));

            // THIS is the fix - forces JSON instead of XML
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Object> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(uri.toString(), entity, Map.class);
            response = responseEntity.getBody();
            LOG.debug("Response Status from RestCallservice.fetchResult: {}", responseEntity.getStatusCode());
            LOG.debug("Fetched Result from RestCallService.fetchResult: " + objectMapper.writeValueAsString(response));

        } catch (HttpClientErrorException e) {
            LOG.error("Error occurred while calling API: status={}, body={}",
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing request: " + e.getMessage(), e);
        }
        return response;
    }
}