package org.egov.waterquality.web.models.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WaterQuality {
	
	private String id;
	
	private String tenantId;
	
	private String applicationNo;
	
	private ApplicationType type;
	
	private Object applicationDetails = null;
	
	private String createdBy;
	
	private long createdTime;

}
