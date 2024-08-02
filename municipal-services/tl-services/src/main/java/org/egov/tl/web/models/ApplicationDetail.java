package org.egov.tl.web.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationDetail {
	
	private String applicationNumber;
    private List<String> action;
    private Double totalPayableAmount;
	private String feeCalculationFormula;
	private Map<Object, Object> billDetails;
	private Map<Object, Object> userDetails;

}
