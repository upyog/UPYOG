package org.egov.web.notification.sms.service;

import org.egov.web.notification.sms.models.SmsTracker;
import org.egov.web.notification.sms.repository.SmsTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsTrackerService {

    @Autowired
    private SmsTrackerRepository repository;

    public void createSmsTracker(SmsTracker tracker) {
        repository.insert(tracker);
    }
    
    public Short getResendCounterByBillId(String billId) {
        return repository.fetchResendCounterByBillId(billId);
    }

}
