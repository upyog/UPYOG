package org.egov.web.notification.sms.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.egov.web.notification.sms.config.SMSProperties;
import org.egov.web.notification.sms.models.Otp;
import org.egov.web.notification.sms.models.OtpRequest;
import org.egov.web.notification.sms.models.OtpResponse;
import org.egov.web.notification.sms.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SMSProperties smsProperties;

	@Override
	public String createOtp(String userUuid) {

		StringBuilder url = new StringBuilder(smsProperties.getOtpServiceHostUrl());
		url.append(smsProperties.getOtpCreateEndpoint());

		OtpRequest otpRequest = OtpRequest.builder()
				.otp(Otp.builder().tenantId(smsProperties.getStateLevelTenantId()).identity(userUuid).build()).build();

		OtpResponse otpResponse = null;
		try {
			otpResponse = restTemplate.postForObject(url.toString(), otpRequest, OtpResponse.class);

		} catch (Exception e) {
			log.error("Error occured while otp create.", e);
			throw new CustomException("OTP CREATE ERROR", "Error occured while otp create. Message: " + e.getMessage());
		}

		if (null == otpResponse || null == otpResponse.getOtp() || StringUtils.isEmpty(otpResponse.getOtp().getOtp())) {
			throw new CustomException("OTP GENERATION ERROR", "Error occured while otp generate.");
		}

		return otpResponse.getOtp().getOtp();
	}

}
