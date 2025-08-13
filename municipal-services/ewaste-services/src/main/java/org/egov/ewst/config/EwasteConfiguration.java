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

/**
 * Configuration class for the Ewaste application.
 * This class sets up various configurations such as timezone, Kafka topics,
 * user service, ID generation service, notification topics, localization,
 * workflow service, and MDMS (Master Data Management System).
 */
@Import({ TracerConfiguration.class })
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class EwasteConfiguration {

	// Application timezone
	@Value("${app.timezone}")
	private String timeZone;

	// Initialize method to set the default timezone
	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	// Bean configuration for Jackson message converter
	@Bean
	@Autowired
	public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	// Kafka topics for ewaste creation and update
	@Value("${ewaste.kafka.create.topic}")
	private String createEwasteTopic;

	@Value("${ewaste.kafka.update.topic}")
	private String updateEwasteTopic;

	// User service configuration
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	// ID generation service configuration
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.ewid.format}")
	private String ewasteIdGenFormat;

	@Value("${egov.idgen.ewid.name}")
	private String ewasteIdGenName;

	// Notification topics
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${kafka.topics.notification.email}")
	private String emailNotifTopic;

	@Value("${kafka.topics.receipt.create}")
	private String receiptTopic;

	@Value("${kafka.topics.notification.pg.save.txns}")
	private String pgTopic;

	// Localization configuration
	@Value("${egov.localization.statelevel}")
	private Boolean isStateLevel;

	// Notification enablement flags
	@Value("${notif.sms.enabled}")
	private Boolean isSMSNotificationEnabled;

	@Value("${notif.email.enabled}")
	private Boolean isEmailNotificationEnabled;

	// Business service configuration
	@Value("${egov.ew.businessService}")
	private String businessService;

	// User event notification configuration
	@Value("${egov.usr.events.download.receipt.link}")
	private String userEventReceiptDownloadLink;

	// Localization service configuration
	@Value("${egov.localization.host}")
	private String localizationHost;

	@Value("${egov.localization.context.path}")
	private String localizationContextPath;

	@Value("${egov.localization.search.endpoint}")
	private String localizationSearchEndpoint;

	@Value("${egov.localization.fallback.locale}")
	private String fallBackLocale;

	// User events configuration
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

	// Workflow service configuration
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

	// MDMS (Master Data Management System) configuration
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

	// Initialize method to set MDMS host and endpoint based on version
	@PostConstruct
	public void init() {
		if (mdmsV2Enabled) {
			mdmsHost = mdmsV2Host;
			mdmsEndpoint = mdmsV2Endpoint;
		}
	}
}