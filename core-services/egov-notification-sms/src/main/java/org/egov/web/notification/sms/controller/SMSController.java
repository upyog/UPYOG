package org.egov.web.notification.sms.controller;

import org.egov.web.notification.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class SMSController {

	@Autowired
	SMSService smsService;

	@GetMapping
	public ResponseEntity<?> sendSms(@RequestParam(value = "number", required = true) String number,
			@RequestParam(value = "userUuid", required = true) String userUuid) {

		smsService.sendSMS(number, userUuid);

		return ResponseEntity.ok().body("Message sent successfully");
	}

}