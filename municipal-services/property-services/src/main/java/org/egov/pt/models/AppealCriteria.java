package org.egov.pt.models;

import java.util.Set;

import org.egov.pt.models.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealCriteria {

	private String tenantId;

	private Set<String> propertyIds;

	private Set<String> tenantIds;
	private Set<String> uuids;
	
	private Set<String>acknowledgementNumbers;
	
	private Long offset;

	private Long limit;
}
