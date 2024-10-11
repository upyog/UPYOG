package org.upyog.employee.dasboard.web.models;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class EmployeeDashboardRequest {

	@Valid
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("moduleName")
	private ModuleName ModuleName;

	@JsonProperty("tenantId")
	private String tenantId;

}
