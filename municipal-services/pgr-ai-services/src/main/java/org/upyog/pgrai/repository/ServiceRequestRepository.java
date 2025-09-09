package org.upyog.pgrai.repository;

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
 * Repository class for handling external service requests.
 * Provides methods to send HTTP requests and fetch results from external services.
 */
@Repository
@Slf4j
public class ServiceRequestRepository {

	private ObjectMapper mapper;
	private RestTemplate restTemplate;

	/**
	 * Constructor for `ServiceRequestRepository`.
	 *
	 * @param mapper       The `ObjectMapper` instance for JSON serialization and deserialization.
	 * @param restTemplate The `RestTemplate` instance for making HTTP requests.
	 */
	@Autowired
	public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
		this.mapper = mapper;
		this.restTemplate = restTemplate;
	}

	/**
	 * Sends a POST request to the specified URI with the given request payload
	 * and fetches the response as a generic object.
	 *
	 * @param uri     The URI to which the request is sent.
	 * @param request The request payload to be sent in the POST request.
	 * @return The response object fetched from the external service.
	 * @throws ServiceCallException If the external service throws an error.
	 */
	public Object fetchResult(StringBuilder uri, Object request) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		try {
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}
		return response;
	}
}