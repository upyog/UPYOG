package org.egov.web.notification.sms.controller;

import org.egov.web.notification.sms.models.SMSSentRequest;
import org.egov.web.notification.sms.models.SmsTracker;
import org.egov.web.notification.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.egov.web.notification.sms.service.SmsProcessing;
import org.egov.web.notification.sms.service.SmsTrackerService;

@RestController
@RequestMapping()
public class NotificationSmsSchedulerController {

    @Autowired
    private SmsProcessing smsProcessing;
    
    @Autowired
	private SmsTrackerService smsTrackerService;

    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSMS(
            @RequestBody(required = false) SMSSentRequest smsSentRequest) {

        String billId = smsSentRequest != null ? smsSentRequest.getBillId() : null;

        if (billId != null && !billId.trim().isEmpty()) {
            smsProcessing.processSmsForBill(billId);
            return ResponseEntity.ok("SMS processing triggered for billId: " + billId);
        } else {
            smsProcessing.processPendingSms();
            return ResponseEntity.ok("SMS processing triggered for all pending messages");
        }
    }

    
	 @PostMapping("/resend-counter")
	 public ResponseEntity<?> getResendCounter(@RequestBody SmsTracker tracker) {
	     String billId = tracker.getBillId();
	     Short resendCounter = smsTrackerService.getResendCounterByBillId(billId);
	     return ResponseEntity.ok(resendCounter);
	 }
}

