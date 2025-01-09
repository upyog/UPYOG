package org.egov.web.notification.sms.service.impl;

import org.egov.tracer.model.CustomException;
import org.egov.web.notification.sms.config.SMSProperties;
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
	public void sendSMS(String number, String userUuid) {

		// validate user
		UserSearchResponse userSearchResponse = userService.searchUser(userUuid);

		if (null == userSearchResponse || CollectionUtils.isEmpty(userSearchResponse.getUserSearchResponseContent())) {
			throw new CustomException("USER NOT FOUND", "User not found for given user uuid.");
		}

		// get otp from otp service
		String otp = otpService.createOtp(userUuid);
		String smsBody = SMS_BODY;
		smsBody = smsBody.replace(OTP_PLACEHOLDER, otp);

		Sms sms = Sms.builder().mobileNumber(number).message(smsBody).templateId(smsProperties.getSmsDefaultTmplid())
				.build();

		baseSmsService.sendSMS(sms);
	}

}
