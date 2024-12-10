package org.egov.ewst.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.TimeZone;

@Import({ TracerConfiguration.class })
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class EwasteConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Bean
	@Autowired
	public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	// PERSISTER
	@Value("${ewaste.kafka.create.topic}")
	private String createEwasteTopic;

	@Value("${ewaste.kafka.update.topic}")
	private String updateEwasteTopic;

	// USER
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	// IDGEN config

	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.ewid.format}")
	private String ewasteIdGenFormat;

	@Value("${egov.idgen.ewid.name}")
	private String ewasteIdGenName;

	// NOTIFICATION TOPICS
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${kafka.topics.notification.email}")
	private String emailNotifTopic;

	@Value("${kafka.topics.receipt.create}")
	private String receiptTopic;

	@Value("${kafka.topics.notification.pg.save.txns}")
	private String pgTopic;

	@Value("${egov.localization.statelevel}")
	private Boolean isStateLevel;

	@Value("${notif.sms.enabled}")
	private Boolean isSMSNotificationEnabled;

	@Value("${notif.email.enabled}")
	private Boolean isEmailNotificationEnabled;

	@Value("${egov.ew.businessService}")
	private String businessService;

	// Notif variables

	@Value("${egov.usr.events.download.receipt.link}")
	private String userEventReceiptDownloadLink;

	// Localization
	@Value("${egov.localization.host}")
	private String localizationHost;

	@Value("${egov.localization.context.path}")
	private String localizationContextPath;

	@Value("${egov.localization.search.endpoint}")
	private String localizationSearchEndpoint;

	@Value("${egov.localization.fallback.locale}")
	private String fallBackLocale;
	// USER EVENTS
	@Value("${egov.ui.app.host}")
	private String uiAppHost;

	@Value("${egov.usr.events.create.topic}")
	private String saveUserEventsTopic;

	@Value("${egov.usr.events.pay.code}")
	private String payCode;

	@Value("${egov.user.event.notification.enabled}")
	private Boolean isUserEventsNotificationEnabled;

	@Value("${egov.msg.download.receipt.link}")
	private String receiptDownloadLink;

	@Value("${egov.msg.pay.link}")
	private String payLinkSMS;

	@Value("${workflow.host}")
	private String wfHost;

	@Value("${workflow.transition.path}")
	private String wfTransitionPath;

	@Value("${workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	@Value("${workflow.processinstance.search.path}")
	private String wfProcessInstanceSearchPath;

	@Value("${is.workflow.enabled}")
	private Boolean isWorkflowEnabled;

	@Value("${workflow.status.active}")
	private String wfStatusActive;

	// ##### mdms

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;

}