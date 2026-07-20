package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
/**
 * Request wrapper posting Email content to the external email service with RequestInfo.
 * Used for citizen notifications alongside SMS in garbage billing flows.
 */
@Builder
public class EmailRequest {
	private RequestInfo requestInfo;

	private Email email;
}