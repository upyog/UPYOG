package org.egov.custom.mapper.billing.impl;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Connection {

	private String propertyId;
	
	
	private String oldConnectionNo;
	
	private String status;

	private Object additionalDetails;

}
