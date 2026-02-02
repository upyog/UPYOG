package org.egov.pt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AmalgamatedProperty {
	@JsonProperty("tenantId")
	private String tenantId;
	@JsonProperty("propertyId")
	private String propertyId;
	@JsonProperty("property")
	private Property property;
	

}
