package org.egov.rentlease.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentLease {
	
	private String id;
	private String tenantId;
	private String assetClassification;
	private String parentCategory;
	private String category;
	private String subCategory;
	private AuditDetails auditDetails;

}
