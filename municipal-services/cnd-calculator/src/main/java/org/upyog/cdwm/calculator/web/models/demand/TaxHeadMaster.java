package org.upyog.cdwm.calculator.web.models.demand;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxHeadMaster {

	private String id;

	@NotNull
	private String tenantId;
	@Valid
	@NotNull
	private TaxHeadCategory category;
	@NotNull
	private String service;
	@NotNull
	private String name;

	private String code;

	private Boolean isDebit = false;

	private Boolean isActualDemand;
	@NotNull
	private Long validFrom;
	@NotNull
	private Long validTill;
	
	private Integer order;

	private AuditDetails auditDetail;
	

}
