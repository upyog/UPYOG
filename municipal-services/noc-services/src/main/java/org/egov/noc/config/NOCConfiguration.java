package org.egov.noc.config;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

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
public class NOCConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	// User Config
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	// SMS
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;

	@Value("${notification.sms.enabled}")
	private Boolean isSMSEnabled;

	// Localization
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

	@Value("${egov.idgen.noc.application.id}")
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
	
	@Value("${persister.save.noc.topic}")
	private String saveTopic;
	
	@Value("${persister.update.noc.topic}")
	private String updateTopic;
	
	@Value("${persister.update.noc.workflow.topic}")
	private String updateWorkflowTopic;
	
	@Value("${egov.noc.pagination.default.limit}")
	private Integer defaultLimit;

	@Value("${egov.noc.pagination.default.offset}")
	private Integer defaultOffset;

	@Value("${egov.noc.pagination.max.limit}")
	private Integer maxSearchLimit;
	
	@Value("${noc.offline.doc.required}")
	private Boolean nocOfflineDocRequired;

	//bpa configuration
    @Value("${egov.bpa.host}")
    private String bpaHost;

    @Value("${egov.bpa.context.path}")
    private String bpaContextPath;

    @Value("${egov.bpa.search.endpoint}")
    private String bpaSearchEndpoint;

	// AAI NOCAS Integration Configuration
	@Value("${aai.nocas.api.url}")
	private String aaiNocasApiUrl;

	@Value("${aai.nocas.token.key}")
	private String aaiNocasTokenKey;

	@Value("${aai.nocas.timeout}")
	private Integer aaiNocasTimeout;

	@Value("${aai.nocas.enabled}")
	private Boolean aaiNocasEnabled;

	// AAI NOCAS Authority credentials (for JSON status API)
	@Value("${aai.nocas.authority.id}")
	private String aaiNocasAuthorityId;

	@Value("${aai.nocas.authority.key}")
	private String aaiNocasAuthorityKey;

	// AAI NOCAS Filter Search API URL (for single UNIQUE ID search)
	@Value("${aai.nocas.filter.search.api.url:}")
	private String aaiNocasFilterSearchApiUrl;

	// Scheduler Configuration
	@Value("${scheduler.aai.noc.status.sync.enabled}")
	private Boolean schedulerEnabled;

	@Value("${scheduler.aai.noc.status.sync.cron}")
	private String schedulerCron;

	@Value("${state.code}")
	private String assamStateCode;

	// Internal microservice user (AAI internal user for scheduler / internal calls)
	@Value("${internal.microservice.user.username}")
	private String internalMicroserviceUserName;

	@Value("${internal.microservice.user.type}")
	private String internalMicroserviceUserType;

	@Value("${nocas.authority.name}")
	private String authorityName;

//	@Value("${nocas.authority.placeholder.fileurl}")
//	private String authorityPlaceholderFileUrl;

	// EDCR Configuration
	@Value("${egov.edcr.host}")
	private String edcrHost;

	@Value("${egov.edcr.scrutiny.details.endpoint}")
	private String edcrScrutinyDetailsEndpoint;

	@Value("${egov.edcr.tenant.id}")
	private String edcrTenantId;

	// FileStore Configuration
	@Value("${egov.filestore.host}")
	private String fileStoreHost;

	@Value("${egov.filestore.path}")
	private String fileStorePath;

	// Fire NOC Validation Configuration
	@Value("${fire.noc.api.url:}")
	private String fireNocApiUrl;

	@Value("${fire.noc.api.session.cookie:}")
	private String fireNocApiSessionCookie;

	@Value("${filestore.upload.path}")
	private String fileStoreUploadPath;
}
