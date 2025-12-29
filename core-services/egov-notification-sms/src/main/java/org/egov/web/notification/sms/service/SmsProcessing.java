package org.egov.web.notification.sms.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.egov.web.notification.sms.repository.SmsTrackerRepository;
import java.util.List;
import org.egov.web.notification.sms.models.SmsTracker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.web.notification.sms.models.SMSSentRequest;



@Service
@Slf4j
public class SmsProcessing {
	 @Autowired
	 private SmsTrackerRepository smsTrackerRepository;
	 
	 @Autowired
	 private SMSService smsService;
	 
	 @Autowired
	 private ObjectMapper objectMapper;


	 public void processPendingSms() {

	        List<SmsTracker> pending = smsTrackerRepository.fetchPendingSms();

	        log.info("Pending SMS count: {}", pending.size());

	        for (SmsTracker tracker : pending) {

	            try {
	                if (tracker.getSmsRequest() == null) {
	                    log.warn("SMS request is null for uuid: {}", tracker.getUuid());
	                    continue;
	                }
	                SMSSentRequest smsSentRequest =
	                        objectMapper.treeToValue(
	                                tracker.getSmsRequest(),
	                                SMSSentRequest.class
	                        );

	                log.info("Sending SMS for uuid: {}", tracker.getUuid());

	                smsService.sendSMS(smsSentRequest); //call the method inside send message api
	                smsTrackerRepository.incrementResendCounter(tracker.getUuid());

	                
	                
	                //setting status to true
	                tracker.setSmsStatus(true);
	                smsTrackerRepository.updateSmsStatus(tracker);
	                
	            } catch (Exception e) {
	                log.error("Failed to send SMS for uuid: {}", tracker.getUuid(), e);
	            }
	        }
	    }
	 
	 public void processSmsForBill(String billId) {

		    List<SmsTracker> pending = smsTrackerRepository.fetchPendingSmsForBill(billId);

		    log.info("Pending SMS count for billId {}: {}", billId, pending.size());

		    for (SmsTracker tracker : pending) {
		        try {
		            if (tracker.getSmsRequest() == null) {
		                log.warn("SMS request is null for uuid: {}", tracker.getUuid());
		                continue;
		            }

		            SMSSentRequest smsSentRequest =
		                    objectMapper.treeToValue(tracker.getSmsRequest(), SMSSentRequest.class);

		            log.info("Sending SMS for uuid: {} and billId: {}", tracker.getUuid(), billId);

		            smsService.sendSMS(smsSentRequest);
		            smsTrackerRepository.incrementResendCounter(tracker.getUuid());


		            tracker.setSmsStatus(true);
		            smsTrackerRepository.updateSmsStatus(tracker);

		        } catch (Exception e) {
		            log.error("Failed to send SMS for uuid: {} and billId: {}", tracker.getUuid(), billId, e);
		        }
		    }
		}


}
