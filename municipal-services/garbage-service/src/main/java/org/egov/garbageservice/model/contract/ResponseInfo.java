package org.egov.garbageservice.model.contract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Standard eGov response metadata echoed back from external service calls.
 * apiId, ver, and msgId should match the corresponding RequestInfo on the request.
 * Includes timestamp, response message id, and overall status via ResponseStatusEnum.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseInfo {
	@JsonProperty("apiId")
	@NotNull
	@Size(max = 128)
	@CustomSafeHtml
	private String apiId = null;

	@JsonProperty("ver")
	@NotNull
	@Size(max = 32)
	@CustomSafeHtml
	private String ver = null;

	@JsonProperty("ts")
	@NotNull
	private Long ts = null;

	@JsonProperty("resMsgId")
	@CustomSafeHtml
	private String resMsgId = null;

	@JsonProperty("msgId")
	@Size(max = 256)
	@CustomSafeHtml
	private String msgId = null;

	@JsonProperty("status")
	@NotNull
	private ResponseStatusEnum status = null;
}
