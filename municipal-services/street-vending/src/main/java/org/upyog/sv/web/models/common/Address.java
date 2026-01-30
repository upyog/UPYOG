package org.upyog.sv.web.models.common;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

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
        
        private String addressType;
        
}

