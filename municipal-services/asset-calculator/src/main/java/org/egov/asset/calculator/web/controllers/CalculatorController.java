package org.egov.asset.calculator.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.asset.calculator.services.CalculationService;
import org.egov.asset.calculator.services.ProcessDepreciation;
import org.egov.asset.calculator.services.ProcessDepreciationV2;
import org.egov.asset.calculator.utils.ResponseInfoFactory;
import org.egov.asset.calculator.web.models.*;
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

	private final ProcessDepreciation depreciationService;
	private final ProcessDepreciationV2 depreciationServiceV2;
	private final CalculationService calculationService;
	private final ResponseInfoFactory responseInfoFactory;

	@Autowired
	public CalculatorController(ProcessDepreciation depreciationService, ProcessDepreciationV2 depreciationServiceV2, CalculationService calculationService, ResponseInfoFactory responseInfoFactory) {
        this.depreciationService = depreciationService;
        this.depreciationServiceV2 = depreciationServiceV2;
        this.calculationService = calculationService;
        this.responseInfoFactory = responseInfoFactory;
    }

	/**
	 * API to trigger depreciation calculation for a specific asset or all assets.
	 *
	 * @param calculationReq (Required) The request payload for depreciation calculation.
	 * @return CalculationRes with calculated results.
	 */
	@PostMapping("/depreciation/_calculate")
	public ResponseEntity<CalculationRes> calculate(@Valid @RequestBody CalculationReq calculationReq) {
		CalculationRes calculationRes = calculationService.calculate(calculationReq);
		log.info("Depreciation calculation response: {}", calculationRes.getMessage());
		CalculationRes response = CalculationRes.builder()
				.message(calculationRes.getMessage())
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(), true))
				.build();
		// Log the response for debugging

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * API to fetch all depreciation details for a specific asset.
	 *
	 * @param assetId (Required) The ID of the asset for which depreciation details are required.
	 * @return List of depreciation details for the provided assetId.
	 */
	@GetMapping("/depreciation/{assetId}/details")
	public ResponseEntity<DepreciationRes> getDepreciationDetails(@PathVariable String assetId) {
		List<DepreciationDetail> depreciationDetails = depreciationService.getDepreciationDetails(assetId);
		// Build the response object
		DepreciationRes response = DepreciationRes.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(null, true))
				.depreciation(depreciationDetails)
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
