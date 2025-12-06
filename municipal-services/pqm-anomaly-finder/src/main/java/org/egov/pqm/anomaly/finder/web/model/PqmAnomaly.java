package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PqmAnomaly {

	@JsonProperty("id")
	private String id = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("testId")
	private String testId = null;

	@JsonProperty("anomalyType")
	private AnomalyType anomalyType = null;

	@JsonProperty("plantCode")
	private String plantCode = null;

	@JsonProperty("description")
	private String description = null;

	@JsonProperty("referenceId")
	private String referenceId = null;

	@JsonProperty("resolutionStatus")
	private String resolutionStatus = null;

	@JsonProperty("isActive")
	private Boolean isActive = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

}
