package org.egov.echallan.repository;

import java.util.Map;
import java.util.Optional;

import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServiceRequestRepository {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	public Object fetchResult(StringBuilder uri, Object request) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		log.info("URI: "+uri.toString());
		try {
			log.info("Request: "+mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		}catch(HttpClientErrorException e) {
			log.error("External Service threw an Exception: ",e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		}catch(Exception e) {
			log.error("Exception while fetching from service: ",e);
		}

		return response;
	}
	
	public Optional<Object> fetchResultV1(StringBuilder uri, Object request) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        try {
        	log.info("request info : "+ request + " uri : " + uri);
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
            log.info("response info : "+ response);
            
        }catch(HttpClientErrorException e) {
            log.error("External Service threw an Exception: ",e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        }catch(Exception e) {
            log.error("Exception while fetching from searcher: ",e);
        }

        return Optional.ofNullable(response);
    }
}
