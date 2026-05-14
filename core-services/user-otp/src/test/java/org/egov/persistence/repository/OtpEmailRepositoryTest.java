package org.egov.persistence.repository;

import org.egov.persistence.contract.EmailMessage;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import java.util.HashMap;
import org.egov.domain.model.OtpRequest;
import org.egov.domain.service.LocalizationService;
import org.egov.domain.model.OtpRequestType;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class OtpEmailRepositoryTest {

	private static final String EMAIL_TOPIC = "email.topic";
	@Mock
	private CustomKafkaTemplate<String, EmailMessage> kakfaTemplate;
	@Mock
	private LocalizationService localizationService;
	private OtpEmailRepository repository;

	@Before
	public void before() {
		repository = new OtpEmailRepository(kakfaTemplate, EMAIL_TOPIC);
		ReflectionTestUtils.setField(repository, "localizationService", localizationService);
	}

	@Test
	public void test_should_not_send_email_when_email_address_is_not_present() {
		repository.send(null, "otpNumber", null);

		verify(kakfaTemplate, never()).send(any(), any());
	}

	@Test
	public void test_should_send_email_message() {
		when(localizationService.getLocalisedMessages(anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
		OtpRequest otpRequest = mock(OtpRequest.class);
		when(otpRequest.getType()).thenReturn(OtpRequestType.PASSWORD_RESET);
		when(otpRequest.isRegistrationRequestType()).thenReturn(false);
		when(otpRequest.isLoginRequestType()).thenReturn(false);

		final EmailMessage expectedEmailMessage = EmailMessage.builder()
				.subject("mSeva Punjab - Password Reset Verification")
				.body("Dear Citizen, Your OTP for recovering password is %s.")
				.sender("")
				.emailTo("foo@bar.com")
				.isHTML(true)
				.build();

		repository.send("foo@bar.com", "otpNumber", otpRequest);

		verify(kakfaTemplate).send(EMAIL_TOPIC, expectedEmailMessage);
	}

}