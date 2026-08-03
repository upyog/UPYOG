package org.egov.noc.repository;

import java.net.URI;
import java.util.Map;

import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServiceRequestRepository {

	private ObjectMapper mapper;

	private RestTemplate restTemplate;

	@Autowired
	public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
		this.mapper = mapper;
		this.restTemplate = restTemplate;
	}

	/**
	 * fetch the results using the request and uri appling restTempalte
	 * @param uri
	 * @param request
	 * @return
	 */
	public Object fetchResult(StringBuilder uri, Object request) {
		Object response = null;
		log.info("URI: " + uri.toString());
		try {
			log.info("Request: " + mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}

		return response;
	}

	/**
	 * Fetch results using GET request with custom headers
	 * @param uri URI endpoint with query parameters
	 * @param headers Custom headers map
	 * @return Response object
	 */
	public Object getResultWithCustomHeaders(StringBuilder uri, Map<String, String> headers) {
		try {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			if (headers != null) {
				headers.forEach((key, value) -> {
					if (!"Content-Type".equalsIgnoreCase(key)) {
						httpHeaders.add(key, value);
					}
				});
			}
			HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
			URI finalUri = URI.create(uri.toString());
			return restTemplate.exchange(finalUri, HttpMethod.GET, httpEntity, Map.class).getBody();
		} catch (HttpClientErrorException e) {
			int statusCode = e.getStatusCode().value();
			String responseBody = e.getResponseBodyAsString();
			if (statusCode == 400) {
				log.error("Bad Request (400) from external service: {}", responseBody);
				throw new ServiceCallException("Bad Request: " + responseBody);
			} else if (statusCode == 401) {
				log.error("Unauthorized (401) from external service: {}", responseBody);
				throw new ServiceCallException("Unauthorized: " + responseBody);
			} else if (statusCode == 403) {
				log.error("Forbidden (403) from external service: {}", responseBody);
				throw new ServiceCallException("Forbidden: " + responseBody);
			} else if (statusCode == 404) {
				log.error("Not Found (404) from external service: {}", responseBody);
				throw new ServiceCallException("Not Found: " + responseBody);
			} else {
				log.error("Client Error ({}) from external service: {}", statusCode, responseBody);
				throw new ServiceCallException("Client Error: " + responseBody);
			}
		} catch (HttpServerErrorException e) {
			int statusCode = e.getStatusCode().value();
			String responseBody = e.getResponseBodyAsString();
			if (statusCode == 500) {
				log.error("Internal Server Error (500) from external service: {}", responseBody);
				throw new ServiceCallException("Internal Server Error: " + responseBody);
			} else {
				log.error("Server Error ({}) from external service: {}", statusCode, responseBody);
				throw new ServiceCallException("Server Error: " + responseBody);
			}
		} catch (Exception e) {
			log.error("Exception while fetching from external service: ", e);
			throw new ServiceCallException("Error calling external service: " + e.getMessage());
		}
	}

	/**
	 * Fetch results using GET request
	 * @param uri URI with query parameters
	 * @return Response object
	 */
	public Object fetchResultUsingGet(StringBuilder uri) {
		Object response = null;
		log.info("URI: " + uri.toString());
		try {
			response = restTemplate.getForObject(uri.toString(), Map.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from service: ", e);
		}
		return response;
	}
}
