package org.egov.advertisementcanopy.model;

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
public class SiteUpdationData {
	
	private Long id;
	private String uuid;
	private String siteId;
	private String siteName;

}
