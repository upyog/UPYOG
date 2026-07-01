package org.egov.pgr.service;

import java.util.HashMap;
import java.util.Map;

import org.egov.pgr.config.PGRConfiguration;
import org.egov.pgr.web.models.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
@Slf4j
public class NayanAPIService {

	private PGRConfiguration config;
	
	private ObjectMapper mapper;

	private RestTemplate restTemplate;
	
	@Autowired
	public NayanAPIService(PGRConfiguration config, RestTemplate restTemplate, ObjectMapper mapper) {
		this.config = config;
		this.restTemplate = restTemplate;
		this.mapper = mapper;
	}
	
	public void updateStatus(ServiceRequest serviceRequest) {
		StringBuilder uri = new StringBuilder(config.getNayanAIHost());
		uri.append(config.getNayanAIStatusUpdateEndPoint());
		Map<String, String> body = new HashMap<String, String>();
		body.put("pmidc_complaint_number", serviceRequest.getService().getServiceRequestId());
		body.put("pmidc_status", serviceRequest.getService().getApplicationStatus());
		
		Object response = fetchResult(uri, body);
		
		log.info("Respnse from Nayan API for status update: " + response);
	}
	
	private Object fetchResult(StringBuilder uri, Map<String, String> requestBody) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("API-KEY", config.getNayanAIKey());
		
		HttpEntity<Map<String, String>> request = new HttpEntity<Map<String,String>>(requestBody, headers);
		try {
			response = restTemplate.postForEntity(uri.toString(), request, Map.class);
		}catch(HttpClientErrorException e) {
			log.error("External Service threw an Exception: ",e);
		}catch(Exception e) {
			log.error("Exception while fetching from searcher: ",e);
		}

		return response;
	}
	
}
