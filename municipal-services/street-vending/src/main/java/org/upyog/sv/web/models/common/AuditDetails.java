package org.upyog.sv.web.models.common;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.validator.CreateApplicationGroup;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD

import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Collection of audit related fields used by most models
 */
<<<<<<< HEAD
@Schema(description = "Collection of audit related fields used by most models")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "Collection of audit related fields used by most models")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails {

	@NotNull(groups = CreateApplicationGroup.class)
	@JsonProperty("createdBy")
	private String createdBy = null;

	@JsonProperty("lastModifiedBy")
	private String lastModifiedBy = null;

	@NotNull(groups = CreateApplicationGroup.class)
	@JsonProperty("createdTime")
	private Long createdTime = null;

	@JsonProperty("lastModifiedTime")
	private Long lastModifiedTime = null;

	public AuditDetails(AuditDetails auditDetails) {

		this.createdBy = auditDetails.createdBy;
		this.lastModifiedBy = auditDetails.lastModifiedBy;
		this.createdTime = auditDetails.createdTime;
		this.lastModifiedTime = auditDetails.lastModifiedTime;

	}

}
