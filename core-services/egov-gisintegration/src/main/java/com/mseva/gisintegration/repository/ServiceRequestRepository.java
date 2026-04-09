package com.mseva.gisintegration.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Repository
public class ServiceRequestRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    public Object fetchResult(StringBuilder uri, Object request) {
        Object response = null;
        try {
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching results from external service", e);
        }
        return response;
    }
}