package org.egov.ewst.models.user;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a response containing user details in the Ewaste application.
 * This class contains the response information and a list of users.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDetailResponse {

	@JsonProperty("responseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("user")
	private List<User> user;
}
