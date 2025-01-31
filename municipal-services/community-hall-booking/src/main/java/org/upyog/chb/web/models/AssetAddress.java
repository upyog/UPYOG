package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetAddress {


	private String type;


	private String street;

	private String addressLine1;

	private String landmark;

	@NotBlank
	private String city;






	@NotBlank
	private String pincode;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;

}
