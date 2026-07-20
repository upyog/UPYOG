package org.egov.ndc.calculator.web.models;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingSlabSearchCriteria {


	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("id")
	private List<String> ids;
}
