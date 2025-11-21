package org.upyog.cdwm.calculator.service;

import java.util.LinkedHashMap;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.repository.ServiceRequestRepository;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.web.models.CNDApplicationDetail;
import org.upyog.cdwm.calculator.web.models.CNDResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;

/**
 * Service class responsible for handling CND (Collection and Disposal) applications.
 * This class interacts with external services to fetch CND application details.
 */

@Service
public class CNDService {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CalculatorConfig config;

	/**
     * Fetches the CND application details for the given application number.
     *
     * @param requestInfo  The request information containing metadata and authentication details.
     * @param tenantId     The tenant ID for which the application is being searched.
     * @param applicationNo The application number of the CND request.
     * @return The {@link CNDApplicationDetail} object containing the application details.
     * @throws CustomException If parsing fails or the application is not found.
     */
	public CNDApplicationDetail getCNDApplication(RequestInfo requestInfo, String tenantId, String applicationNo) {
		StringBuilder url = getCNDSearchURL();
		
		url.append("applicationNumber=");
		url.append(applicationNo);

		LinkedHashMap responseMap = null;
		responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(url, new RequestInfoWrapper(requestInfo));

		CNDResponse cndResponse = null;

		try {
			cndResponse = mapper.convertValue(responseMap, CNDResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException(CalculatorConstants.PARSING_ERROR,
					"Error while parsing response of CND Application Search");
		}

		if (cndResponse.getCndRequest().isEmpty()) {
			throw new CustomException(CalculatorConstants.APPLICATION_NOT_FOUND,
					"Application with applicationNo: " + applicationNo + " not found !");
		}

		return cndResponse.getCndRequest().get(0);
	}

	 /**
     * Constructs the base URL for fetching CND applications.
     *
     * @return A {@link StringBuilder} containing the base URL for CND search.
     */
	
	private StringBuilder getCNDSearchURL() {
		StringBuilder url = new StringBuilder(config.getCndHost());
		url.append(config.getCndContextPath());
		url.append(config.getCndSearchEndpoint());
		url.append("?");
		return url;
	}
}
