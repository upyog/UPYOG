package org.egov.web.notification.sms.controller;

import org.egov.web.notification.sms.models.OTPSentRequest;
import org.egov.web.notification.sms.models.SMSSentRequest;
import org.egov.web.notification.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class SMSController {

	@Autowired
	SMSService smsService;

	@PostMapping(value = "/otp")
	public ResponseEntity<?> sendOtp(@RequestBody OTPSentRequest otpSentRequest) {

		smsService.sendOtp(otpSentRequest);

        return ResponseEntity.ok().body("{\"message\":\"Message sent successfully\"}");
	}

	@PostMapping(value = "/sms")
	public ResponseEntity<?> sendSMS(@RequestBody SMSSentRequest smsSentRequest) {

		smsService.sendSMS(smsSentRequest);

		return ResponseEntity.ok().body("Message sent successfully");
	}

}