package org.egov.ewst.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcessInstanceRequest
 */
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

	/**
	 * Adds a new process instance to the list of process instances.
	 *
	 * @param processInstanceItem The process instance to be added.
	 * @return The current instance of ProcessInstanceRequest.
	 */
	public ProcessInstanceRequest addProcessInstanceItem(ProcessInstance processInstanceItem) {
		if (this.processInstances == null) {
			this.processInstances = new ArrayList<>();
		}
		this.processInstances.add(processInstanceItem);
		return this;
	}

}