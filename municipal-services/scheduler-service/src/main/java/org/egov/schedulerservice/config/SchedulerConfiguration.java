package org.egov.schedulerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class SchedulerConfiguration {

	@Value("${state.level.tenant.id}")
	private String stateLevelTenantId;

	@Value("${egov.user.host}")
	private String userServiceHostUrl;

	@Value("${egov.user.search.endpoint}")
	private String userSearchEndpoint;

	@Value("${egov.garbage.context.host}")
	private String garbageServiceHostUrl;

	@Value("${egov.garbage.endpoint.bill-generator}")
	private String garbageBillGeneratorEndpoint;

	@Value("${egov.bill.context.host}")
	private String billServiceHostUrl;

	@Value("${egov.bill.endpoint.bill-expiry}")
	private String billExpityEndpoint;

	@Value("${egov.property.context.host}")
	private String propertyServiceHostUrl;

	@Value("${egov.property.endpoint.tax-calculator}")
	private String propertyTaxCalculatorEndpoint;

	@Value("${egov.pgr.context.host}")
	private String pgrServiceHostUrl;

	@Value("${egov.pgr.endpoint.request-escalator}")
	private String pgrRequestEscalatorEndpoint;

	@Value("${egov.pgr.endpoint.notification-sender}")
	private String pgrNotificationSenderEndpoint;

	@Value("${egov.pgr.endpoint.delete-notification}")
	private String pgrDeleteNotificationEndpoint;

}
