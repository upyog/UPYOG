package org.egov.demand.repository;

import java.util.Map;

import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author kavi elrey
 *
 * Generic Repository to make a rest call and return JSON response 
 */
@Repository
@Slf4j
public class ServiceRequestRepository {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplate restTemplate;


	/**
	 * fetch method which takes the URI and request object
	 *  and returns response in generic format
	 * @param uri
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map fetchResult(String uri, Object request) {
		
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map response = null;
		log.info("URI: "+ uri);
		
		try {
			log.info("Request: "+mapper.writeValueAsString(request));
			response = restTemplate.postForObject(uri, request, Map.class);
		}catch(HttpClientErrorException e) {
			log.error("External Service threw an Exception: ",e.getResponseBodyAsString());
			throw new ServiceCallException(e.getResponseBodyAsString());
		}catch(JsonProcessingException e) {
			log.error("Exception while searching user data : ",e);
		}

		return response;
	}
	
	
	public String getShorteningURL(StringBuilder uri, Object request) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		String response = null;
		log.info("getShorteningURL :"+uri);
		StringBuilder str = new StringBuilder(this.getClass().getCanonicalName()).append(".fetchResult:")
				.append(System.lineSeparator());
		str.append("URI: ").append(uri.toString()).append(System.lineSeparator());
		try {
			log.debug(str.toString());
			response = restTemplate.postForObject(uri.toString(), request, String.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}
		log.info(" response "+ response);
		return response;
	}

}