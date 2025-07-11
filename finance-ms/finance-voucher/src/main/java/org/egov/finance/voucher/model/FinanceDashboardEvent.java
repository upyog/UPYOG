package org.egov.finance.voucher.model;

import org.egov.finance.voucher.enumeration.FinanceEventType;
import org.springframework.context.ApplicationEvent;

import lombok.Data;

@Data
public class FinanceDashboardEvent extends ApplicationEvent {

	Object data;
	FinanceEventType eventType;
	String tenantId;
	String token;
	String domainName;

	public FinanceDashboardEvent(Object source, Object data, FinanceEventType eventType, String tenantId, String token,
			String domainName) {
		super(source);
		this.data = data;
		this.eventType = eventType;
		this.tenantId = tenantId;
		this.token = token;
		this.domainName = domainName;
	}

}
