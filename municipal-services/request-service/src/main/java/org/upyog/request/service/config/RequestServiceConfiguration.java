package org.upyog.request.service.config;

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


}
