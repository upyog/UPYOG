package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetAddress {

	private String addressId;

	private String applicantDetailId;

	private String doorNo;

	private String houseNo;

	private String streetName;

	private String addressLine1;

	private String landmark;

	@NotBlank
	private String city;

	@NotBlank
	private String cityCode;

	@JsonProperty("locality")
	private Boundary locality = null;

	@NotBlank
	private String localityCode;

	@NotBlank
	private String pincode;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;

}
