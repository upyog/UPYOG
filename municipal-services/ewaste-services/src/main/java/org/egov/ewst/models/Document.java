package org.egov.ewst.models;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.SafeHtml;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = { "filestoreid", "documentuid", "id" })
public class Document {

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("id")
	private String id;

	@JsonProperty("active")
	private Boolean active;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("tenantId")
	private String tenantId = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("documentType")
	private String documentType = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("filestoreId")
	private String filestoreId = null;

	@Size(max = 64)
	@SafeHtml
	@JsonProperty("documentUid")
	private String documentUid;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

}
