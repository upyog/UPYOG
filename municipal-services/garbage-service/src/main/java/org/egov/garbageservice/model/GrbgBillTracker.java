package org.egov.garbageservice.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrbgBillTracker {

	private String uuid;
	private String grbgApplicationId;
	private String tenantId;
	private String month;
	private String year;
	private String fromDate;
	private String toDate;
	private String ward;
	private String billId;
	private String status = "ACTIVE";
	@Builder.Default
	private String type = "GENERAL";
	private BigDecimal grbgBillAmount;
	private AuditDetails auditDetails;
	@JsonProperty("additionaldetail")
	private JsonNode additionaldetail;
}
