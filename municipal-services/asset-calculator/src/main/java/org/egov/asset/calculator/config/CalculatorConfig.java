package org.egov.asset.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class CalculatorConfig {

	@Value("${egov.billingservice.host}")
	private String billingHost;

	@Value("${egov.taxhead.search.endpoint}")
	private String taxHeadSearchEndpoint;

	@Value("${egov.taxperiod.search.endpoint}")
	private String taxPeriodSearchEndpoint;

	@Value("${egov.demand.create.endpoint}")
	private String demandCreateEndpoint;

	@Value("${egov.demand.update.endpoint}")
	private String demandUpdateEndpoint;

	@Value("${egov.demand.search.endpoint}")
	private String demandSearchEndpoint;

	@Value("${egov.bill.gen.endpoint}")
	private String billGenerateEndpoint;

	@Value("${egov.asset.appl.fee.taxhead}")
	private String applicationFeeTaxHead;

	@Value("${egov.asset.appl.fee.businesssrv}")
	private String applicationFeeBusinessService;

	// tradelicense Registry
	@Value("${egov.asset.host}")
	private String fsmHost;

	@Value("${egov.asset.context.path}")
	private String fsmContextPath;

	@Value("${egov.asset.create.endpoint}")
	private String fsmCreateEndpoint;

	@Value("${egov.asset.update.endpoint}")
	private String fsmUpdateEndpoint;

	@Value("${egov.asset.search.endpoint}")
	private String fsmSearchEndpoint;

	@Value("${advance.payment.type}")
	private String advancePaymentType;

	@Value("${cancellation.fee.type}")
	private String cancellationFeeType;

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

	@Value("${egov.bill.fetch.endpoint}")
	private String fetchBillEndpoint;

}
