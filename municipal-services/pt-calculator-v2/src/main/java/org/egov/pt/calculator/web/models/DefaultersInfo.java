package org.egov.pt.calculator.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultersInfo {

	private String propertyId;
	private String ownerName;
	private String mobileNumber;
	private double dueAmount;
	private String finYear;
	private String rebateEndDate;
	private String tenantId;

}
