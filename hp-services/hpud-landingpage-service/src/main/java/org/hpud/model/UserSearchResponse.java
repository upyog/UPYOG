package org.hpud.model;

import java.util.List;

import org.egov.common.contract.request.User;
//import org.hpud.entity.User;

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
@Builder
public class UserSearchResponse {
	@JsonProperty("responseInfo")
	ResponseInfo responseInfo;

	@JsonProperty("user")
	List<User> user;

}
