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

	@Value("${egov.garbage.context.host}")
	private String garbageServiceHostUrl;

	@Value("${egov.garbage.endpoint.bill-generator}")
	private String garbageBillGeneratorEndpoint;

	@Value("${egov.bill.context.host}")
	private String billServiceHostUrl;

	@Value("${egov.bill.endpoint.bill-expiry}")
	private String billExpityEndpoint;

}
