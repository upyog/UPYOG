package org.upyog.chb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandRequest;
import org.upyog.chb.web.models.billing.DemandResponse;

import java.util.List;

/**
 * This class is responsible for interacting with the billing service to manage demands
 * related to the Community Hall Booking module.
 * 
 * Purpose:
 * - To create and retrieve demands for community hall bookings.
 * - To handle communication with external services for demand-related operations.
 * 
 * Dependencies:
 * - ServiceRequestRepository: Used to send HTTP requests to external services.
 * - CommunityHallBookingConfiguration: Provides configuration properties for demand operations.
 * - ObjectMapper: Used to serialize and deserialize JSON objects.
 * 
 * Features:
 * - Provides methods to create demands for bookings.
 * - Handles exceptions and ensures proper error reporting using CustomException.
 * - Uses configuration properties to determine service endpoints and other settings.
 * 
 * Methods:
 * 1. saveDemand:
 *    - Creates new demands for community hall bookings.
 *    - Sends the demand creation request to the billing service.
 * 
 * Usage:
 * - This class is used by the service layer to manage demands for bookings.
 * - It ensures consistent and reusable logic for demand-related operations.
 */
@Repository
public class DemandRepository {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Creates demand
	 * 
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @param demands     The demands to be created
	 * @return The list of demand created
	 */
	public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demand) {
		StringBuilder url = new StringBuilder(config.getBillingHost());
		url.append(config.getDemandCreateEndpoint());
		DemandRequest request = new DemandRequest(requestInfo, demand);
		System.out.println("Request object for fetchResult: " + request);
		System.out.println("URL for fetchResult: " + url);
		Object result = serviceRequestRepository.fetchResult(url, request);
		System.out.println("Result from fetchResult method: " + result);
		DemandResponse response = null;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
			System.out.println("Demand response mapper: " + response);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of create demand");
		}
		return response.getDemands();
	}

	/**
	 * Updates the demand
	 * 
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @param demands     The demands to be updated
	 * @return The list of demand updated
	 */
	public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands) {
		StringBuilder url = new StringBuilder(config.getBillingHost());
		url.append(config.getDemandUpdateEndpoint());
		DemandRequest request = new DemandRequest(requestInfo, demands);
		Object result = serviceRequestRepository.fetchResult(url, request);
		DemandResponse response = null;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of update demand");
		}
		return response.getDemands();

	}

}
