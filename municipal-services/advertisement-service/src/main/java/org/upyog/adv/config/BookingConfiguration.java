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

	
	//Module and business name
	@Value("${adv.module.name}")
	private String moduleName;
	
	@Value("${adv.business.service.name}")
	private String businessServiceName;
	
	@Value("${adv.calculation.tax.applicable}")
	private String applicableTaxes;
	
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
	
	

}