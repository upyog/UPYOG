package org.upyog.cdwm.calculator.config;

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

	@Value("${egov.fsm.appl.fee.taxhead}")
	private String applicationFeeTaxHead;

	@Value("${egov.fsm.appl.fee.businesssrv}")
	private String applicationFeeBusinessService;

	// tradelicense Registry
	@Value("${egov.cnd.host}")
	private String cndHost;

	@Value("${egov.cnd.context.path}")
	private String cndContextPath;

	@Value("${egov.cnd.create.endpoint}")
	private String cndCreateEndpoint;

	@Value("${egov.cnd.update.endpoint}")
	private String cndUpdateEndpoint;

	@Value("${egov.cnd.search.endpoint}")
	private String cndSearchEndpoint;

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsUrl;

	@Value("${egov.mdms.master.name}")
	private String masterName;

	@Value("${upyog.cnd.calculator.module.name}")
	private String moduleName;
	
	@Value("${upyog.cnd.calculator.businessservice.name}")
	private String businessserviceName;
	
	@Value("${egov.bill.fetch.endpoint}")
	private String fetchBillEndpoint;

}
