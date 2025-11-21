package org.upyog.adv.web.models.billing;

import java.math.BigDecimal;

import org.upyog.adv.web.models.AuditDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A object holds a demand and collection values for a tax head and period.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandDetail {

	@JsonProperty("id")
	private String id;

	@JsonProperty("demandId")
	private String demandId;

	@JsonProperty("taxHeadMasterCode")
	private String taxHeadMasterCode;

	@JsonProperty("taxAmount")
	private BigDecimal taxAmount;

	@Default
	@JsonProperty("collectionAmount")
	private BigDecimal collectionAmount = BigDecimal.ZERO;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("tenantId")
	private String tenantId;

}
