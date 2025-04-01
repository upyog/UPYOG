package org.egov.fsm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FSMEvent {
	
	@JsonProperty("fsmRequest")
	private FSMRequest fsmRequest;
}
