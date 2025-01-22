package org.egov.demand.web.controller;

import javax.validation.Valid;

import org.egov.demand.service.BillSchedulerService;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bill-scheduler")
public class BillSchedulerController {

	@Autowired
	private BillSchedulerService billSchedulerService;

	@PostMapping("/bill-expiry")
	public ResponseEntity<?> expireEligibleBill(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper) {

		billSchedulerService.expireEligibleBill(requestInfoWrapper);

		return ResponseEntity.ok("Eligible Bill expired successfully!!!");
	}

}
