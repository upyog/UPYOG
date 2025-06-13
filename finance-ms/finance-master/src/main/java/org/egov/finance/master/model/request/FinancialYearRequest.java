package org.egov.finance.master.model.request;

import org.egov.finance.master.model.FinancialYearModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class FinancialYearRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @Valid
    @JsonProperty("FinancialYear")
    private FinancialYearModel financialYear;

}
