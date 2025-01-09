package org.egov.web.notification.sms.service;

public interface SMSService {

	void sendSMS(String number, String userUuid);

}
