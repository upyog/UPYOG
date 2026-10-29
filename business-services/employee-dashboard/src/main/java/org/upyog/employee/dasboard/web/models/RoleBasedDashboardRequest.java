package org.upyog.employee.dasboard.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.common.contract.request.RequestInfo;
import lombok.Data;

@Data
public class RoleBasedDashboardRequest {
    @Valid
    @NotNull
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @NotNull
    @JsonProperty("tenantId")
    public String tenantId;
}
