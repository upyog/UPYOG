package org.egov.pt.models;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representation of a address. Individual APIs may choose to extend from this
 * using allOf if more details needed to be added in their case.
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("doorNo")
	private String doorNo;

	@JsonProperty("plotNo")
	private String plotNo;

	@JsonProperty("id")
	private String id;

	@JsonProperty("landmark")
	private String landmark;

	@JsonProperty("city")
	private String city;

	@JsonProperty("district")
	private String district;

	@JsonProperty("region")
	private String region;

	@JsonProperty("state")
	private String state;

	@JsonProperty("country")
	private String country;

	@JsonProperty("pincode")
	private String pincode;

	@JsonProperty("buildingName")
	private String buildingName;

	@JsonProperty("street")
	private String street;

	@JsonProperty("locality")
	private Locality locality;

	@JsonProperty("geoLocation")
	private GeoLocation geoLocation;
	
	@JsonProperty("additionalDetails")
	private Object additionalDetails;
	
	//FOR MANIPUR IMPLEMENTATION
	
	@JsonProperty("dagNo")
	@Valid
	@NotEmpty
	@NotNull(message = "Invalid DAG No. ")
	private String dagNo;
	@JsonProperty("districtName")
	private String districtName;
	@JsonProperty("townName")
	private String townName;
	@JsonProperty("wardNo")
	private String wardNo;
	@JsonProperty("village")
	private String village;
	@JsonProperty("pattaNo")
	private String pattaNo;
	@JsonProperty("properHouseNo")
	private String properHouseNo;
	@JsonProperty("commonNameOfBuilding")
	private String commonNameOfBuilding;
	@JsonProperty("principalRoadName")
	private String principalRoadName;
	@JsonProperty("subSideRoadName")
	private String subSideRoadName;
	@JsonProperty("landmarkName")
	private String landmarkName;
	@JsonProperty("others")
	private String others;
	@JsonProperty("typeOfRoad")
	private TypeOfRoad typeOfRoad;
	
}
