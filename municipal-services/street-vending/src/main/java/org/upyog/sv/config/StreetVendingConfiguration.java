package org.upyog.sv.config;

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

@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Import({ TracerConfiguration.class })
public class StreetVendingConfiguration {

	/*
	 * // Pagination config for search results
	 * 
	 * @Value("${egov.street-vening.default.limit}") private Integer defaultLimit;
	 * 
	 * @Value("${egov.street-vending.default.offset}") private Integer
	 * defaultOffset;
	 * 
	 * @Value("${egov.chb.max.limit}") private Integer maxSearchLimit;
	 * 
	 * // Workflow configs
	 */
	@Value("${egov.workflow.host}")
	private String wfHost;

	@Value("${egov.workflow.transition.path}")
	private String wfTransitionPath;

	@Value("${egov.workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	@Value("${egov.workflow.processinstance.search.path}")
	private String wfProcessSearchPath;

	@Value("${egov.workflow.processinstance.search.path}")
	private String wfProcessInstanceSearchPath;

	/** Used for application no creation **/

	@Value("${egov.idgen.street-vending.application.id.name}")
	private String streetVendingApplicationKey;

	@Value("${egov.idgen.street-vending.application.id.format}")
	private String streetVendingApplicationFormat;

	// Kafka topics for saving street vending data

	@Value("${egov.idgen.street-vending.certificate.no.name}")
	private String streetVendingCertificateNoName;

	@Value("${egov.idgen.street-vending.certificate.no.format}")
	private String streetVendingCertificateNoFormat;

	@Value("${persister.create.street-vending.topic}")
	private String streetVendingApplicationSaveTopic;

	@Value("${persister.update.street-vending.topic}")
	private String streetVendingApplicationUpdateTopic;

	@Value("${persister.create.draft.street-vending.topic}")
	private String streetVendingDraftApplicationSaveTopic;

	@Value("${persister.update.draft.street-vending.topic}")
	private String streetVendingDraftApplicationUpdateTopic;

	@Value("${persister.delete.draft.street-vending.topic}")
	private String streetVendingDraftApplicationDeleteTopic;

	@Value("${sv.module.name}")
	private String moduleName;
	
	@Value("${sv.service.monthly.name}")
	private String serviceNameMonthly;
	
	@Value("${sv.service.quaterly.name}")
	private String serviceNameQuaterly;

	@Value("${sv.payment.business.service.name}")
	private String paymentBusinessServiceName;

	@Value("${sv.business.service.name}")
	private String businessServiceName;

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

	// Idgen Config
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

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

	@Value("${employee.allowed.search.params}")
	private String allowedEmployeeSearchParameters;

	// USER EVENTS
	@Value("${egov.ui.app.host}")
	private String uiAppHost;

	@Value("${egov.usr.events.create.topic}")
	private String saveUserEventsTopic;

	@Value("${egov.user.event.notification.enabled}")
	private Boolean isUserEventsNotificationEnabled;

	// url shortener
	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${egov.url.shortner.endpoint}")
	private String shortenerEndpoint;

	@Value("${chb.claculation.tax.applicable}")
	private String applicableTaxes;

	// Billing-Service

	@Value("${egbs.host}")
	private String egbsHost;

	@Value("${egbs.fetchbill.endpoint}")
	private String egbsFetchBill;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;

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
 	
 	 @Value("${egov.download.receipt.link}")
     private String downloadReceiptLink;
 	 
 	 @Value("${egov.usr.events.paynow.link}")
     private String payNowLink;
 	 
 	@Value("${persister.create.payment-schedule.topic}")
	private String streetVendingPaymentScheduleSaveTopic;
 	
 	@Value("${persister.update.payment-schedule.topic}")
	private String streetVendingPaymentScheduleUpdateTopic;
 	
 	@Value("${egov.location.host}")
	private String locationHost;
 	
 	@Value("${egov.location.path}")
	private String locationPath;
 	
 	@Value("${egov.location.heirarchy}")
	private String locationHierarchyTypeCode;

	@Value("${internal.microservice.user.username}")
	private String internalMicroserviceUserName;

	@Value("${internal.microservice.user.type}")
	private String internalMicroserviceUserType;

	@Value("${state.level.tenant.id}")
	private String stateLevelTenantId;
 	
}
