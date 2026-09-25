package org.upyog.adv.web.models;

import jakarta.validation.constraints.NotBlank;


import org.upyog.adv.validator.CreateApplicationGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Document details of uploaded documents
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDetail {
	
	private String documentDetailId;
	
	private String bookingId;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String documentType;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	private String fileStoreId;

	private AuditDetails auditDetails;

}
