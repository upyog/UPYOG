package org.egov.pt.web.controllers;

import org.egov.pt.models.CalculateTaxRequest;
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
	public ResponseEntity<?> taxCalculator(@RequestBody CalculateTaxRequest calculateTaxRequest) {

//		service.calculateTax(calculateTaxRequest);
//
//		return ResponseEntity.ok("Tax calculated successfully!!!");
		return ResponseEntity.ok(service.calculateTax(calculateTaxRequest));
	}

	@PostMapping("/update-tracker-bill-status")
	public ResponseEntity<?> updateTrackerBillStatus(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.updateTrackerBillStatus(requestInfoWrapper);

		return ResponseEntity.ok("Tracker bill status updated successfully!!!");
//		return ResponseEntity.ok(service.updateTrackerBillStatus(requestInfoWrapper));
	}

	@PostMapping("/reverse-rebate-amount")
	public ResponseEntity<?> reverseRebateAmount(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.reverseRebateAmount(requestInfoWrapper);

		return ResponseEntity.ok("Rebate amount reversed successfully!!!");
//		return ResponseEntity.ok(service.reverseRebateAmount(requestInfoWrapper));
	}

}
