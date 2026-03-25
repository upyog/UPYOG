package org.egov.pt.models;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAndRevenueWithProperties {
	private BigInteger total;
	private String type;
	private List<PropertyData> properties;

}
