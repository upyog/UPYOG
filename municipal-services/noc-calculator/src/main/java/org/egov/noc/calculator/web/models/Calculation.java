package org.egov.noc.calculator.web.models;

import java.math.BigDecimal;
import java.util.List;

import org.egov.noc.calculator.web.models.demand.TaxHeadEstimate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation {
    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    @JsonProperty("totalAmount")
    private BigDecimal  totalAmount = null;

    @JsonProperty("tenantId")
    private String tenantId = null;
    
    @JsonProperty("taxHeadEstimates")
	List<TaxHeadEstimate> taxHeadEstimates;
    
    @JsonProperty("taxPeriodFrom")
    private Long taxPeriodFrom;

    @JsonProperty("taxPeriodTo")
    private Long taxPeriodTo;
    
    @JsonProperty("NOC")
    private Noc noc;


}