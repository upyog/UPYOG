package org.egov.web.notification.sms.service;

import org.egov.web.notification.sms.models.SMSSentRequest;

public interface SMSService {

	void sendSMS(SMSSentRequest smsSentRequest);

}
