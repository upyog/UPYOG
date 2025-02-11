package org.egov.pqm.anomaly.finder.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

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
public class PqmAnomalyConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Value("${save.test.anomaly.details}")
	private String saveTestAnomalyTopic;

	// Persister Config
	@Value("${persister.save.pqm.topic}")
	private String saveTopic;

	@Value("${egov.event.notification.enabled}")
	private Boolean isEventsNotificationEnabled;

	// USER EVENTS
	@Value("${egov.ui.app.host}")
	private String uiAppHost;

	@Value("${egov.usr.events.create.topic}")
	private String saveUserEventsTopic;

	// User Config
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	// Localization
	@Value("${egov.localization.host}")
	private String localizationHost;

	@Value("${egov.localization.context.path}")
	private String localizationContextPath;

	@Value("${egov.localization.search.endpoint}")
	private String localizationSearchEndpoint;

	@Value("${egov.localization.statelevel}")
	private Boolean isLocalizationStateLevel;

	@Value("${egov.usr.events.view.application.link}")
	private String testLink;

	@Value("${egov.usr.events.view.code}")
	private String viewCode;
	
	@Value("${egov.pqmAnomaly.max.limit}")
	private Integer maxSearchLimit;
	
	@Value("${persister.save.pqm.topic}")
	private String notAsPerBenchMark;
	
	@Value("${egov.pqm.anomaly.testNotSubmitted.kafka.topic}")
	private String testNotSubmitted;

}
