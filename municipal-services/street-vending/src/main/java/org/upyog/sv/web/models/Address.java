package org.upyog.sv.web.models;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {
	
		private String addressId;
		
		private String vendorId;

        private String doorNo;

        private String houseNo;
        
        private String streetName;

        private String addressLine1;
        
        private String addressLine2;

        private String landmark;

        @NotBlank
        private String city;
        
        @NotBlank
        private String cityCode;

        @NotBlank
        private String locality;

        @NotBlank
        private String localityCode;
        
        @NotBlank
        private String pincode;
        
        //Permanent, Correspondence, both
        private String addressType;
        
}

