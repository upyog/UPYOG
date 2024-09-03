package org.egov.advertisementcanopy.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = { "filestoreid", "documentuid", "id" })
public class Document {

	@Size(max = 64)
	@JsonProperty("id")
	private String id;

	@JsonProperty("active")
	private Boolean active;

	@Size(max = 64)
	@JsonProperty("tenantId")
	private String tenantId = null;

	@Size(max = 64)
	@NotNull
	@JsonProperty("documentType")
	private String documentType = null;

	@Size(max = 64)
	@JsonProperty("filestoreId")
	private String filestoreId = null;

	@Size(max = 64)
	@JsonProperty("documentUid")
	private String documentUid;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

}
