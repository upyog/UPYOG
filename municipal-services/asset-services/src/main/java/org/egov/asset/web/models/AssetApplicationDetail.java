package org.egov.asset.web.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetApplicationDetail {
	
	private String applicationNumber;
    private List<String> action;
    private BigDecimal totalPayableAmount;
	private String feeCalculationFormula;
	private Map<Object, Object> billDetails;
	private Map<Object, Object> userDetails;

}
