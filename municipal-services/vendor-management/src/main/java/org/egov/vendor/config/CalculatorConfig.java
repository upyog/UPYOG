package org.egov.vendor.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CalculatorConfig {

	@Value("${scheduler.batch.size}")
	private int batchSize;

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

	@Value("${default.tenant.id}")
	private String defaultTenantId;

}
