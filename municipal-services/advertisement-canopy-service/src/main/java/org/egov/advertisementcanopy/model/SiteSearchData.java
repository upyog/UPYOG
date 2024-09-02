package org.egov.advertisementcanopy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteSearchData {

	private String districtName;
	private String ulbName;
	private String wardNumber;
	private String advertisementType;
	private String status;
	@JsonProperty("active")
	private boolean isActive;
}
