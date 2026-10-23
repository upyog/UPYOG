package org.egov.echallan.web.models.calculation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.egov.echallan.model.Challan;
import org.egov.echallan.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Calculation
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-27T14:56:03.454+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation {

	@SanitizeHtml
	@JsonProperty("challanNo")
	private String challanNo = null;

	@JsonProperty("challan")
	private Challan challan = null;

	@NotNull
	@SanitizeHtml
	@JsonProperty("tenantId")
	@Size(min = 2, max = 256)
	private String tenantId = null;

	@JsonProperty("taxHeadEstimates")
	List<TaxHeadEstimate> taxHeadEstimates;

}
