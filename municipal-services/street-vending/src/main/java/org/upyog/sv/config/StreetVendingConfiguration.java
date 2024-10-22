package org.upyog.sv.config;

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
	 * 
	 * @Value("${workflow.host}") private String wfHost;
	 * 
	 * @Value("${workflow.transition.path}") private String wfTransitionPath;
	 * 
	 * @Value("${workflow.businessservice.search.path}") private String
	 * wfBusinessServiceSearchPath;
	 * 
	 * @Value("${workflow.processinstance.search.path}") private String
	 * wfProcessSearchPath;
	 * 
	 * // User Config
	 * 
	 * @Value("${egov.user.host}") private String userHost;
	 * 
	 * @Value("${egov.user.context.path}") private String userContextPath;
	 * 
	 * @Value("${egov.user.create.path}") private String userCreateEndpoint;
	 * 
	 * @Value("${egov.user.search.path}") private String userSearchEndpoint;
	 * 
	 * @Value("${egov.user.update.path}") private String userUpdateEndpoint;
	 * 
	 * // NOTIFICATION TOPICS
	 * 
	 * @Value("${kafka.topics.notification.sms}") private String smsNotifTopic;
	 * 
	 * @Value("${kafka.topics.notification.email}") private String emailNotifTopic;
	 * 
	 * @Value("${kafka.topics.receipt.create}") private String receiptCreateTopic;
	 * 
	 * @Value("${egov.localization.statelevel}") private Boolean isStateLevel;
	 * 
	 * @Value("${notif.sms.enabled}") private Boolean isSMSNotificationEnabled;
	 * 
	 * @Value("${notif.email.enabled}") private Boolean isEmailNotificationEnabled;
	 * 
	 * // Localization
	 * 
	 * @Value("${egov.localization.host}") private String localizationHost;
	 * 
	 * @Value("${egov.localization.context.path}") private String
	 * localizationContextPath;
	 * 
	 * @Value("${egov.localization.search.endpoint}") private String
	 * localizationSearchEndpoint;
	 * 
	 * @Value("${egov.localization.fallback.locale}") private String fallBackLocale;
	 * 
	 * // Billing-Service
	 * 
	 * @Value("${egbs.host}") private String egbsHost;
	 * 
	 * @Value("${egbs.fetchbill.endpoint}") private String egbsFetchBill;
	 * 
	 * @Value("${egov.localization.statelevel}") private Boolean
	 * isLocalizationStateLevel;
	 */

	/** Used Parameters **/

	@Value("${egov.idgen.street-vending.application.id.name}")
	private String streetVendingApplicationKey;

	@Value("${egov.idgen.street-vending.application.id.format}")
	private String streetVendingApplicationFormat;

	@Value("${persister.create.street-vending.topic}")
	private String streetVendingApplicationSaveTopic;

	@Value("${persister.init.street-vending.topic}")
	private String streetVendingApplicationInitSaveTopic;

	@Value("${persister.update.street-vending.topic}")
	private String streetVendingApplicationUpdateTopic;

	@Value("${sv.module.name}")
	private String moduleName;

	@Value("${sv.business.service.name}")
	private String businessServiceName;

	// MDMS Config
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsPath;

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

}
