package org.upyog.adv.config;

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
 * This class is a configuration component for the Advertisement Booking module.
 * It is responsible for setting up application-specific configurations such as
 * the default timezone for the application
**/
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Import({ TracerConfiguration.class })
public class BookingConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Value("${persister.save.advertisement.booking.topic}")
	private String advertisementBookingSaveTopic;

	@Value("${persister.update.advertisement.booking.topic}")
	private String advertisementBookingUpdateTopic;
	
	@Value("${persister.create.draft.advertisement.topic}")
	private String advertisementDraftApplicationSaveTopic;

	@Value("${persister.update.draft.advertisement.topic}")
	private String advertisementDraftApplicationUpdateTopic;

	@Value("${persister.delete.draft.advertisement.topic}")
	private String advertisementDraftApplicationDeleteTopic;


	// Idgen Config
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.advertisement.booking.id.name}")
	private String advertisementBookingIdKey;

	@Value("${egov.idgen.advertisement.booking.id.format}")
	private String advertisementBookingIdFromat;

	// Pagination config for search results
	@Value("${egov.adv.default.limit}")
	private Integer defaultLimit;

	@Value("${egov.adv.default.offset}")
	private Integer defaultOffset;

	@Value("${egov.adv.max.limit}")
	private Integer maxSearchLimit;

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
//	@Value("${kafka.topics.notification.sms}")
//	private String smsNotifTopic;
//
//	@Value("${kafka.topics.notification.email}")
//	private String emailNotifTopic;

	@Value("${kafka.topics.receipt.create}")
	private String receiptCreateTopic;

	@Value("${egov.adv.download.permission.letter.link}")
	private String permissionLetterLink;
	
	@Value("${egov.application.view.link}")
	private String viewApplicationLink;
	
	// Module and business name
	@Value("${adv.module.name}")
	private String moduleName;

	@Value("${adv.business.service.name}")
	private String businessServiceName;

	@Value("${adv.calculation.tax.applicable}")
	private String applicableTaxes;

	@Value("${egbs.host}")
	private String egbsHost;

	@Value("${egbs.fetchbill.endpoint}")
	private String egbsFetchBill;

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

	@Value("${adv.payment.timer}")
	private Long paymentTimer;

	// USER EVENTS
	@Value("${egov.ui.app.host}")
	private String uiAppHost;

	@Value("${egov.usr.events.create.topic}")
	private String saveUserEventsTopic;

	@Value("${egov.user.event.notification.enabled}")
	private Boolean isUserEventsNotificationEnabled;

	@Value("${egov.msg.pay.link}")
	private String payLinkSMS;

	// url shortener
	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${egov.url.shortner.endpoint}")
	private String shortenerEndpoint;

	// NOTIFICATION TOPICS
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${kafka.topics.notification.email}")
	private String emailNotifTopic;

	@Value("${egov.localization.statelevel}")
	private Boolean isStateLevel;

	@Value("${notif.sms.enabled}")
	private Boolean isSMSNotificationEnabled;

	@Value("${notif.email.enabled}")
	private Boolean isEmailNotificationEnabled;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;
	// Localization
	@Value("${egov.localization.host}")
	private String localizationHost;

	@Value("${egov.localization.context.path}")
	private String localizationContextPath;

	@Value("${egov.localization.search.endpoint}")
	private String localizationSearchEndpoint;
 	
 	@Value("${egov.download.receipt.link}")
    private String downloadReceiptLink;
 	 
    @Value("${egov.usr.events.paynow.link}")
    private String payNowLink;
	

}