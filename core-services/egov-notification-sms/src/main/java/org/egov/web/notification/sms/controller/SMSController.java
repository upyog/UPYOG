package org.egov.web.notification.sms.controller;

import org.egov.common.contract.request.RequestInfo;
import org.egov.web.notification.sms.models.OTPSentRequest;
import org.egov.web.notification.sms.models.SMSSentRequest;
import org.egov.web.notification.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.egov.web.notification.sms.models.SmsTracker;
import lombok.extern.slf4j.Slf4j;
import org.egov.web.notification.sms.service.SmsTrackerService;



@Slf4j
@RestController
@RequestMapping()
public class SMSController {

	@Autowired
	SMSService smsService;
	
	@Autowired
	private SmsTrackerService smsTrackerService;

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
	
	 @PostMapping("/_create")
	    public ResponseEntity<String> create(@RequestBody SmsTracker tracker) {
	        log.info("Creating SMS Tracker entry for applicationNo: {}", tracker.getApplicationNo());
	        smsTrackerService.createSmsTracker(tracker);
	        return ResponseEntity.ok("SMS Tracker entry created successfully");
	    }
	 
}