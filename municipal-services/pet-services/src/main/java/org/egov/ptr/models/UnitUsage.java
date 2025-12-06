package org.egov.ptr.models;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.ptr.validator.SanitizeHtml;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitUsage {

	@SanitizeHtml
	@JsonProperty("id")
	private String id;

	@SanitizeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("unitId")
	@SanitizeHtml
	@NotNull
	private String unitId;

	@JsonProperty("usageCategory")
	@SanitizeHtml
	@NotNull
	private String usageCategory;

	@JsonProperty("occupancyType")
	@SanitizeHtml
	@NotNull
	private String occupancyType;

	@JsonProperty("occupancyDate")
	private Long occupancyDate;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
