package org.upyog.cdwm.web.models;

import digit.models.coremodels.AuditDetails;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * Document details of uploaded documents
 */
@ApiModel(description = "Document details of uploaded documents")
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class DocumentDetail {
	
	private String documentDetailId;
	
	private String applicationId;

	@NotBlank
	private String documentType;

	@NotBlank
	private String uploadedByUserType;
	
	@NotBlank
	private String fileStoreId;

	private AuditDetails auditDetails;

}
