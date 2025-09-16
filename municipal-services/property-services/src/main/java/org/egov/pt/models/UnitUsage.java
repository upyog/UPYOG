package org.egov.pt.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.egov.tracer.annotations.CustomSafeHtml;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitUsage {

	@CustomSafeHtml
	@JsonProperty("id")
	private String id;

	@CustomSafeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("unitId")
	@CustomSafeHtml
	@NotNull
	private String unitId;

	@JsonProperty("usageCategory")
	@CustomSafeHtml
	@NotNull
	private String usageCategory;

	@JsonProperty("occupancyType")
	@CustomSafeHtml
	@NotNull
	private String occupancyType;

	@JsonProperty("occupancyDate")
	private Long occupancyDate;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
