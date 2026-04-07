package org.egov.dx.web.models;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.request.User;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserResponse {
	
	@JsonProperty("access_token")
    private String authToken;

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("UserRequest")
    private User user;

}
