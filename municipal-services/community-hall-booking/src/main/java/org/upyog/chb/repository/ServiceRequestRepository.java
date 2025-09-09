package org.upyog.chb.repository;


import java.util.Map;

import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for making HTTP requests to external services and fetching
 * responses for the Community Hall Booking module.
 * 
 * Purpose:
 * - To handle communication with external services by sending HTTP requests and processing responses.
 * - To provide a reusable mechanism for making service calls across the application.
 * 
 * Dependencies:
 * - RestTemplate: Used to send HTTP requests to external services.
 * - ObjectMapper: Used to serialize and deserialize JSON objects for requests and responses.
 * 
 * Features:
 * - Provides methods to send POST requests with a request body and fetch results.
 * - Configures the ObjectMapper to handle empty beans gracefully during serialization.
 * - Logs request and response details for debugging and monitoring purposes.
 * - Handles exceptions such as HttpClientErrorException and ServiceCallException.
 * 
 * Constructor:
 * - Accepts ObjectMapper and RestTemplate as dependencies and initializes them.
 * 
 * Methods:
 * 1. fetchResult:
 *    - Sends a POST request to the specified URI with the given request object.
 *    - Returns the response from the external service as an Object.
 * 
 * Usage:
 * - This class is used by various repository and service classes to interact with external APIs.
 * - It ensures consistent and reusable logic for making service requests across the application.
 */
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


    public Object fetchResult(StringBuilder uri, Object request) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        try {
        	log.info("request info : {} and uri : {}", request, uri);
            response = restTemplate.postForObject(uri.toString(), request, Map.class);
            log.info("response info : "+ response);
            
        }catch(HttpClientErrorException e) {
            log.error("External Service threw an Exception: ",e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        }catch(Exception e) {
            log.error("Exception while fetching from searcher: ",e);
        }

        return response;
    }
    
    public String getShorteningURL(StringBuilder uri, Object request) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		String response = null;
		/*
		 * StringBuilder strq = new
		 * StringBuilder(this.getClass().getCanonicalName()).append(".fetchResult:")
		 * .append(System.lineSeparator());
		 * str.append("URI: ").append(uri.toString()).append(System.lineSeparator());
		 */
		try {
			//log.info("Url shortener url : " +  str.toString());
			log.info("request info : "+ request + " uri : " + uri);
			response = restTemplate.postForObject(uri.toString(), request, String.class);
			log.info("response info : "+ response);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}
		return response;
	}
}