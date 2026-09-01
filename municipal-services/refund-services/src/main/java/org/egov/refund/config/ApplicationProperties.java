package org.egov.refund.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class ApplicationProperties {

	@Value("${pg.service.host}")
	private String pghost;

	@Value("${pg.initiate.refund.endpoint}")
	private String pgrefundEndpoint;

	@Value("${refund.receipts.search.default.size}")
	private Integer searchDefaultLimit;

	@Value("${refund.receipts.search.max.size}")
    private Integer searchMaxLimit;
	
	@Value("${workflow.wf-host}")
	private String wfHost;

	@Value("${workflow.wf-transition-path}")
	private String wfTransitionPath;
	
	@Value("${idgen.host}")
	private String idgenHost;
	
	@Value("${idgen.endpoint}")
	private String idgenEndpoint;
	
	@Value("${refund.idgen.name}")
	private String refundIdgenName;
	
	@Value("${refund.idgen.format}")
	private String refundIdgenFormat;
	
	@Value("${egov.refund.finance-topic}")
	private String financeTopic;
	
	@Value("${egov.system.user.uuid}")
	private String systemUUid;
	
	@Value("${refund.workflow.send-to-finance}")
	private boolean sendToFinance;

}
