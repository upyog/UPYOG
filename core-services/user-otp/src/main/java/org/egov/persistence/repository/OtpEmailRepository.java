package org.egov.persistence.repository;

import org.egov.domain.model.OtpRequest;
import org.egov.domain.service.LocalizationService;
import org.egov.persistence.contract.EmailMessage;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Map;

@Service
public class OtpEmailRepository {
	private static final String PASSWORD_RESET_SUBJECT = "mSeva Punjab - Password Reset Verification";
	private static final String REGISTER_SUBJECT = "mSeva Punjab - Registration OTP";
    private static final String LOGIN_SUBJECT = "OTP for Login - mSeva Punjab Municipal Services";
    private static final String PASSWORD_RESET_BODY = "Your OTP for recovering password is %s.";
    private CustomKafkaTemplate<String, EmailMessage> kafkaTemplate;
    private String emailTopic;
    private static final String LOCALIZATION_KEY_REGISTER_MAIL = "email_register_otp";
    private static final String LOCALIZATION_KEY_LOGIN_MAIL = "email_login_otp";
    private static final String LOCALIZATION_KEY_PWD_RESET_MAIL = "email_reset_otp";
    @Autowired
    public OtpEmailRepository(CustomKafkaTemplate<String, EmailMessage> kafkaTemplate,
							  @Value("${email.topic}") String emailTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.emailTopic = emailTopic;
    }
    @Autowired
    private LocalizationService localizationService;
    
    
    public void send(String email, String otpNumber, OtpRequest otpRequest) {
    	if (isEmpty(email)) {
			return;
		}
		sendEmail(email, otpNumber, otpRequest);
    }

    private void sendEmail(String email, String otpNumber, OtpRequest otpRequest) {

        final String messageFormat = getMessageFormat(otpRequest);
        String finalBody = messageFormat
                .replace("{otp}", otpNumber)
                .replace("{userName}", "Citizen")
                .replace("{expiryMinutes}", "15")
                .replace("{cityName}", "Punjab");
        final EmailMessage emailMessage = EmailMessage.builder()
                .body(finalBody)
                .subject(getSubject(otpRequest))
                .emailTo(email)
                .isHTML(true)
                .sender(EMPTY)
                .build();

        kafkaTemplate.send(emailTopic, emailMessage);
    }

    private String getMessageFormat(OtpRequest otpRequest) {
        String tenantId = "pb";
        Map<String, String> localisedMsgs = localizationService.getLocalisedMessages(tenantId, "en_IN", "egov-user");
        if (localisedMsgs.isEmpty()) {
            localisedMsgs.put(LOCALIZATION_KEY_REGISTER_MAIL, "Dear Citizen, Your OTP to complete your mSeva Registration is %s.");
            localisedMsgs.put(LOCALIZATION_KEY_LOGIN_MAIL, "Dear Citizen, Your Login OTP is %s.");
            localisedMsgs.put(LOCALIZATION_KEY_PWD_RESET_MAIL, "Dear Citizen, Your OTP for recovering password is %s.");
        }
        String message = null;

        if (otpRequest.isRegistrationRequestType())
            message = localisedMsgs.get(LOCALIZATION_KEY_REGISTER_MAIL);
        else if (otpRequest.isLoginRequestType())
            message = localisedMsgs.get(LOCALIZATION_KEY_LOGIN_MAIL);
        else
            message = localisedMsgs.get(LOCALIZATION_KEY_PWD_RESET_MAIL);

        return message;
    }
    
	private String getSubject( OtpRequest otpRequest) {
		if (!isEmpty(otpRequest.getType().toString())) {
			if (otpRequest.isRegistrationRequestType())
				return REGISTER_SUBJECT;
			else if (otpRequest.isLoginRequestType())
				return LOGIN_SUBJECT;
			else
				return PASSWORD_RESET_SUBJECT;
		}
		return PASSWORD_RESET_SUBJECT;
	}

	private String getBody(String otpNumber) {
		return format(PASSWORD_RESET_BODY, otpNumber);
	}

}
