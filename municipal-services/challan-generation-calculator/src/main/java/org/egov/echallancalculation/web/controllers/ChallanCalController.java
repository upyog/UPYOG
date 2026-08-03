package org.egov.echallancalculation.web.controllers;


import java.util.List;

import jakarta.validation.Valid;

import org.egov.echallancalculation.service.CalculationService;
import org.egov.echallancalculation.web.models.calculation.Calculation;
import org.egov.echallancalculation.web.models.calculation.CalculationReq;
import org.egov.echallancalculation.web.models.calculation.CalculationRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChallanCalController {

	private final CalculationService calculationService;

	@PostMapping("/_calculate")
	public ResponseEntity<CalculationRes> calculate(@Valid @RequestBody CalculationReq calculationReq) {

		List<Calculation> calculations = calculationService.getCalculation(calculationReq);

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return new ResponseEntity<>(calculationRes, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<CalculationRes> updateCalculation(@Valid @RequestBody CalculationReq calculationReq) {

		List<Calculation> calculations = calculationService.updateCalculation(calculationReq);

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return new ResponseEntity<>(calculationRes, HttpStatus.OK);
	}


}
