package org.upyog.cdwm.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.repository.ServiceRequestRepository;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CalculationRequest;
import org.upyog.cdwm.web.models.CalulationCriteria;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CalculationService {
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CNDConfiguration config;
	
	
	/**
	 * Triggers the calculation process for a given CND application request by preparing
	 * a {@link CalculationRequest} and invoking the calculator service endpoint.
	 *
	 * <p>This method creates a {@link CalulationCriteria} based on the input
	 * {@link CNDApplicationRequest} and calls the external calculation service using the
	 * configured URL.</p>
	 *
	 * @param request The {@link CNDApplicationRequest} object containing request info and
	 *                CND application data needed for calculation.
	 */
	
	public void addCalculation(CNDApplicationRequest request) {

		CalculationRequest calulcationRequest = new CalculationRequest();
		calulcationRequest.setRequestInfo(request.getRequestInfo());
		CalulationCriteria calculationCriteria = new CalulationCriteria();
	
		List<CalulationCriteria> criterias = Arrays.asList(calculationCriteria);
		calulcationRequest.setCalulationCriteria(criterias);
		calculationCriteria.setCndRequest(request.getCndApplication());
		StringBuilder url = new StringBuilder();
		url.append(config.getCalculatorHost());
		url.append(config.getCalulatorEndPoint());
		
		log.info("Sending calculation request to URL: {}", url);
		log.debug("CalculationRequest Payload: {}", calulcationRequest);


		Object response = serviceRequestRepository.fetchResult(url, calulcationRequest);
		log.debug("Calculation service response: {}", response);
	}

}
