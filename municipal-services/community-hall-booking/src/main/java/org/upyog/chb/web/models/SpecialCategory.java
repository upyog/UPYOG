package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Category of applicants for community hall booking
 */
@ApiModel(description = "Category of applicants for community hall booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

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

