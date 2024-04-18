package org.egov.notice.web.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NoticeCriteria {
	
	private Set<String> propertyIds;

	private Set<String> tenantIds;
	
	private Set<String> acknowledgementIds;
	
	private Set<String> noticenumber;
	
	private boolean audit;

}
