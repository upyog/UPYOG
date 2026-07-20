package org.egov.garbageservice.model.contract;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for creating a citizen or owner user through the eGov user service.
 * Carries RequestInfo for auth context and an OwnerInfo payload with profile and ownership fields.
 * Built and posted from UserService when garbage accounts need a linked user record.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Setter
public class CreateUserRequest {

	@JsonProperty("requestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("user")
	private OwnerInfo user;

}
