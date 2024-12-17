package org.egov.garbageservice.controller;

import org.egov.garbageservice.service.GarbageAccountSchedulerService;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garbage-accounts-scheduler")
public class GarbageAccountSchedulerController {

	@Autowired
	private GarbageAccountSchedulerService service;

	@PostMapping("/bill-generator")
	public ResponseEntity<?> billGenerator(@RequestBody RequestInfoWrapper requestInfoWrapper) {
		service.generateBill(requestInfoWrapper);
		return ResponseEntity.ok("Bill generated successfully!!!");
	}

}
