package org.upyog.cdwm.calculator.web.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.cdwm.calculator.service.CalculationService;
import org.upyog.cdwm.calculator.service.DemandService;
import org.upyog.cdwm.calculator.util.CalculationUtils;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.util.ResponseInfoFactory;
import org.upyog.cdwm.calculator.web.models.CNDRequest.StatusEnum;
import org.upyog.cdwm.calculator.web.models.CalculationRequest;
import org.upyog.cdwm.calculator.web.models.ResponseInfo;
import org.upyog.cdwm.calculator.web.models.demand.Demand;
import org.upyog.cdwm.calculator.web.models.demand.DemandResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/cnd-calculator")
@Slf4j
public class CalculatorController {

	private ObjectMapper objectMapper;

	private HttpServletRequest request;

	private CalculationService calculationService;

	private DemandService demandService;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	public CalculatorController(ObjectMapper objectMapper, HttpServletRequest request,
			CalculationService calculationService, DemandService demandService) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.calculationService = calculationService;
		this.demandService = demandService;
	}

	/**
	 * Calulates the CND fee and creates Demand
	 * 
	 * @param calculationReq The calculation Request
	 * @return Calculation Response
	 */
	@PostMapping(value = "/v1/_calculate")
	public ResponseEntity<List<DemandResponse>> calculate(
			@ApiParam(value = "Details for the CND application, payment", required = true)
	        @Valid @RequestBody CalculationRequest calculationReq)
	        {

	    log.debug("CalculationRequest:: {}", calculationReq);
	  
	    List<Demand> demands = demandService.createDemand(
	            calculationReq.getRequestInfo(), 
	            calculationReq.getCalulationCriteria());

	    ResponseInfo responseInfo = CalculationUtils.createReponseInfo(
	    		calculationReq.getRequestInfo(), CalculatorConstants.DEMAND_DETAIL_FOUND, StatusEnum.SUCCESSFUL);

	    DemandResponse demandResponse = DemandResponse.builder()
	            .demands(demands)
	            .responseInfo(responseInfo)
	            .build();
	    
	    return new ResponseEntity<>(Collections.singletonList(demandResponse), HttpStatus.OK);
	}

	
}
