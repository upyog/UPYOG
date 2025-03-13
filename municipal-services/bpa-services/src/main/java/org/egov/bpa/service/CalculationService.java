package org.egov.bpa.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.repository.ServiceRequestRepository;
import org.egov.bpa.util.BPAConstants;
import org.egov.bpa.web.model.BPARequest;
import org.egov.bpa.web.model.CalculationReq;
import org.egov.bpa.web.model.CalulationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationService {

	private ServiceRequestRepository serviceRequestRepository;

	private BPAConfiguration config;

	@Autowired
	public CalculationService(ServiceRequestRepository serviceRequestRepository, BPAConfiguration config) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
	}

	/**
	 * add calculation for the bpa object based on the FeeType
	 * @param bpaRequest
	 * @param feeType
	 */
	public void addCalculation(BPARequest bpaRequest, String feeType) {

		CalculationReq calulcationRequest = new CalculationReq();
		calulcationRequest.setRequestInfo(bpaRequest.getRequestInfo());
		CalulationCriteria calculationCriteria = new CalulationCriteria();
		calculationCriteria.setApplicationNo(bpaRequest.getBPA().getApplicationNo());
		calculationCriteria.setBpa(bpaRequest.getBPA());
		calculationCriteria.setFeeType(feeType);
		calculationCriteria.setTenantId(bpaRequest.getBPA().getTenantId());
		List<CalulationCriteria> criterias = Arrays.asList(calculationCriteria);
		calulcationRequest.setCalulationCriteria(criterias);
		StringBuilder url = new StringBuilder();
		url.append(this.config.getCalculatorHost());
		url.append(this.config.getCalulatorEndPoint());

		this.serviceRequestRepository.fetchResult(url, calulcationRequest);
	}

	/**
	 * call bpa-calculator /_estimate API
	 * @param bpaRequest
	 */
	public Object callBpaCalculatorEstimate(Object bpaRequest) { 
		StringBuilder url = new StringBuilder();
		url.append(this.config.getCalculatorHost());
		url.append(this.config.getBpaCalculationEstimateEndpoint());
		return this.serviceRequestRepository.fetchResult(url, bpaRequest);
	}
	

}
