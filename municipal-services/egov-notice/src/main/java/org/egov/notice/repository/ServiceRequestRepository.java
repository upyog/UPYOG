package org.egov.notice.repository;

import java.util.List;
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
	private ObjectMapper mapper;

	private RestTemplate restTemplate;

	@Autowired
	public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
		this.mapper = mapper;
		this.restTemplate = restTemplate;
	}
	/**
	 * fetchResult form the different services based on the url and request object
	 * @param uri
	 * @param request
	 * @return
	 */
	public Optional<Object> fetchResult(StringBuilder uri, Object request) {

		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		log.info("URI: "+uri.toString());
		try {
			log.info("Request: "+mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			
			log.error("Exception while fetching from external service: ", e);
			throw new CustomException("REST_CALL_EXCEPTION : "+uri.toString(),e.getMessage());
		}
		return Optional.ofNullable(response);
	}
	
	/**
	 * fetchResult form the different services based on the url and request object
	 * @param uri
	 * @param request
	 * @return
	 */
	public List fetchListResult(StringBuilder uri, Object request) {
		List response = null;
		//log.debug("URI: " + uri.toString());
		try {
			//log.debug("Request: " + mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, List.class);
		} catch (HttpClientErrorException e) {
			//log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			//log.error("Exception while fetching from searcher: ", e);
			throw new ServiceCallException(e.getMessage());
		}

		return response;
	}
	/**
	 * fetchResult form the different services based on the url and request object
	 * @param uri
	 * @param request
	 * @return
	 */
	public Integer fetchIntResult(StringBuilder uri, Object request) {
		Integer response = null;
		//log.debug("URI: " + uri.toString());
		try {
			//log.debug("Request: " + mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Integer.class);
		} catch (HttpClientErrorException e) {
			//log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			//log.error("Exception while fetching from searcher: ", e);
			throw new ServiceCallException(e.getMessage());
		}

		return response;
	}
	
	
	public Optional<Object> fetchResultNew(StringBuilder uri, Object request) {

		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		log.info("URI: "+uri.toString());
		try {
			log.info("Request: "+mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			
			log.error("Exception while fetching from external service: ", e);
			throw new CustomException("REST_CALL_EXCEPTION : "+uri.toString(),e.getMessage());
		}
		return Optional.ofNullable(response);
	}

}
