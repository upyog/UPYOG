package org.egov.web.notification.sms.service.impl;

import org.egov.web.notification.sms.models.Sms;
import org.egov.web.notification.sms.service.BaseSMSService;
import org.egov.web.notification.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SMSServiceImpl implements SMSService {

	@Autowired
	private BaseSMSService baseSmsService;

	@Override
	public void sendSMS(Sms sms) {

		baseSmsService.sendSMS(sms);
	}

}
