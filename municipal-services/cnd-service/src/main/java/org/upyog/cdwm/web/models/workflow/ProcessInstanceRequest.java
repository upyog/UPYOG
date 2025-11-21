package org.upyog.cdwm.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

/**
 * Contract class to receive process instance request.
 */

@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProcessInstanceRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("ProcessInstances")
	@Valid
	@NotNull
	private List<ProcessInstance> processInstances;

	public ProcessInstanceRequest addProcessInstanceItem(ProcessInstance processInstanceItem) {
		if (this.processInstances == null) {
			this.processInstances = new ArrayList<>();
		}
		this.processInstances.add(processInstanceItem);
		return this;
	}

}