package org.egov.pgr.web.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object to fetch the report data
 */
@ApiModel(description = "Request object to fetch the report data")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceStatusUpdateRequest {

	@NotNull
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@NotNull
	@SafeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@NotNull
	@SafeHtml
	@JsonProperty("serviceRequestId")
	private String serviceRequestId;

	@NotNull
	@SafeHtml
	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	private String resolutionDate;

	@NotNull
	@Valid
	@JsonProperty("workflow")
	private Workflow workflow;

}
