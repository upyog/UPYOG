package org.egov.pg.web.controllers;

import org.egov.pg.service.TransactionsSchedulerService;
import org.egov.pg.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction-scheduler")
public class TransactionsSchedulerController {

	@Autowired
	private TransactionsSchedulerService transactionsSchedulerService;

	@PostMapping("/transfer-amount")
	public ResponseEntity<?> transferAmount(@RequestBody RequestInfoWrapper requestInfoWrapper) {

//		transactionsSchedulerService.transferAmount(requestInfoWrapper);

//		return ResponseEntity.ok("Amount transfered successfully!!!");
		return ResponseEntity.ok(transactionsSchedulerService.transferAmount(requestInfoWrapper));
	}
}
