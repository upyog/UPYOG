package org.egov.asset.config;

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


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@Import({TracerConfiguration.class})
public class AssetConfiguration {

    @Value("${app.timezone}")
    private String timeZone;
    
    // MDMS Related Configurations
 	@Value("${egov.mdms.host}")
 	private String mdmsHost;

 	@Value("${egov.mdms.search.endpoint}")
 	private String mdmsEndPoint;
 	
 	// Idgen Config
 	@Value("${egov.idgen.host}")
 	private String idGenHost;

 	@Value("${egov.idgen.path}")
 	private String idGenPath;

 	@Value("${egov.idgen.asset.applicationNum.name}")
 	private String applicationNoIdgenName;

 	@Value("${egov.idgen.asset.applicationNum.format}")
 	private String applicationNoIdgenFormat;

 	
 	// Persister Config
 	@Value("${persister.save.assetdetails.topic}")
 	private String saveTopic;

 	@Value("${persister.update.assetdetails.topic}")
 	private String updateTopic;
 	
 	@Value("${persister.save.assetassignment.topic}")
 	private String saveAssignmentTopic;

 	@Value("${persister.update.assetassignment.topic}")
 	private String updateAssignmentTopic;
 	
	@Value("${employee.allowed.search.params}")
	private String allowedEmployeeSearchParameters;
	
	@Value("${egov.asset.default.limit}")
	private Integer defaultLimit;

	@Value("${egov.asset.default.offset}")
	private Integer defaultOffset;

	@Value("${egov.asset.max.limit}")
	private Integer maxSearchLimit;
	
	@Value("${workflow.context.path}")
	private String wfHost;

	@Value("${workflow.transition.path}")
	private String wfTransitionPath;
	
	@Value("${workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;
	
	@Value("${workflow.process.path}")
	private String wfProcessPath;
 	
	// SMS
//	@Value("${kafka.topics.notification.sms}")
//	private String smsNotifTopic;
//
//	@Value("${notification.sms.enabled}")
//	private Boolean isSMSEnabled;
//
//	// Email
//	@Value("${kafka.topics.notification.email}")
//	private String emailNotifTopic;
//
//	@Value("${notification.email.enabled}")
//	private Boolean isEmailNotificationEnabled;

	// Localization
//	@Value("${egov.localization.host}")
//	private String localizationHost;
//
//	@Value("${egov.localization.context.path}")
//	private String localizationContextPath;
//
//	@Value("${egov.localization.search.endpoint}")
//	private String localizationSearchEndpoint;
//
//	@Value("${egov.localization.statelevel}")
//	private Boolean isLocalizationStateLevel;
//
//	@Value("${egov.localization.fallback.locale}")
//	private String fallBackLocale;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @Bean
    public ObjectMapper objectMapper(){
    return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setTimeZone(TimeZone.getTimeZone(timeZone));
    }

    @Bean
    @Autowired
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);
    return converter;
    }

	
}