package org.egov.refund.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.idgen.IdGenerationRequest;
import org.egov.common.contract.idgen.IdGenerationResponse;
import org.egov.common.contract.idgen.IdRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.config.ApplicationProperties;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * This class is responsible for interacting with the ID generation service to
 * generate unique IDs for various entities in the Community Hall Booking
 * module.
 * 
 * Purpose: - To generate unique identifiers for bookings, slots, and other
 * entities. - To handle communication with the external ID generation service.
 * 
 * Dependencies: - RestTemplate: Used to send HTTP requests to the ID generation
 * service. - CommunityHallBookingConfiguration: Provides configuration
 * properties such as service URLs.
 * 
 * Features: - Provides methods to create ID generation requests and process
 * responses. - Handles exceptions such as HttpClientErrorException and
 * ServiceCallException. - Uses configuration properties to determine service
 * endpoints and other settings.
 * 
 * Constructor: - Accepts RestTemplate and CommunityHallBookingConfiguration as
 * dependencies.
 * 
 * Methods: 1. getIdGenerationResponse: - Sends a request to the ID generation
 * service to generate unique IDs. - Processes the response and returns the
 * generated IDs.
 * 
 * Usage: - This class is used by the service layer to generate unique IDs for
 * various entities. - It ensures consistent and reusable logic for ID
 * generation across the application.
 */
@Repository
public class IdGenRepository {

	private RestTemplate restTemplate;

	private ApplicationProperties config;

	@Autowired
	public IdGenRepository(RestTemplate restTemplate, ApplicationProperties config) {
		this.restTemplate = restTemplate;
		this.config = config;
	}

	/**
	 * Call iDgen to generateIds
	 * 
	 * @param requestInfo The rquestInfo of the request
	 * @param tenantId    The tenantiD of the service request
	 * @param name        Name of the foramt
	 * @param format      Format of the ids
	 * @param count       Total Number of idGen ids required
	 * @return
	 */
	public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format) {

		List<IdRequest> reqList = new ArrayList<>();
		reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
		IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
		IdGenerationResponse response = null;
		try {
			response = restTemplate.postForObject(config.getIdgenHost() + config.getIdgenEndpoint(), req,
					IdGenerationResponse.class);
		} catch (HttpClientErrorException e) {
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			Map<String, String> map = new HashMap<>();
			map.put(e.getCause().getClass().getName(), e.getMessage());
			throw new CustomException(map);
		}
		return response;
	}

}
