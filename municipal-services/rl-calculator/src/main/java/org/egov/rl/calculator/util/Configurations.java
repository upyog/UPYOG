package org.egov.rl.calculator.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Getter
@Setter
public class Configurations {

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

	// billing service
	@Value("${egov.billingservice.host}")
	private String billingServiceHost;

	@Value("${egov.taxhead.search.endpoint}")
	private String taxheadsSearchEndpoint;

	@Value("${egov.taxperiod.search.endpoint}")
	private String taxPeriodSearchEndpoint;

	@Value("${egov.demand.create.endpoint}")
	private String demandCreateEndPoint;

	@Value("${egov.demand.update.endpoint}")
	private String demandUpdateEndPoint;

	@Value("${egov.demand.search.endpoint}")
	private String demandSearchEndPoint;

	@Value("${egov.bill.gen.endpoint}")
	private String billGenEndPoint;

	@Value("${egov.collectionservice.host}")
	private String collectionServiceHost;

	@Value("${egov.receipt.search.endpoint}")
	private String ReceiptSearchEndpoint;

	@Value("${egov.payment.search.endpoint}")
	private String PaymentSearchEndpoint;

	@Value("${egov.bill.search.endpoint}")
	private String billSearchEndPoint;

	@Value("${state.level.tenant.id}")
	private String stateLevelTenantId;
	
	@Value("${egov.rl.service.host}")
	private String rlServiceHost;
	@Value("${egov.rl.search.endpoint}")
	private String rlSearchEndpoint;
	@Value("${demand.generation.batch.size}")
	private Integer demandGenerationBatchSize;
	
	  //USER
    @Value("${egov.user.host}")
    private String userHost;
    
    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;
    

    @Value("${egov.internal.microservice.user.uuid}")
    private String egovInternalMicroserviceUserUuid;
    
    //NOTIFICATION TOPICS
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
    
    //Localization

    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

    @Value("${egov.localization.fallback.locale}")
    private String fallBackLocale;
    

    @Value("${egov.localization.statelevel}")
    private Boolean isLocalizationStateLevel;
    
    @Value("${rl.batch.demand.size}")
    private int demandBatchSize;
    
 // Workflow

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
}
