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

	public void addCalculationV2(BPARequest bpaRequest, String feeType, String applicationType, String serviceType) {
	    log.info("Inside method addCalculationV2 with BPARequest: {}, feeType: {}, applicationType: {}, serviceType: {}", 
	             bpaRequest, feeType, applicationType, serviceType);

	    CalculationReq calculationRequest = new CalculationReq();
	    calculationRequest.setRequestInfo(bpaRequest.getRequestInfo());
	    log.info("Set RequestInfo in CalculationReq: {}", bpaRequest.getRequestInfo());

	    CalulationCriteria calculationCriteria = new CalulationCriteria();
	    calculationCriteria.setApplicationNo(bpaRequest.getBPA().getApplicationNo());
	    calculationCriteria.setBpa(bpaRequest.getBPA());
	    calculationCriteria.setFeeType(feeType);
	    calculationCriteria.setApplicationType(applicationType);
	    calculationCriteria.setServiceType(serviceType);
	    calculationCriteria.setTenantId(bpaRequest.getBPA().getTenantId());

	    log.info("Initialized CalculationCriteria with applicationNo: {}, feeType: {}, tenantId: {}",
	             calculationCriteria.getApplicationNo(), calculationCriteria.getFeeType(), calculationCriteria.getTenantId());


	    List<CalulationCriteria> criterias = Arrays.asList(calculationCriteria);
	    calculationRequest.setCalulationCriteria(criterias);
	    log.info("Final CalculationRequest prepared: {}", calculationRequest);

	    StringBuilder url = new StringBuilder();
	    url.append(this.config.getCalculatorHost());

	    log.info("Evaluating conditions for endpoint selection:");
	    log.info("Fee Type: {}", feeType);
	    log.info("Application Type: {}", applicationType);
	    url.append(this.config.getCalulatorEndPoint());
	    log.info("Using Calculator Endpoint: {}", url);
	    log.info("Calling service request repository with URL: {}", url);
	    this.serviceRequestRepository.fetchResult(url, calculationRequest);
	}
	

}
