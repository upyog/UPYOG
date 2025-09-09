package org.egov.ewst.models.workflow;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response containing process instances in the Ewaste application.
 * This class contains the response information and a list of process instances.
 */
@ApiModel(description = "Process Instance Response")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessInstanceResponse {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("ProcessInstances")
	@Valid
	private List<ProcessInstance> processInstances;

	/**
	 * Adds a process instance to the list of process instances in the response.
	 *
	 * @param proceInstanceItem the process instance to add
	 * @return the updated ProcessInstanceResponse object
	 */
	public ProcessInstanceResponse addProceInstanceItem(ProcessInstance proceInstanceItem) {
		if (this.processInstances == null) {
			this.processInstances = new ArrayList<>();
		}
		this.processInstances.add(proceInstanceItem);
		return this;
	}

}
