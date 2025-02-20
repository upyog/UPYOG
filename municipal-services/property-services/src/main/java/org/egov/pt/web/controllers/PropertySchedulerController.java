package org.egov.pt.web.controllers;

import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.service.PropertySchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property-scheduler")
public class PropertySchedulerController {

	@Autowired
	private PropertySchedulerService service;

	@PostMapping("/tax-calculator")
	public ResponseEntity<?> taxCalculator(@RequestBody CalculateTaxRequest calculateTaxRequest) {

//		service.calculateTax(calculateTaxRequest);
//
//		return ResponseEntity.ok("Tax calculated successfully!!!");
		return ResponseEntity.ok(service.calculateTax(calculateTaxRequest));
	}

}
