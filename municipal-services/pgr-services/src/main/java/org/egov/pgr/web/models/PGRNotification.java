package org.egov.pgr.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PGRNotification {

	private String uuid;
	private String serviceRequestId;
	private String tenantId;
	private String applicationStatus;
	private String recipientName;
	private String emailId;
	private String mobileNumber;
	@Builder.Default
	private Boolean isEmailSent = false;
	@Builder.Default
	private Boolean isSmsSent = false;
	private AuditDetails auditDetails;

}
