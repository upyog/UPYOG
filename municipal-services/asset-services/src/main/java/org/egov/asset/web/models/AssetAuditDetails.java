package org.egov.asset.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetAuditDetails {
	private String classification;
	private String parentCategory;
	private String category;
	private String subCategory;
	private String tenantId;
}
