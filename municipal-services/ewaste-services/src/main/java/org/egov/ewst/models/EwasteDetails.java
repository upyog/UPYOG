package org.egov.ewst.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "Details of the pet for pet registration")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EwasteDetails {

	@JsonProperty("id")
	private String id = null;

	@JsonProperty("productId")
	private String productId = null;

	@JsonProperty("productName")
	private String productName = null;

	@JsonProperty("quantity")
	private String quantity = null;

	@JsonProperty("price")
	private String price = null;

}
