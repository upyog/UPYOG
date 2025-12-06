package org.upyog.chb.web.models;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
=======
import javax.validation.constraints.NotBlank;
>>>>>>> master-LTS

import org.springframework.validation.annotation.Validated;

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
 * Purpose for which community hall booking is allowed
 */
<<<<<<< HEAD
@Schema(description = "Purpose for which community hall booking is allowed")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "Purpose for which community hall booking is allowed")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

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

