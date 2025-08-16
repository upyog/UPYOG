package org.egov.ewst.models.user;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request to create a user in the Ewaste application.
 * This class contains the request information and the user details.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Setter
public class CreateUserRequest {

	// Request information associated with the create user request
	@JsonProperty("requestInfo")
	private RequestInfo requestInfo;

	// User details associated with the create user request
	@JsonProperty("user")
	private User user;

}