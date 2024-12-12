package org.egov.garbageservice.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.enums.SMSCategory;
import org.egov.garbageservice.model.Email;
import org.egov.garbageservice.model.EmailRequest;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.SMSRequest;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

	private static final String USER_NAME_PLACEHOLDER = "{user_name}";
	private static final String GARBAGE_BILL_NO_PLACEHOLDER = "{bill_no}";

	private static final String EMAIL_BODY_GENERATE_BILL = "Dear " + USER_NAME_PLACEHOLDER + ",\r\n" + "\r\n"
			+ "Email Sent Successfully";

	private static final String SMS_BODY_GENERATE_BILL = "Message Sent Successfully";

	private static final String EMAIL_SUBJECT_GENERATE_BILL = "UPYOG: Congratulations! Your Bill Has Been Generated : "
			+ GARBAGE_BILL_NO_PLACEHOLDER;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topics.email.service.topic.name}")
	private String emailTopic;

	@Value("${kafka.topics.sms.service.topic.name}")
	private String smsTopic;

	public void sendSms(String message, String mobileNumber) {

		SMSRequest smsRequest = SMSRequest.builder().message(message).mobileNumber(mobileNumber)
				.category(SMSCategory.NOTIFICATION).build();

		kafkaTemplate.send(smsTopic, smsRequest);
	}

	private void sendEmail(String emailBody, List<String> emailIds, RequestInfo requestInfo,
			List<String> attachmentDocRefIds, String emailSubject) {
		Email email = new Email();
		email.setEmailTo(new HashSet<>(emailIds));
		email.setBody(emailBody);
		email.setSubject(emailSubject);

		if (!CollectionUtils.isEmpty(attachmentDocRefIds)) {
			email.setHTML(true);
			email.setFileStoreIds(attachmentDocRefIds);
		} else {
			email.setHTML(false);
		}

		EmailRequest emailRequest = EmailRequest.builder().requestInfo(requestInfo).email(email).build();
		kafkaTemplate.send(emailTopic, emailRequest);
	}

	public void triggerNotificationsGenerateBill(GarbageAccount garbageAccount, Bill bill,
			RequestInfoWrapper requestInfoWrapper) {
		String emailBody = EMAIL_BODY_GENERATE_BILL;
		String smsBody = SMS_BODY_GENERATE_BILL;
		String emailSubject = EMAIL_SUBJECT_GENERATE_BILL;

		emailBody = populateNotificationPlaceholders(emailBody, garbageAccount, bill);
		smsBody = populateNotificationPlaceholders(smsBody, garbageAccount, bill);
		emailSubject = populateNotificationPlaceholders(emailSubject, garbageAccount, bill);

		if (!StringUtils.isEmpty(garbageAccount.getEmailId())) {
			sendEmail(emailBody, Collections.singletonList(garbageAccount.getEmailId()),
					requestInfoWrapper.getRequestInfo(), null, emailSubject);
		}
		if (!StringUtils.isEmpty(garbageAccount.getMobileNumber())) {
			sendSms(smsBody, garbageAccount.getMobileNumber());
		}

	}

	private String populateNotificationPlaceholders(String body, GarbageAccount garbageAccount, Bill bill) {
		body = body.replace(USER_NAME_PLACEHOLDER, garbageAccount.getName());
		body = body.replace(GARBAGE_BILL_NO_PLACEHOLDER, bill.getBillNumber());
		return body;
	}

}
