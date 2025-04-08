/**
 * Main configuration class for the E-Waste Management Service module.
 * 
 * This class sets up essential configurations for the service, including:
 * 1. Default timezone configuration for consistent date/time handling
 * 2. HTTP message converter setup for proper API request/response handling
 * 3. Kafka topic configurations for e-waste related events
 * 
 * The class uses Lombok annotations (@Getter, @AllArgsConstructor, etc.) to reduce boilerplate code,
 * imports TracerConfiguration for request tracing capabilities, and provides beans
 * that will be used throughout the application.
 * 
 * It also contains configuration properties for various services and endpoints
 * needed by the e-waste management system.
 */

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
	
	@Value("${upyog.mdms.v2.host}")
	private String mdmsV2Host;
	
	@Value("${upyog.mdms.v2.search.endpoint}")
	private String mdmsV2Endpoint;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;
	
	@Value("${upyog.mdms.v2.enabled}")
	private boolean mdmsV2Enabled;
	
	@PostConstruct
	public void init() {
		if (mdmsV2Enabled) {
			mdmsHost = mdmsV2Host;
			mdmsEndpoint = mdmsV2Endpoint;
		}
	}

}