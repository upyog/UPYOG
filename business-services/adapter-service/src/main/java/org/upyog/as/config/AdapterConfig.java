package org.upyog.as.config;

import java.util.TimeZone;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;

/**
 * Holds adapter configuration values and initializes shared runtime settings at startup.
 */
@Component
@Import({ TracerConfiguration.class })
@Data
public class AdapterConfig {
	@Value("${app.timezone}")
	public String timeZone;

	@Value("${egov.mdms.host}")
	public String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	public String mdmsPath;

	@Value("${upyog.mdms.v2.host}")
	public String mdmsV2Host;

	@Value("${upyog.mdms.v2.search.endpoint}")
	public String mdmsV2Path;

	@Value("${upyog.mdms.v2.enabled}")
	public boolean mdmsV2Enabled;

	@Value("${employee.allowed.search.params}")
	public String allowedEmployeeSearchParameters;

	@Value("${egov.user.host}")
	public String userHost;

	@Value("${egov.user.context.path}")
	public String userContextPath;

	@Value("${egov.user.create.path}")
	public String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	public String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	public String userUpdateEndpoint;

	@Value("${as.module.name}")
	public String moduleName;

	@Value("${as.business.service.name}")
	public String businessServiceName;

	@Value("${state.level.tenant.id}")
	public String stateLevelTenantId;

	/**
	 * Initializes the JVM default timezone and applies the adapter bootstrap logic.
	 */
	@PostConstruct
	public void initialize() {
		if (timeZone != null) {
			TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
		}
		init();
	}

	/**
	 * Switches the MDMS settings to the V2 endpoints when enabled.
	 */
	public void init() {
		if (mdmsV2Enabled) {
			mdmsHost = mdmsV2Host;
			mdmsPath = mdmsV2Path;
		}
	}
}
