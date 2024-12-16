package org.egov.garbageservice.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.egov.garbageservice.model.GrbgAddress;
import org.egov.garbageservice.model.SMSRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

	@Value("classpath:template/GrbgBillEmailTemplate.html")
	private Resource grbgBillEmailTemplateResource;

	private static final String RECIPINTS_NAME_PLACEHOLDER = "{recipients_name}";
	private static final String MONTH_PLACEHOLDER = "{month}";
	private static final String YEAR_PLACEHOLDER = "{year}";
	private static final String GARBAGE_BILL_NO_PLACEHOLDER = "{bill_no}";
	private static final String ADDRESS_PLACEHOLDER = "{address}";
	private static final String COLLECTION_UNIT_TYPE_PLACEHOLDER = "{collection_unit_type}";
	private static final String AMOUNT_PLACEHOLDER = "{amount}";
	private static final String DUE_DATE_PLACEHOLDER = "{due_date}";
	private static final String GARBAGE_NO_PLACEHOLDER = "{garbage_no}";

//	private static final String EMAIL_BODY_GENERATE_BILL = "Dear " + RECIPINTS_NAME_PLACEHOLDER + ",\r\n" + " \r\n"
//			+ "We hope this message finds you well. We would like to inform you that your garbage collection bill for"
//			+ " the period of " + MONTH_PLACEHOLDER + "/" + YEAR_PLACEHOLDER
//			+ " has been generated. Below are the details of your bill:\r\n" + " \r\n" + "Bill Details:\r\n" + " \r\n"
//			+ "Bill ID: " + GARBAGE_BILL_NO_PLACEHOLDER + "\r\n" + "Name: " + RECIPINTS_NAME_PLACEHOLDER + "\r\n"
//			+ "Address: " + ADDRESS_PLACEHOLDER + "\r\n" + "Collection Unit Type: " + COLLECTION_UNIT_TYPE_PLACEHOLDER
//			+ "\r\n" + "Total Amount: " + AMOUNT_PLACEHOLDER + "\r\n" + "Due Date: " + DUE_DATE_PLACEHOLDER;

	private static final String EMAIL_BODY_GENERATE_BILL = GrbgConstants.getContentAsString(
			"C:/SOURAV/Git/HP/UPYOG/municipal-services/garbage-service/src/main/resources/template/GrbgBillEmailTemplate.html");
//	private final String EMAIL_BODY_GENERATE_BILL = grbgBillEmailTemplate();

	private static final String SMS_BODY_GENERATE_BILL = "Message Sent Successfully";

	private static final String EMAIL_SUBJECT_GENERATE_BILL = "Your Garbage Collection Bill for " + MONTH_PLACEHOLDER
			+ "/" + YEAR_PLACEHOLDER + " with " + GARBAGE_NO_PLACEHOLDER;

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
			email.setHTML(true);
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

		Instant instant = Instant.ofEpochMilli(bill.getBillDate());
		LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

		body = body.replace(RECIPINTS_NAME_PLACEHOLDER, garbageAccount.getName());
		body = body.replace(MONTH_PLACEHOLDER, GrbgConstants.toCamelCase(String.valueOf(dateTime.getMonth())));
		body = body.replace(YEAR_PLACEHOLDER, String.valueOf(dateTime.getYear()));
		body = body.replace(GARBAGE_BILL_NO_PLACEHOLDER, bill.getBillNumber());
		if (!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
			body = body.replace(ADDRESS_PLACEHOLDER, prepareAddress(garbageAccount.getAddresses().get(0)));
		}
		if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
			body = body.replace(COLLECTION_UNIT_TYPE_PLACEHOLDER,
					!StringUtils.isEmpty(garbageAccount.getGrbgCollectionUnits().get(0).getUnitType())
							? garbageAccount.getGrbgCollectionUnits().get(0).getUnitType()
							: "");
		}
		body = body.replace(AMOUNT_PLACEHOLDER, String.valueOf(bill.getTotalAmount()));
		body = body.replace(DUE_DATE_PLACEHOLDER, "");
		body = body.replace(GARBAGE_NO_PLACEHOLDER, garbageAccount.getGrbgApplicationNumber());
		return body;
	}

	private String prepareAddress(GrbgAddress grbgAddress) {
		StringBuilder fullAddress = new StringBuilder();
		if (null != grbgAddress) {
			// address1+ward_name+ulb_name(ulbType)+District +pincode
			fullAddress.append(!StringUtils.isEmpty(grbgAddress.getAddress1()) ? grbgAddress.getAddress1() : "")
					.append(", ")
					.append(!StringUtils.isEmpty(grbgAddress.getWardName()) ? grbgAddress.getWardName() : "")
					.append(", ").append(!StringUtils.isEmpty(grbgAddress.getUlbName()) ? grbgAddress.getUlbName() : "")
					.append("(").append(!StringUtils.isEmpty(grbgAddress.getUlbType()) ? grbgAddress.getUlbType() : "")
					.append("), ")
					.append(null != grbgAddress.getAdditionalDetail() ? GrbgConstants.removeFirstAndLastChar(
							String.valueOf(grbgAddress.getAdditionalDetail().get("district"))) : "")
					.append(", ").append(!StringUtils.isEmpty(grbgAddress.getPincode()) ? grbgAddress.getPincode() : "")
					.append(".");
		}
		return String.valueOf(fullAddress);
	}

	public String grbgBillEmailTemplate() {
		String htmlContent = "";
		try {
			htmlContent = StreamUtils.copyToString(grbgBillEmailTemplateResource.getInputStream(),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// Handle exception if the file is not found or can't be read
			e.printStackTrace();
		}
		return htmlContent;
	}

}
