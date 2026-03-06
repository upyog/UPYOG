package org.egov.pt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentData {

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
}
