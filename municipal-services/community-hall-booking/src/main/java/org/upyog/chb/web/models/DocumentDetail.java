package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

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
public class DocumentDetail {
	
	private String documentDetailId;
	
	private String bookingId;

	@NotBlank
	private String documentType;
	
	@NotBlank
	private String fileStoreId;

	private AuditDetails auditDetails;

}
