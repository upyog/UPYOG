package org.egov.pgr.web.controllers;

import org.egov.pgr.service.PGRSchedulerService;
import org.egov.pgr.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pgr-scheduler")
public class PGRSchedulerController {

	@Autowired
	private PGRSchedulerService service;

	@PostMapping("/request-escalator")
	public ResponseEntity<?> requestEscalator(@RequestBody RequestInfoWrapper requestInfoWrapper) {
		service.escalateRequest(requestInfoWrapper);
		return ResponseEntity.ok("Request Escalated Successfully!!!");
	}

	@PostMapping("/notification-sender")
	public ResponseEntity<?> sendNotification(@RequestBody RequestInfoWrapper requestInfoWrapper) {
		service.sendNotification(requestInfoWrapper);
		return ResponseEntity.ok("Notification Send Successfully!!!");
	}

	@PostMapping("/delete-notification")
	public ResponseEntity<?> deleteNotification(@RequestBody RequestInfoWrapper requestInfoWrapper) {
		service.deleteNotification(requestInfoWrapper);
		return ResponseEntity.ok("Notification Deleted Successfully!!!");
	}

}
