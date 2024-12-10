package org.upyog.adv.web.models;

import org.springframework.validation.annotation.Validated;

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
public class AdvertisementDraftDetail {
	
	private String draftId;

	private String tenantId;

	private String userUuid;

	private String draftApplicationData;

	private AuditDetails auditDetails;

}
