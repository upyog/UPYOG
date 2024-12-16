package org.upyog.sv.web.models;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.web.models.common.AuditDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class StreetVendingDraftDetail {
	
	private String draftId;

	private String tenantId;

	private String userUuid;

	private String draftApplicationData;

	private AuditDetails auditDetails;

}
