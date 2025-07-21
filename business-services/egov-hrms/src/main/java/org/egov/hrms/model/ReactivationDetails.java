package org.egov.hrms.model;

import lombok.*;
import org.egov.hrms.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Validated
@EqualsAndHashCode(exclude = {"auditDetails"})
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class ReactivationDetails {

	@SanitizeHtml
	private String id;

	@SanitizeHtml
	@NotNull
	private String reasonForReactivation;

	@SanitizeHtml
	private String orderNo;

	@SanitizeHtml
	private String remarks;

	@NotNull
	private Long effectiveFrom;

	@SanitizeHtml
	private String tenantId;

	private AuditDetails auditDetails;




}


