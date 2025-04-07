package org.egov.garbageservice.model.contract;

import javax.validation.constraints.NotNull;

import org.egov.garbageservice.enums.Status;
import org.egov.garbageservice.model.AuditDetails;

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
@EqualsAndHashCode(of = { "fileStoreId", "documentUid", "id" })
public class Document {

	@JsonProperty("id")
	private String id;

	@JsonProperty("documentType")
	@NotNull
	private String documentType;

	@JsonProperty("fileStoreId")
	@NotNull
	private String fileStoreId;

	@JsonProperty("documentUid")
	private String documentUid;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("status")
	private Status status;
}
