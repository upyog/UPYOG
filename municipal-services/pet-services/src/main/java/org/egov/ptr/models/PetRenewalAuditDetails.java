package org.egov.ptr.models;

import org.springframework.validation.annotation.Validated;

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

<<<<<<< HEAD
@Schema(description = "Details of the renewal audit timeline details")
=======
@ApiModel(description = "Details of the renewal audit timeline details")
>>>>>>> master-LTS
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
