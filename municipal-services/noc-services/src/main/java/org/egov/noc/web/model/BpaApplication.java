package org.egov.noc.web.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BpaApplication {
	
	private Long id;
	private String uniqueId;
	private String applicationNo;
	private String applicationDate;
	private String applicantName;
	private String applicantAddress;
	private String applicantContact;
	private String applicantEmail;
	private String ownerName;
	private String ownerAddress;
	private String structureType;
	private String structurePurpose;
	private String siteAddress;
	private String siteCity;
	private String siteState;
	private Double plotSize;
	private String isInAirportPremises;
	private String permissionTaken;
	private NocasStatus nocasStatus;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Noc noc;
}
