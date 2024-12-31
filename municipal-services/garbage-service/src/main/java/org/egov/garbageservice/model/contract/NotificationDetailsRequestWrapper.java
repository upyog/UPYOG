package org.egov.garbageservice.model.contract;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.common.contract.request.RequestInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
