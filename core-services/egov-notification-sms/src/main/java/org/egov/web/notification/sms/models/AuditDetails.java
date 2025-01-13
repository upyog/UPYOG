package org.egov.web.notification.sms.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Collection of audit related fields used by most models
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails {

	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("createdTime")
	private Long createdTime;

	@JsonProperty("lastModifiedBy")
	private String lastModifiedBy;

	@JsonProperty("lastModifiedTime")
	private Long lastModifiedTime;
}
