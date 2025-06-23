package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.FinancialYearModel;
import org.egov.finance.inbox.model.RequestInfo;

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
