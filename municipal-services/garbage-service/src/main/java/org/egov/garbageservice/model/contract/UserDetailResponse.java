package org.egov.garbageservice.model.contract;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response wrapper returned from user service search or create APIs.
 * Contains ResponseInfo metadata and a list of OwnerInfo records for matched or created users.
 * Parsed in UserService when garbage-service provisions or looks up account owners.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDetailResponse {
	@JsonProperty("responseInfo")
	ResponseInfo responseInfo;

	@JsonProperty("user")
	List<OwnerInfo> user;
}
