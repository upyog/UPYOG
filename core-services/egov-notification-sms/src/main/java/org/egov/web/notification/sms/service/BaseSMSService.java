package org.egov.web.notification.sms.service;

import org.egov.web.notification.sms.models.Sms;

public interface BaseSMSService {
    void sendSMS(Sms sms);
}

