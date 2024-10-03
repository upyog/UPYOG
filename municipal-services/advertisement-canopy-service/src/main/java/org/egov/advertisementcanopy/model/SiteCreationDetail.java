package org.egov.advertisementcanopy.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteCreationDetail {
	private String siteId;
    private List<String> action;
	private Map<Object, Object> userDetails;

}
