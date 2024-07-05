package org.egov.tl.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTLStatusCriteria {
	
	private String action;
	private String businessId;
	private String tenantId;
	private String comment;

}
