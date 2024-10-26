package org.egov.advertisementcanopy.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSearchData {

	private String districtName;
	private String ulbName;
	private String wardNumber;
	private String advertisementType;
	private String status;
	private List<String> uuids;
	@JsonProperty("active")
	private boolean isActive;
	private String siteID;
	private List<String> workflowStatus;
	private String tenantId;
}
