package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Document details of uploaded documents
 */
@ApiModel(description = "Document details of uploaded documents")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDetails {
	@JsonProperty("documentId")
	private String documentId = null;
	
	private String bookingId;

	@NotBlank
	@JsonProperty("documentType")
	private String documentType = null;
	
	@NotBlank
	@JsonProperty("fileStoreId")
	private String fileStoreId = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

}
