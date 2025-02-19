package org.egov.pgr.service;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pgr.web.models.Email;
import org.egov.pgr.web.models.EmailRequest;
import org.egov.pgr.web.models.PGRNotification;
import org.egov.pgr.web.models.RequestInfoWrapper;
import org.egov.pgr.web.models.SMSSentRequest;
import org.egov.pgr.web.models.enums.SMSCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

	private static final String SMS_TEMPLATE_PGR_STATUS_NOTIFICATION = "PGR-STATUS-NOTIFICATION";

	private static final String SERVICE_REQUEST_ID_PLACEHOLDER = "{service_request_id}";
	private static final String APPLICATION_STATUS_PLACEHOLDER = "{application_status}";

	private static final String SMS_BODY_STATUS_CHANGE = "Your Grievance with ID No. " + SERVICE_REQUEST_ID_PLACEHOLDER
			+ " has been " + APPLICATION_STATUS_PLACEHOLDER
			+ ". Please visit CitizenSeva Portal for details. CitizenSeva H.P.";

	private static final String EMAIL_BODY_STATUS_CHANGE = "Your Grievance with ID No. "
			+ SERVICE_REQUEST_ID_PLACEHOLDER + " has been " + APPLICATION_STATUS_PLACEHOLDER
			+ ". Please visit CitizenSeva Portal for details. CitizenSeva H.P.";

	private static final String EMAIL_SUBJECT_STATUS_CHANGE = "Your Grievance with ID No. "
			+ SERVICE_REQUEST_ID_PLACEHOLDER + " has been " + APPLICATION_STATUS_PLACEHOLDER + ".";

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topics.email.service.topic.name}")
	private String emailTopic;

	@Value("${kafka.topics.sms.service.topic.name}")
	private String smsTopic;

	public void sendSms(String message, String mobileNumber) {

		SMSSentRequest smsRequest = SMSSentRequest.builder().message(message).mobileNumber(mobileNumber)
				.category(SMSCategory.NOTIFICATION).templateName(SMS_TEMPLATE_PGR_STATUS_NOTIFICATION).build();

		kafkaTemplate.send(smsTopic, smsRequest);
	}

	private void sendEmail(String emailBody, List<String> emailIds, RequestInfo requestInfo,
			List<String> attachmentDocRefIds, String emailSubject) {
		Email email = new Email();
		email.setEmailTo(new HashSet<>(emailIds));
		email.setBody(emailBody);
		email.setSubject(emailSubject);
		email.setHTML(true);

		if (!CollectionUtils.isEmpty(attachmentDocRefIds)) {
			email.setFileStoreIds(attachmentDocRefIds);
		}

		EmailRequest emailRequest = EmailRequest.builder().requestInfo(requestInfo).email(email).build();
		kafkaTemplate.send(emailTopic, emailRequest);
	}

	public PGRNotification triggerNotificationsStatusChange(PGRNotification pgrNotification,
			RequestInfoWrapper requestInfoWrapper) {
		String emailBody = EMAIL_BODY_STATUS_CHANGE;
		String smsBody = SMS_BODY_STATUS_CHANGE;
		String emailSubject = EMAIL_SUBJECT_STATUS_CHANGE;

		emailBody = populateNotificationPlaceholders(smsBody, pgrNotification);
		smsBody = populateNotificationPlaceholders(smsBody, pgrNotification);
		emailSubject = populateNotificationPlaceholders(smsBody, pgrNotification);

		if (!pgrNotification.getIsEmailSent() && !StringUtils.isEmpty(pgrNotification.getEmailId())) {
//			sendEmail(emailBody, Collections.singletonList(pgrNotification.getEmailId()),
//					requestInfoWrapper.getRequestInfo(), null, emailSubject); // TODO uncommit for send email
			pgrNotification.setIsEmailSent(true);
		}
		if (!pgrNotification.getIsSmsSent() && !StringUtils.isEmpty(pgrNotification.getMobileNumber())) {
			sendSms(smsBody, pgrNotification.getMobileNumber());
			pgrNotification.setIsSmsSent(true);
		}

		return pgrNotification;
	}

	private String populateNotificationPlaceholders(String body, PGRNotification pgrNotification) {

		body = body.replace(SERVICE_REQUEST_ID_PLACEHOLDER, pgrNotification.getServiceRequestId());
		body = body.replace(APPLICATION_STATUS_PLACEHOLDER, "resolved"); // TODO

		return body;
	}

}