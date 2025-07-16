package org.hpud.repository;

import java.util.Map;
import java.util.Optional;

import org.egov.tracer.model.CustomException;
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

	/**
	 * Fetches results from a REST service using the uri and object
	 * 
	 * @return Object
	 * @author vishal
	 */
	public Optional<Object> fetchResult(StringBuilder uri, Object request) {

		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		try {
			//log.info("Request: " + mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			log.info("URI: " + uri.toString());
			log.info("Request: {}" + request);
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.info("URI: " + uri.toString());
			log.info("Request: {}" + request);
			log.error("Exception while fetching from external service: ", e);
			throw new CustomException("REST_CALL_EXCEPTION : " + uri.toString(), e.getMessage());
		}
		return Optional.ofNullable(response);
	}
}
