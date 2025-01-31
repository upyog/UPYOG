package org.upyog.rs.config;

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
public class RequestServiceConfiguration {

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

	@Value("${egov.idgen.water.tanker.booking.id.name}")
	private String waterTankerApplicationKey;

	@Value("${egov.idgen.water.tanker.booking.id.format}")
	private String waterTankerApplicationFormat;

	// Kafka topics for saving street vending data
	@Value("${persister.create.water-tanker.topic}")
	private String waterTankerApplicationSaveTopic;

	@Value("${persister.update.water-tanker.topic}")
	private String waterTankerApplicationUpdateTopic;

	// Idgen Config
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${egov.url.shortner.endpoint}")
	private String urShortnerPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	// Pagination config for search results
	@Value("${upyog.request.service.default.limit}")
	private Integer defaultLimit;

	@Value("${upyog.request.service.default.offset}")
	private Integer defaultOffset;

	@Value("${upyog.request.service.max.limit}")
	private Integer maxSearchLimit;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

	@Value("${upyog.mdms.v2.host}")
	private String mdmsV2Host;

	@Value("${upyog.mdms.v2.search.endpoint}")
	private String mdmsV2Endpoint;

	@Value("${upyog.mdms.v2.enabled}")
	private boolean mdmsV2Enabled;

	@PostConstruct
	public void init() {
		if (mdmsV2Enabled) {
			mdmsHost = mdmsV2Host;
			mdmsEndpoint = mdmsV2Endpoint;
		}
	}

	// Billing Service configs
	@Value("${egov.billingservice.host}")
	private String billingHost;

	@Value("${egov.demand.create.endpoint}")
	private String demandCreateEndpoint;

	@Value("${egov.demand.update.endpoint}")
	private String demandUpdateEndpoint;

	@Value("${egov.demand.search.endpoint}")
	private String demandSearchEndpoint;
}
