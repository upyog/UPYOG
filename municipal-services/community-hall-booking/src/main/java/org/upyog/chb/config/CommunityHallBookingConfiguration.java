package org.upyog.chb.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	
	//Notification Template
	@Value("${chb.notification.booking.created.template}")
	private String bookingCreatedTemplate;

	@Value("${chb.notification.booked.template}")
	private String bookedTemplate;
	
	@Value("${chb.claculation.tax.applicable}")
	private String applicableTaxes;
	

}