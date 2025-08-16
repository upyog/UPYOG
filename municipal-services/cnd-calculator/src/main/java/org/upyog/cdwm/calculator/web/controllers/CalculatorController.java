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
import org.upyog.cdwm.calculator.web.models.CNDApplicationDetail.StatusEnum;
import org.upyog.cdwm.calculator.web.models.CalculationRequest;
import org.upyog.cdwm.calculator.web.models.ResponseInfo;
import org.upyog.cdwm.calculator.web.models.demand.Demand;
import org.upyog.cdwm.calculator.web.models.demand.DemandResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling CND (Collection and Demand) calculations.
 */

@RestController
@Slf4j
public class CalculatorController {

	private ObjectMapper objectMapper;

	private HttpServletRequest request;

	private CalculationService calculationService;

	private DemandService demandService;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	  /**
     * Constructor for CalculatorController.
     * 
     * @param objectMapper       ObjectMapper instance for JSON processing.
     * @param request            HttpServletRequest instance.
     * @param calculationService Service to handle calculations.
     * @param demandService      Service to handle demand generation.
     */

	@Autowired
	public CalculatorController(ObjectMapper objectMapper, HttpServletRequest request,
			CalculationService calculationService, DemandService demandService) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.calculationService = calculationService;
		this.demandService = demandService;
	}

	/**
     * Calculates the CND fee and creates demand based on the provided calculation request.
     * 
     * @param calculationReq The calculation request containing application and payment details.
     * @return A ResponseEntity containing a list of DemandResponse objects.
     */
	@PostMapping(value = "/v1/_calculate")
	public ResponseEntity<DemandResponse> calculate(
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
	    
	    return ResponseEntity.ok(demandResponse);

	}

	
}
