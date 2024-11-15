package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class QualityCriteria {

	@JsonProperty("id")
	private String id;

	@JsonProperty("testId")
	private String testId;

	@JsonProperty("criteriaCode")
	private String criteriaCode = null;

	@JsonProperty("resultValue")
	private BigDecimal resultValue = null;

	@JsonProperty("allowedDeviation")
	private BigDecimal allowedDeviation = null;

	@JsonProperty("resultStatus")
	private TestResultStatus resultStatus = null;

	@JsonProperty("isActive")
	private Boolean isActive = Boolean.TRUE;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
}
