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
 * Category of applicants for community hall booking
 */
<<<<<<< HEAD
@Schema(description = "Category of applicants for community hall booking")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "Category of applicants for community hall booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecialCategory   {
		@NotBlank
        @JsonProperty("category")
        private String category = null;

        @JsonProperty("discountRate")
        private Integer discountRate = null;


}

