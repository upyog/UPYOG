package org.upyog.cdwm.calculator.web.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.cdwm.calculator.service.DemandService;
import org.upyog.cdwm.calculator.util.CalculationUtils;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.web.models.CalculationRequest;
import org.upyog.cdwm.calculator.web.models.ResponseInfo;
import org.upyog.cdwm.calculator.web.models.demand.Demand;
import org.upyog.cdwm.calculator.web.models.demand.DemandResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling CND (Collection and Demand) calculations.
 */

@RestController
@Slf4j
@Tag(name = "Cnd Calculator", description = "Cnd Calculator operations")
public class CalculatorController {

	private final DemandService demandService;

	/**
     * Constructor for CalculatorController.
     *
     * @param demandService Service to handle demand generation.
     */
	public CalculatorController(DemandService demandService) {
		this.demandService = demandService;
	}

	/**
     * Calculates the CND fee and creates demand based on the provided calculation request.
     *
     * @param calculationReq The calculation request containing application and payment details.
     * @return A ResponseEntity containing a list of DemandResponse objects.
     */
	@PostMapping(value = "/v1/_calculate")
	@Operation(summary = "Calculate Fee", description = "Calculate the Fee and genearte the Demand")
	public ResponseEntity<DemandResponse> calculate(
	        @Valid @RequestBody CalculationRequest calculationReq)
	        {

	    log.debug("CalculationRequest:: {}", calculationReq);
	  
	    List<Demand> demands = demandService.createDemand(
	            calculationReq.getRequestInfo(), 
	            calculationReq.getCalulationCriteria());

	    ResponseInfo responseInfo = CalculationUtils.createReponseInfo(
	    		calculationReq.getRequestInfo(), CalculatorConstants.DEMAND_DETAIL_FOUND);

	    DemandResponse demandResponse = DemandResponse.builder()
	            .demands(demands)
	            .responseInfo(responseInfo)
	            .build();
	    
	    return ResponseEntity.ok(demandResponse);

	}

	
}
