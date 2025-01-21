package org.egov.web.notification.sms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Builder
public class SMSTemplate {

	private String id;
	private String templateName;
	private String templateId;
	private String smsBody;
	private String tenantId;
	private Boolean isActive;
	private AuditDetails auditDetails;

}
