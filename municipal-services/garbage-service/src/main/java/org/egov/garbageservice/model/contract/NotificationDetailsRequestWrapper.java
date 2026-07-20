package org.egov.garbageservice.model.contract;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.common.contract.request.RequestInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bulk wrapper for posting multiple notifications in one call to the notification service.
 * Combines standard RequestInfo with a list of NotificationDetailsRequest entries.
 * JSON uses PascalCase keys (RequestInfo) per eGov API convention.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationDetailsRequestWrapper {

	@JsonProperty("RequestInfo")
	@NotNull
	private RequestInfo requestInfo;

	@NotNull
	private List<NotificationDetailsRequest> notificationDetailsRequests;
}
