package org.egov.wscalculation.web.models;

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
public class SingleDemand {

	@JsonProperty("RequestInfo")
	@NotNull
	private RequestInfo requestInfo;
	
	@JsonProperty("consumercode")
    @NotNull(message = "Consumer code cannot be null")
    @NotEmpty(message = "Consumer code cannot be empty")
    private String consumercode;

    // Getters and setters
    public String getConsumercode() {
        return consumercode;
    }

    public void setConsumercode(String consumercode) {
        this.consumercode = consumercode;
    }

	@JsonProperty("tenantId")
	 @NotNull(message = "tenantId  cannot be null")
    @NotEmpty(message = "tenantId  cannot be empty")
    private String tenantId;

    // Getters and setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

}
