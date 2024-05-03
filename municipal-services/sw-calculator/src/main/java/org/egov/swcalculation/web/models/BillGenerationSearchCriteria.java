package org.egov.swcalculation.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BillGenerationSearchCriteria {

	@JsonProperty("locality")
	private String locality;

	@JsonProperty("status")
	private String status;

	@JsonProperty("billingcyclestartdate")
	private Long billingcycleStartdate;

	@JsonProperty("billingcycleenddate")
	private Long billingcycleEnddate;

	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

}
