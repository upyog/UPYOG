package org.upyog.chb.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is a configuration component for the Community Hall Booking module.
 * It is responsible for setting up application-specific configurations such as
 * the default timezone for the application.
 * 
 * Annotations used:
 * - @Builder: Enables the builder pattern for creating instances of this class.
 * - @Component: Marks this class as a Spring-managed component.
 * - @AllArgsConstructor: Generates a constructor with all fields as parameters.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * - @Getter and @Setter: Automatically generates getter and setter methods for fields.
 * - @Import: Imports additional configuration classes (e.g., TracerConfiguration).
 */

@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Import({ TracerConfiguration.class })
public class CommunityHallBookingConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Value("${persister.save.communityhall.booking.topic}")
	private String communityHallBookingSaveTopic;

	@Value("${persister.save.communityhall.booking.init.topic}")
	private String communityHallBookingInitSaveTopic;
	
	@Value("${persister.update.communityhall.booking.topic}")
	private String communityHallBookingUpdateTopic;

	// Idgen Config
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.comunityHallBooking.booking.id.name}")
	private String communityHallBookingIdKey;

	@Value("${egov.idgen.comunityHallBooking.booking.id.format}")
	private String communityHallBookingIdFromat;
	
	// Pagination config for search results
	@Value("${egov.chb.default.limit}")
	private Integer defaultLimit;

	@Value("${egov.chb.default.offset}")
	private Integer defaultOffset;

	@Value("${egov.chb.max.limit}")
	private Integer maxSearchLimit;

	// Workflow configs
	@Value("${workflow.host}")
	private String wfHost;

	@Value("${workflow.transition.path}")
	private String wfTransitionPath;

	@Value("${workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	@Value("${workflow.processinstance.search.path}")
	private String wfProcessSearchPath;

	// MDMS Config
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsPath;

	@Value("${upyog.mdms.v2.host}")
	private String mdmsV2Host;

	@Value("${upyog.mdms.v2.search.endpoint}")
	private String mdmsV2Path;

	@Value("${upyog.mdms.v2.enabled}")
	private boolean mdmsV2Enabled;

	@PostConstruct
	public void init() {
		if(mdmsV2Enabled) {
			mdmsHost = mdmsV2Host;
			mdmsPath = mdmsV2Path;
		}
	}
	
	@Value("${employee.allowed.search.params}")
	private String allowedEmployeeSearchParameters;

	// User Config
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	// NOTIFICATION TOPICS
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${kafka.topics.notification.email}")
	private String emailNotifTopic;

	@Value("${kafka.topics.receipt.create}")
	private String receiptCreateTopic;

	@Value("${egov.localization.statelevel}")
	private Boolean isStateLevel;

	@Value("${notif.sms.enabled}")
	private Boolean isSMSNotificationEnabled;

	@Value("${notif.email.enabled}")
	private Boolean isEmailNotificationEnabled;

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

	@Value("${egov.user.event.notification.enabled}")
	private Boolean isUserEventsNotificationEnabled;
	
	
	@Value("${egov.msg.pay.link}")
	private String payLinkSMS;
	
	@Value("${egov.chb.download.permission.letter.link}")
	private String permissionLetterLink;

	// Billing-Service

	@Value("${egbs.host}")
	private String egbsHost;

	@Value("${egbs.fetchbill.endpoint}")
	private String egbsFetchBill;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;

	@Value("${egov.billingservice.host}")
	private String billingHost;

	@Value("${egov.demand.create.endpoint}")
	private String demandCreateEndpoint;

	@Value("${egov.demand.update.endpoint}")
	private String demandUpdateEndpoint;

	@Value("${egov.demand.search.endpoint}")
	private String demandSearchEndpoint;

	@Value("${egov.bill.gen.endpoint}")
	private String billGenerateEndpoint;

	@Value("${internal.microservice.user.username}")
	private String internalMicroserviceUserName;

	@Value("${internal.microservice.user.type}")
	private String internalMicroserviceUserType;

	@Value("${state.level.tenant.id}")
	private String stateLevelTenantId;
	
	//Module and business name
	@Value("${chb.module.name}")
	private String moduleName;
	
	@Value("${chb.business.service.name}")
	private String businessServiceName;
	
	//url shortener
	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${egov.url.shortner.endpoint}")
	private String shortenerEndpoint;
	
	@Value("${booking.payment.timer.value}")
	private String bookingPaymentTimerValue;
	
	@Value("${egov.download.receipt.link}")
    private String downloadReceipt;
 	 
    @Value("${egov.usr.events.pay.now.link}")
    private String payNowLink;

}