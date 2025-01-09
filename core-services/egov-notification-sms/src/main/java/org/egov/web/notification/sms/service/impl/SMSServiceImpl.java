package org.egov.web.notification.sms.service.impl;

import org.egov.tracer.model.CustomException;
import org.egov.web.notification.sms.config.SMSProperties;
import org.egov.web.notification.sms.models.SMSSentRequest;
import org.egov.web.notification.sms.models.Sms;
import org.egov.web.notification.sms.models.UserSearchResponse;
import org.egov.web.notification.sms.service.BaseSMSService;
import org.egov.web.notification.sms.service.OtpService;
import org.egov.web.notification.sms.service.SMSService;
import org.egov.web.notification.sms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SMSServiceImpl implements SMSService {

	@Autowired
	private BaseSMSService baseSmsService;

	@Autowired
	private SMSProperties smsProperties;

	@Autowired
	private UserService userService;

	@Autowired
	private OtpService otpService;

	private static final String OTP_PLACEHOLDER = "{otp}";

	private static final String SMS_BODY = OTP_PLACEHOLDER
			+ " is One Time Password for RTI Portal. State Information Commission Himachal Pradesh";

	@Override
	public void sendSMS(SMSSentRequest smsSentRequest) {

		if (!smsSentRequest.isValid()) {
			throw new CustomException("INVALID REQUEST", "Mobile number or User uuid is missing.");
		}

		// validate user
		UserSearchResponse userSearchResponse = userService.searchUser(smsSentRequest.getUserUuid());

		if (null == userSearchResponse || CollectionUtils.isEmpty(userSearchResponse.getUserSearchResponseContent())) {
			throw new CustomException("USER NOT FOUND", "User not found for given user uuid.");
		}

		// get otp from otp service
		String otp = otpService.createOtp(smsSentRequest.getUserUuid());
		String smsBody = SMS_BODY;
		smsBody = smsBody.replace(OTP_PLACEHOLDER, otp);

		Sms sms = Sms.builder().mobileNumber(smsSentRequest.getNumber()).message(smsBody)
				.templateId(smsProperties.getSmsDefaultTmplid()).build();

		baseSmsService.sendSMS(sms);
	}

}
