package org.egov.garbageservice.model;

import java.util.List;

import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * Wrapper for user service responses listing common.contract User entries.
 * Carries ResponseInfo and user list after search or create calls from UserService.
 */
@Builder
public class UserResponse {

	@JsonProperty("responseInfo")
	ResponseInfo responseInfo;

	@JsonProperty("user")
	List<User> user;

}
