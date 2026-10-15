package org.egov.web.notification.mail.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.egov.web.notification.mail.consumer.contract.Email;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.egov.web.notification.mail.config.EmailProperties;
import java.util.Properties;
import org.springframework.core.io.ByteArrayResource;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
@Slf4j
public class ExternalEmailService implements EmailService {

	public static final String EXCEPTION_MESSAGE = "Exception creating HTML email";
	private JavaMailSenderImpl mailSender;

	public ExternalEmailService(JavaMailSenderImpl mailSender, EmailProperties emailProperties) {

		this.mailSender = mailSender;
		this.emailProperties = emailProperties;
	}
    
    private EmailProperties emailProperties;
    
    @Override
    public void sendEmail(Email email) {
		if(email.isHTML()) {
			sendHTMLEmail(email);
		} else {
			sendTextEmail(email);
		}
    }

	private void sendTextEmail(Email email) {
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email.getEmailTo().toArray(new String[0]));
		mailMessage.setSubject(email.getSubject());
		mailMessage.setText(email.getBody());
		mailSender.send(mailMessage);
	}

	private void sendHTMLEmail(Email email) {
		JavaMailSenderImpl dynamicSender = getMailSender(email.getUlbName());
		MimeMessage message = dynamicSender.createMimeMessage();
		MimeMessageHelper helper;

		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo(email.getEmailTo().toArray(new String[0]));
			helper.setSubject(email.getSubject());
			helper.setText(email.getBody(), true);
			if (email.getAttachment() != null) {
				helper.addAttachment(email.getAttachmentFileName(), new ByteArrayResource(email.getAttachment()));
			}
		} catch (MessagingException e) {
			log.error(EXCEPTION_MESSAGE, e);
			throw new RuntimeException(e);
		}
		dynamicSender.send(message);
	}
	
	private JavaMailSenderImpl getMailSender(String ulbName) {

		if (!"Solan".equalsIgnoreCase(ulbName)) {
			return mailSender;
		}

		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		sender.setHost(emailProperties.getMailHost());
		sender.setPort(emailProperties.getMailPort());
		sender.setProtocol(emailProperties.getMailProtocol());
		sender.setUsername(emailProperties.getSolanUsername());
		sender.setPassword(emailProperties.getSolanPassword());

		Properties props = sender.getJavaMailProperties();
		props.put("mail.smtps.auth", emailProperties.getMailSmtpsAuth());
		props.put("mail.smtps.starttls.enable", emailProperties.getMailStartTlsEnabled());
		props.put("mail.smtps.debug", emailProperties.getMailSmtpsDebug());

		return sender;
	}
	
}
