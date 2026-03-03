package org.egov.pt.models;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceWithProperties {
	private BigInteger total;
	private String type = null;
	private List<PropertyData> properties;

}
