package org.upyog.chb.repository;


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

    private ObjectMapper mapper;

    private RestTemplate restTemplate;


    @Autowired
    public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }


    public Optional<Object> fetchResult(StringBuilder uri, Object request) {
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