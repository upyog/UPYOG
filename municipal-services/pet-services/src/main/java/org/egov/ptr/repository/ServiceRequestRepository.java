package org.egov.ptr.repository;

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
	 * Performs basic validation to prevent unnecessary calls if payload lacks required criteria
	 */
	public Object fetchResult(StringBuilder uri, Object request) {

		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		// ✅ Defensive check: skip API call if request has no valid search criteria
		if (isInvalidUserSearchRequest(request)) {
			log.warn("Skipping external call to {} — invalid or empty search criteria: {}",
					uri, safeToJson(request));
			return Optional.empty();
		}

		Object response = null;
		log.info("URI: {}", uri);
		log.debug("Request payload: {}", safeToJson(request));

		try {
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			// External service responded with 4xx (e.g., 400 Bad Request)
			log.error("External Service threw an Exception - Status: {}, Body: {}",
					e.getStatusCode(), e.getResponseBodyAsString());
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from external service: ", e);
			throw new CustomException("REST_CALL_EXCEPTION : " + uri.toString(), e.getMessage());
		}

		return response;
	}

	/**
	 * Checks if the request is a user search with missing criteria
	 */
	private boolean isInvalidUserSearchRequest(Object request) {
		try {
			String json = mapper.writeValueAsString(request);
			// crude but safe check — match key fields the user search API expects
			return json != null
					&& (json.contains("\"userName\":null") || json.contains("\"userName\":\"\""))
					&& (json.contains("\"mobileNumber\":null") || json.contains("\"mobileNumber\":\"\""))
					&& (json.contains("\"uuid\":null") || json.contains("\"uuid\":\"\""));
		} catch (Exception e) {
			log.warn("Error while checking request validity: {}", e.getMessage());
			return false; // Fall back to allowing the call
		}
	}

	/**
	 * Safely serialize an object to JSON for logging
	 */
	private String safeToJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			return String.valueOf(obj);
		}
	}
}
