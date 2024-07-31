package org.egov.wscalculation.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class CancelDemand {

	@JsonProperty("RequestInfo")
	@NotNull
	private RequestInfo requestInfo;
	

	private List<CancelList> CancelList = new ArrayList<>();



	@JsonProperty("tenantId")
    private String tenantId;


    // Getters and setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    
    @JsonProperty("taxPeriodFrom")
    private Long taxPeriodFrom;


    // Getters and setters
    public Long gettaxPeriodFrom() {
        return taxPeriodFrom;
    }

    public void settaxPeriodFrom(Long taxPeriodFrom) {
        this.taxPeriodFrom = taxPeriodFrom;
    }
    
    
    @JsonProperty("taxPeriodTo")
    private Long taxPeriodTo;


    // Getters and setters
    public Long gettaxPeriodTo() {
        return taxPeriodTo;
    }

    public void settaxPeriodTo(Long taxPeriodTo) {
        this.taxPeriodTo = taxPeriodTo;
    }

}
