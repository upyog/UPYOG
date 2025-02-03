package org.egov.pg.repository;

import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import java.util.Optional;

import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.egov.pg.config.AppProperties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServiceCallRepository {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
    private AppProperties appProperties;


	/**
	 * Fetches results from a REST service using the uri and object
	 * 
	 * @param requestInfo
	 * @param serviceReqSearchCriteria
	 * @return Object
	 * @author vishal
	 */
	public Optional<Object> fetchResult(String uri, Object request) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		try {
			response = restTemplate.postForObject(uri, request, JsonNode.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from external service: ", e);
			throw new CustomException("ERROR_EXTERNAL_API", "Exception while fetching from external service: ");
		}

		return Optional.ofNullable(response);

	}
	
	public MdmsResponse getMdmsMasterData(MdmsCriteriaReq mdmsCriteriaReq) {


		MdmsResponse response = null;
		try {
			StringBuilder mdmsv2Endpoint = new StringBuilder().append(appProperties.getMdmsV2host())
					.append(appProperties.getMdmsV2Endpoint());
			response = restTemplate.postForObject(mdmsv2Endpoint.toString(), mdmsCriteriaReq, MdmsResponse.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}
		return response;
	}

}