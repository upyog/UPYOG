package org.egov.pgr.web.models;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PgrNotificationSearchCriteria {

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("tenantIds")
	private Set<String> tenantIds;

	@JsonProperty("uuids")
	private Set<String> uuids;

	@JsonProperty("serviceRequestId")
	private Set<String> serviceRequestIds;

	@JsonProperty("applicationStatus")
	private Set<String> applicationStatus;

	@JsonProperty("recipientNames")
	private Set<String> recipientNames;

	@JsonProperty("emailIds")
	private Set<String> emailIds;

	@JsonProperty("mobileNumber")
	private Set<String> mobileNumbers;

	@JsonProperty("isEmailSent")
	private Boolean isEmailSent;

	@JsonProperty("isSmsSent")
	private Boolean isSmsSent;

}
