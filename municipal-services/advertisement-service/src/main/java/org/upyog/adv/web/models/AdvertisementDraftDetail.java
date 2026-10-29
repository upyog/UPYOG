package org.upyog.adv.web.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain model class used by advertisement service requests and responses.
 */
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
