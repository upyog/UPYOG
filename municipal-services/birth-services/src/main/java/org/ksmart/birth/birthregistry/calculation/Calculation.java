package org.ksmart.birth.birthregistry.calculation;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.common.calculation.TaxHeadEstimate;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation {

	@JsonProperty("birthCertificateNo")
	private String birthCertificateNo = null;

	@JsonProperty("birthCertificate")
	private BirthCertificate birthCertificate = null;

	@NotNull
	@JsonProperty("tenantId")
	@Size(min = 2, max = 256)
	private String tenantId = null;

	@JsonProperty("taxHeadEstimates")
	List<TaxHeadEstimate> taxHeadEstimates;

}
