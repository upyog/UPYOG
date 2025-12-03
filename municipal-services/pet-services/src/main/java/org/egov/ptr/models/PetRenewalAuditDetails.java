package org.egov.ptr.models;

import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "Details of the renewal audit timeline details")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetRenewalAuditDetails {

	private String id = null;

	private String tokenNumber = null;

	private String applicationNumber = null;

	private String previousapplicationnumber = null;

	private Long expiryDate = null;

	private Long renewalDate = null;
	
	private String petRegistrationId = null;

}
