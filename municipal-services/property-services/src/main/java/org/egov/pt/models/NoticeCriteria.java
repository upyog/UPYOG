package org.egov.pt.models;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCriteria {

	private Set<String> propertyIds;

	private Set<String> tenantIds;
	
	private Set<String> acknowledgementIds;
	
	private Set<String> noticenumber;
	
	private boolean audit;
	
	private Long offset;

	private Long limit;

}
