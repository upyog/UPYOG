package org.egov.asset.calculator.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.asset.calculator.services.CalculationService;
import org.egov.asset.calculator.services.ProcessDepreciation;
import org.egov.asset.calculator.utils.ResponseInfoFactory;
import org.egov.asset.calculator.web.models.Calculation;
import org.egov.asset.calculator.web.models.CalculationReq;
import org.egov.asset.calculator.web.models.CalculationRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class CalculatorController {

	private ProcessDepreciation depreciationService;
	private final CalculationService calculationService;

	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	public CalculatorController(CalculationService calculationService) {
		this.calculationService = calculationService;
	}

	/**
	 * API to trigger depreciation calculation for a specific asset or all assets.
	 *
	 * @param calculationReq (Required) The request payload for depreciation calculation.
	 * @return CalculationRes with calculated results.
	 */
	@PostMapping("/_calculate")
	public ResponseEntity<CalculationRes> calculate(@Valid @RequestBody CalculationReq calculationReq) {
		CalculationRes calculationRes = calculationService.calculate(calculationReq);
		return new ResponseEntity<>(calculationRes, HttpStatus.OK);
	}
}
