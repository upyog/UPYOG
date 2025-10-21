package org.egov.ndc.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class NDCConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${notification.sms.enabled}")
	private Boolean isSMSEnabled;

	@Value("${egov.localization.host}")
	private String localizationHost;

	@Value("${egov.localization.context.path}")
	private String localizationContextPath;

	@Value("${egov.localization.search.endpoint}")
	private String localizationSearchEndpoint;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;
	
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.ndc.application.id}")
	private String applicationNoIdgenName;
	
	@Value("${workflow.context.path}")
	private String wfHost;

	@Value("${workflow.transition.path}")
	private String wfTransitionPath;

	@Value("${workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	@Value("${workflow.process.path}")
	private String wfProcessPath;
		
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndPoint;
	
	@Value("${persister.save.ndc.topic}")
	private String saveTopic;

	@Value("${persister.update.ndc.topic}")
	private String updateTopic;

	@Value("${persister.delete.ndc.topic}")
	private String deleteTopic;
	
	@Value("${egov.ndc.pagination.default.limit}")
	private Integer defaultLimit;

	@Value("${egov.ndc.pagination.default.offset}")
	private Integer defaultOffset;

	@Value("${egov.ndc.pagination.max.limit}")
	private Integer maxSearchLimit;
	
	@Value("${ndc.offline.doc.required}")
	private Boolean ndcOfflineDocRequired;

	@Value("${ndc.module.code}")
	private String moduleCode;

	@Value("${ndc.taxhead.master.code}")
	private String taxHeadMasterCode;

	@Value("${egov.billingservice.host}")
	private String billingServiceHost;

	@Value("${egov.demand.create.endpoint}")
	private String demandCreateEndpoint;

	@Value("${egov.billingservice.fetch.bill}")
	private String fetchBillEndpoint;

	@Value("${egov.ndccalculator.host}")
	private String ndcCalculatorHost;

	@Value("${egov.ndccalculator.endpoint}")
	private String ndcCalculatorEndpoint;

	@Value("${spring.kafka.consumer.group-id}")
	private String kafkaGroupId;

	@Value("${workflow.process.path}")
	private String wfProcessSearchPath;
}
