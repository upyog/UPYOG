package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {

	@JsonProperty("id")
	private String id = null;

	@JsonProperty("testId")
	private String testId = null;

	@JsonProperty("documentType")
	private String documentType = null;

	@JsonProperty("documentUid")
	private String documentUid = null;

	@JsonProperty("documentUri")
	private String documentUri = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("fileStoreId")
	private String fileStoreId;

	@JsonProperty("isActive")
	private boolean isActive;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
