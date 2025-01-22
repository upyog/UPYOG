package org.egov.web.notification.sms.service.impl;

import java.util.Arrays;
import java.util.List;

import org.egov.tracer.model.CustomException;
import org.egov.web.notification.sms.constants.SMSConstants;
import org.egov.web.notification.sms.consumer.contract.SMSRequest;
import org.egov.web.notification.sms.models.OTPSentRequest;
import org.egov.web.notification.sms.models.SMSSentRequest;
import org.egov.web.notification.sms.models.SMSTemplate;
import org.egov.web.notification.sms.models.SMSTemplateSearchCriteria;
import org.egov.web.notification.sms.models.Sms;
import org.egov.web.notification.sms.models.UserSearchResponse;
import org.egov.web.notification.sms.repository.SMSTemplateRepository;
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
	private UserService userService;

	@Autowired
	private OtpService otpService;

	@Autowired
	private SMSTemplateRepository smsTemplateRepository;

	private static final String OTP_PLACEHOLDER = "{otp}";
	private static final String VALID_FOR_PLACEHOLDER = "{valid_for}";

	private static final String SMS_BODY_OTP = "Your OTP for accessing services on the CitizenSeva portal is "
			+ OTP_PLACEHOLDER + ". This code is valid for " + VALID_FOR_PLACEHOLDER
			+ " Min. Please do not share this code with anyone. CitizenSeva H.P.";

	@Override
	public void sendOtp(OTPSentRequest otpSentRequest) {

		if (!otpSentRequest.isValid()) {
			throw new CustomException("INVALID REQUEST", "Mobile number or User uuid is missing.");
		}

		// validate user
		UserSearchResponse userSearchResponse = userService.searchUser(otpSentRequest.getUserUuid());

		if (null == userSearchResponse || CollectionUtils.isEmpty(userSearchResponse.getUserSearchResponseContent())) {
			throw new CustomException("USER NOT FOUND", "User not found for given user uuid.");
		}

		// get otp from otp service
		String otp = otpService.createOtp(otpSentRequest.getUserUuid());

		SMSTemplate smsTemplate = getSmsTemplate(SMSConstants.TEMPLATE_NAME_LOGIN_OTP);

		if (null == smsTemplate) {
			throw new CustomException("SMS TEMPLATE NOT FOUND", "SMS template not found.");
		}

		long otpValidFor = 15; // in minute

		String smsBody = SMS_BODY_OTP;
		smsBody = smsBody.replace(OTP_PLACEHOLDER, otp);
		smsBody = smsBody.replace(VALID_FOR_PLACEHOLDER, String.valueOf(otpValidFor));

		Sms sms = Sms.builder().mobileNumber(otpSentRequest.getNumber()).message(smsBody)
				.templateId(smsTemplate.getTemplateId()).expiryTime(otpValidFor * 60 * 1000).build();

		baseSmsService.sendSMS(sms);
	}

	private SMSTemplate getSmsTemplate(String templateName) {
		SMSTemplate smsTemplate = null;
		List<SMSTemplate> smsTemplates = smsTemplateRepository.fetchSmsTemplate(
				SMSTemplateSearchCriteria.builder().templateNames(Arrays.asList(templateName)).build());
		if (!CollectionUtils.isEmpty(smsTemplates)) {
			smsTemplate = smsTemplates.get(0);
		}
		return smsTemplate;
	}

	@Override
	public void sendSMS(SMSSentRequest smsSentRequest) {

		if (!smsSentRequest.isValid()) {
			throw new CustomException("INVALID REQUEST", "Mobile number or Message or Template Name is missing.");
		}

		SMSTemplate smsTemplate = getSmsTemplate(smsSentRequest.getTemplateName());

		if (null == smsTemplate) {
			throw new CustomException("SMS TEMPLATE NOT FOUND", "SMS template not found.");
		}

		Sms sms = Sms.builder().mobileNumber(smsSentRequest.getMobileNumber()).message(smsSentRequest.getMessage())
				.templateId(smsTemplate.getTemplateId()).build();

		baseSmsService.sendSMS(sms);

	}

	@Override
	public SMSRequest populateSMSRequest(SMSSentRequest smsSentRequest) {

		SMSTemplate smsTemplate = getSmsTemplate(smsSentRequest.getTemplateName());

		if (null == smsTemplate) {
			throw new CustomException("SMS TEMPLATE NOT FOUND", "SMS template not found.");
		}

		return SMSRequest.builder().mobileNumber(smsSentRequest.getMobileNumber()).message(smsSentRequest.getMessage())
				.category(smsSentRequest.getCategory()).expiryTime(smsSentRequest.getExpiryTime())
				.templateId(smsTemplate.getTemplateId()).build();
	}

}
