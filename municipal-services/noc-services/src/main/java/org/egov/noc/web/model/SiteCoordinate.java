package org.egov.noc.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SiteCoordinate {

	private Long id;
	private BpaApplication application;
	private Integer structureNo;
	private String latitude;
	private String longitude;
	private Double siteElevation;
	private Double buildingHeight;
}