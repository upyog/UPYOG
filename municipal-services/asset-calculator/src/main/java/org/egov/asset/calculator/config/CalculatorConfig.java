package org.egov.asset.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class CalculatorConfig {

	// tradelicense Registry
	@Value("${egov.asset.host}")
	private String assetHost;

	@Value("${egov.asset.context.path}")
	private String assetContextPath;

	@Value("${egov.asset.create.endpoint}")
	private String assetCreateEndpoint;

	@Value("${egov.asset.update.endpoint}")
	private String assetUpdateEndpoint;

	@Value("${egov.asset.search.endpoint}")
	private String assetSearchEndpoint;


	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

}
