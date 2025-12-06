package org.upyog.adv.web.models;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
=======
import javax.validation.constraints.NotBlank;
>>>>>>> master-LTS

import org.springframework.validation.annotation.Validated;
import org.upyog.adv.validator.CreateApplicationGroup;

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
<<<<<<< HEAD
 * Representation of  address. Indiavidual APIs may choose to extend from this
 * using allOf if more details needed to be added in their case.
 */
@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
 * Representation of a address. Indiavidual APIs may choose to extend from this
 * using allOf if more details needed to be added in their case.
 */
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

	private String addressId;

	private String applicantDetailId;

	private String doorNo;
	@NotBlank(groups = CreateApplicationGroup.class)
	private String houseNo;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	private String streetName;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	private String addressLine1;

	private String addressLine2;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	private String landmark;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String city;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String cityCode;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String locality;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String localityCode;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String pincode;

}
