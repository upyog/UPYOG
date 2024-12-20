package org.egov.advertisementcanopy.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class AllSiteCountResponse {
	
    private List<Map<String, Object>> countsData;
    
	private long  applicationTotalCount;
}
