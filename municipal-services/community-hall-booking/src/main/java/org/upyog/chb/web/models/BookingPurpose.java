package org.upyog.chb.web.models;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Purpose for which community hall booking is allowed
 */
@Schema(description = "Purpose for which community hall booking is allowed")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingPurpose   {
		@NotBlank
        @JsonProperty("purpose")
        private String purpose = null;
        
        @JsonProperty("discountRate")
        private Integer discountRate = null;


}

