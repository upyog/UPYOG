package org.egov.advertisementcanopy.model;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SiteCreationData {
	private Long id;
	private String siteName;
	private String uuid;
	private String accountId;
	private String siteDescription;
	private String siteID;
	private String gpsLocation;
	private String siteAddress;
	private String siteCost;
	private String sitePhotograph;
	@JsonProperty("structueType")
	private String structure;
	private Long sizeLength;
	private Long sizeWidth;
	private String ledSelection;
	private Long securityAmount;
	@JsonProperty("powerSelection")
	private String powered;
	@JsonProperty("otherDetails")
	private String others;
	private String districtName;
	private String ulbName;
	private String ulbType;
	private String wardNumber;
	private String pinCode;
	private JsonNode additionalDetail;
	private AuditDetails auditDetails;
	private String siteType;
	@JsonProperty("active")
	private boolean isActive;
	private String status;
	private String tenantId;
}
