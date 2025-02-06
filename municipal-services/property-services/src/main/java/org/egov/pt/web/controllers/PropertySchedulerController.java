package org.egov.pt.web.controllers;

import org.egov.pt.service.PropertySchedulerService;
import org.egov.pt.web.contracts.RequestInfoWrapper;
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
	public ResponseEntity<?> taxCalculator(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.calculateTax(requestInfoWrapper);

//		return new ResponseEntity<>(service.calculateTax(requestInfoWrapper), HttpStatus.OK);

		return ResponseEntity.ok("Tax calculated successfully!!!");
	}

}
